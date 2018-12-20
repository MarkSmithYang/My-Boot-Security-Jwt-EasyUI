package com.yb.boot.security.jwt.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yb.boot.security.jwt.common.CommonDic;
import com.yb.boot.security.jwt.exception.ParameterErrorException;
import com.yb.boot.security.jwt.repository.SysUserRepository;
import com.yb.boot.security.jwt.request.AddUserModel;
import com.yb.boot.security.jwt.request.UserRequest;
import com.yb.boot.security.jwt.auth.other.CustomAuthenticationProvider;
import com.yb.boot.security.jwt.auth.other.MyUsernamePasswordAuthenticationToken;
import com.yb.boot.security.jwt.auth.tools.JwtTokenTools;
import com.yb.boot.security.jwt.common.JwtProperties;
import com.yb.boot.security.jwt.model.SysUser;
import com.yb.boot.security.jwt.model.UserInfo;
import com.yb.boot.security.jwt.response.JwtToken;
import com.yb.boot.security.jwt.response.UserDetailsInfo;
import com.yb.boot.security.jwt.utils.LoginUserUtils;
import com.yb.boot.security.jwt.utils.RealIpGetUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Description:服务层代码
 * author yangbiao
 * date 2018/11/30
 */
@Service
public class SecurityJwtService {
    public static final Logger log = LoggerFactory.getLogger(SecurityJwtService.class);

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    /**
     * 用户的登录认证
     */
    public JwtToken authUser(UserRequest userRequest, String from, HttpServletResponse response,
                             HttpServletRequest request) {
        //获取获取到的用户名和密码
        String username = userRequest.getUsername();
        String password = userRequest.getPassword();
        //构造Token类
        UsernamePasswordAuthenticationToken userToken = new MyUsernamePasswordAuthenticationToken(username, password, from);
        //调用自定义的用户认证Provider认证用户---(可以不使用自定义的这个认证,直接在过滤器那里处理--个人觉得)
        Authentication authenticate = customAuthenticationProvider.authenticate(userToken);
        //获取并解析封装在Authentication里的sysUser信息
        SysUser sysUser = JSONObject.parseObject((String) authenticate.getCredentials(), SysUser.class);
        //把认证信息存储安全上下文--(把密码等敏感信息置为null)
        authenticate = new UsernamePasswordAuthenticationToken(authenticate.getPrincipal(),
                null, authenticate.getAuthorities());
        //把构造的没有密码的信息放进安全上下文
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        //封装sysUser到UserDetailsInfo
        if (sysUser == null) {
            log.info("sysUser通过密码参数传递过来解析出来为空");
            ParameterErrorException.message("用户名或密码错误");
        }
        //封装数据
        UserDetailsInfo detailsInfo = setUserDetailsInfo(sysUser, request);
        //生成token
        String accessToken = JwtTokenTools.createAccessToken(detailsInfo, jwtProperties.getExpireSeconds(), response, jwtProperties);
        String refreshToken = JwtTokenTools.createAccessToken(detailsInfo, jwtProperties.getExpireSeconds() * 7, response, jwtProperties);
        //封装token返回
        JwtToken jwtToken = new JwtToken();
        jwtToken.setAccessToken(CommonDic.TOKEN_PREFIX + accessToken);
        jwtToken.setRefreshToken(CommonDic.TOKEN_PREFIX + refreshToken);
        jwtToken.setTokenExpire(jwtProperties.getExpireSeconds());
        //填充数据到LoginUserUtils,供其他的子线程共享信息
        LoginUserUtils.setUserDetailsInfo(detailsInfo);
        //把token存储到redis,用来判断前后端未分离请求的合法性,因为没有在请求头里放置token,所以
        //需要去redis获取token来验证是否已经登录,设置30分钟的过期时间,在此期间访问都不需要登录
        String ipAddress = RealIpGetUtils.getIpAddress(request);
        redisTemplate.opsForValue().set(CommonDic.LOGIN_SUCCESS_TOKEN + ipAddress, CommonDic.TOKEN_PREFIX +
                accessToken, CommonDic.TOKEN_EXPIRE, TimeUnit.MINUTES);
        //返回数据
        return jwtToken;
    }

    /**
     * 封装用户详情信息(角色权限部门电话等等信息)封装并存入redis里(from作为拼接的字符串)
     */
    public UserDetailsInfo setUserDetailsInfo(SysUser sysUser, HttpServletRequest request) {
        //实例化封装用户信息的类
        UserDetailsInfo detailsInfo = new UserDetailsInfo();
        //获取用户基本详细信息
        UserInfo userInfo = sysUser.getUserInfo();
        if (userInfo != null) {
            //封装用户基本详信息
            detailsInfo.setDepartment(userInfo.getDepartment());
            detailsInfo.setPhone(userInfo.getPhone());
            detailsInfo.setPosition(userInfo.getPosition());
        } else {
            log.info("用户的基本详细信息UserInfo信息为空");
        }
        //获取用户ip信息
        String ipAddress = RealIpGetUtils.getIpAddress(request);
        //封装用户基础信息
        detailsInfo.setId(sysUser.getId());
        detailsInfo.setCreateTime(sysUser.getCreateTime());
        detailsInfo.setHeadUrl(sysUser.getHeadUrl());
        detailsInfo.setUsername(sysUser.getUsername());
        detailsInfo.setIp(ipAddress);
        detailsInfo.setFrom(sysUser.getUserFrom());
        //获取权限角色的集合
        Set<String> permissions = detailsInfo.getPermissions();
        Set<String> roles = detailsInfo.getRoles();
        Set<String> modules = detailsInfo.getModules();
        //封装用户的权限角色信息
        if (CollectionUtils.isNotEmpty(sysUser.getPermissions())) {
            sysUser.getPermissions().forEach(s -> permissions.add(s.getPermission()));
        }
        //封装用户角色以及它的权限
        if (CollectionUtils.isNotEmpty(sysUser.getRoles())) {
            sysUser.getRoles().forEach(a -> {
                //封装角色信息
                roles.add(a.getRole());
                //封装角色的权限信息
                if (CollectionUtils.isNotEmpty(a.getPermissions())) {
                    a.getPermissions().forEach(d -> permissions.add(d.getPermission()));
                }
            });
        }
        //封装用户的模块以及它的权限
        if (CollectionUtils.isNotEmpty(sysUser.getModules())) {
            sysUser.getModules().forEach(f -> {
                //封装模块信息
                modules.add(f.getModule());
                //封装模块的权限信息
                if (CollectionUtils.isNotEmpty(f.getPermissions())) {
                    f.getPermissions().forEach(g -> permissions.add(g.getPermission()));
                }
            });
        }
        return detailsInfo;
    }
    //-------------------------------------------------------------------------------------------------------

    /**
     * 添加用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUser(AddUserModel userRegister) {
        //封装用户基本信息--没有弄头像信息
        SysUser sysUser = new SysUser();
        //调用抽取方法处理信息
        insertUser(userRegister, sysUser);
    }

    /**
     * 添加用户抽取代码
     */
    public void insertUser(AddUserModel userRegister, SysUser sysUser) {
        if (sysUser == null) {
            log.info("insertUser方法里的sysUser为空");
            ParameterErrorException.message("操作失败");
        }
        //封装数据
        sysUser.setUserFrom(userRegister.getFrom());
        sysUser.setUsername(userRegister.getUsername());
        //封装用户基础信息
        UserInfo userInfo = null;
        //判断sysUser里的userInfo是否为空
        if (sysUser.getUserInfo() == null) {
            userInfo = new UserInfo();
        } else {
            userInfo = sysUser.getUserInfo();
        }
        //封装数据
        userInfo.setDepartment(userRegister.getDepartment());
        userInfo.setPhone(userRegister.getPhone());
        userInfo.setPosition(userRegister.getPosition());
        //这一步特别重要,不做此步,userInfo的外键就是null(实测),先相互set的顺序并不影响添加
        //建议要保存的放在最后set其他的对象,如下sysUser-->实测只需只有id的对象即可,这样可以
        //减少封装,还可以避免反复嵌套让对象显得太笨重
        userInfo.setSysUser(new SysUser(sysUser.getId()));
        //把用户基础信息放进用户基本信息
        sysUser.setUserInfo(userInfo);
        //因为这里只是添加一些用户的基础信息,所以不需要处理权限角色等
        try {
            sysUserRepository.save(sysUser);
        } catch (Exception e) {
            try {
                sysUserRepository.save(sysUser);
            } catch (Exception e1) {
                log.info("用户信息第二次保存失败=" + e1.getMessage());
                //抛出异常-->回滚事务
                ParameterErrorException.message("操作失败");
            }
        }
    }

    /**
     * 查询用户信息列表(因为数据少,未分页)
     */
    public JSONObject queryUserList(int page, int rows, String username) {
        //构建Pageable
        Pageable pageable = PageRequest.of(page - 1, rows, new Sort(Sort.Direction.DESC, "createTime"));
        //查询数据
        Page<SysUser> all = sysUserRepository.findAll((root, cq, cb) -> {
            Predicate predicate = cb.conjunction();
            List<Expression<Boolean>> expressions = predicate.getExpressions();
            if (StringUtils.isNotBlank(username)) {
                expressions.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            return predicate;
        }, pageable);
        //封装需要的数据到UserDetailsInfo
        JSONArray array = new JSONArray();
        if (CollectionUtils.isNotEmpty(all.getContent())) {
            all.getContent().forEach(s -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", s.getId());
                jsonObject.put("username", s.getUsername());
                jsonObject.put("department", s.getUserInfo() == null ? null : s.getUserInfo().getDepartment());
                jsonObject.put("position", s.getUserInfo() == null ? null : s.getUserInfo().getPosition());
                jsonObject.put("phone", s.getUserInfo() == null ? null : s.getUserInfo().getPhone());
                //翻译from为中文展示
                if (CommonDic.FROM_FRONT.equals(s.getUserFrom())) {
                    jsonObject.put("from", "前台");
                } else if (CommonDic.FROM_BACK.equals(s.getUserFrom())) {
                    jsonObject.put("from", "后台");
                } else {
                    jsonObject.put("from", null);
                }
                jsonObject.put("headUrl", s.getHeadUrl());
                array.add(jsonObject);
            });
        }
        //封装需要的数据结构
        JSONObject json = new JSONObject();
        json.put("total", all.getTotalElements());
        json.put("rows", array);
        return json;
    }

    /**
     * 通过用户id查询用户信息
     */
    public SysUser findUserById(String id) {
        Optional<SysUser> result = sysUserRepository.findById(id);
        return result.isPresent() ? result.get() : null;
    }

    /**
     * 根据用户id修改在前端页面展示的信息(因为这个和添加的时候是一样的,其他的信息是添加不了的)
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(@Valid AddUserModel addUserModel) {
        //判断id是否为空(因为添加的时候前端是不会传id的,所以没有非空判断)
        if(StringUtils.isBlank(addUserModel.getId())){
            log.info("编辑(修改)数据的id为空");
            ParameterErrorException.message("操作失败");
        }
        //获取id查询用户信息确认用户存在再修改
        Optional<SysUser> byId = sysUserRepository.findById(addUserModel.getId());
        //调用抽取的添加用户的方法处理信息
        insertUser(addUserModel, byId.isPresent() ? byId.get() : null);
    }

    /**
     * 根据用户id删除用户相关信息
     */
    public void deleteUser(String ids) {
        //处理id(拼接的多个id),id的非空判断已经由注解实现
        String[] split = ids.split(",");
        //因为不管字符串含不含有逗号,都不会报错,只要字符串不空,切割的数组也不能空,所以
        List<String> list = Arrays.asList(split);
        //通过id查询用户是否存在,存在则删除
        List<SysUser> users = sysUserRepository.findByIdIn(list);
        //判断id是否都能查询出用户
        if (CollectionUtils.isNotEmpty(users) && list.size() == users.size()) {
            //说明传递过来的id都是正确的--删除信息
            try {
                sysUserRepository.deleteAll(users);
            } catch (Exception e) {
                try {
                    sysUserRepository.deleteAll(users);
                } catch (Exception e1) {
                    log.info("删除失败=" + e1.getMessage());
                    ParameterErrorException.message("操作失败");
                }
            }
        }
    }

}
