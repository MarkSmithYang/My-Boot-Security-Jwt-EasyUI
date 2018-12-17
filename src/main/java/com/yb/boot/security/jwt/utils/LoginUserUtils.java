package com.yb.boot.security.jwt.utils;

import com.yb.boot.security.jwt.response.UserDetailsInfo;
import java.util.Set;

/**
 * Description:获取当前登录用户信息的工具类
 * author yangbiao
 * date 2018/12/4
 */
public class LoginUserUtils {
    //把用户信息设置到可以让子线程共享本地变量的ThreaLocal里面去
    private static InheritableThreadLocal<UserDetailsInfo> detailsInfo = new InheritableThreadLocal<>();

    //用户的创建时间哪里因为jwt不好处理,所以这类不提供,如果有需要可通过id去获取

    /**
     * 设置用户信息
     */
    public static void setUserDetailsInfo(UserDetailsInfo userDetailsInfo) {
        detailsInfo.set(userDetailsInfo);
    }

    /**
     * 获取用户信息
     */
    public static UserDetailsInfo getUserDetails() {
        return detailsInfo.get();
    }

    /**
     * 获取用户id
     */
    public static String getUserId() {
        return getUserDetails().getId();
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return getUserDetails().getUsername();
    }

    /**
     * 获取电话
     */
    public static String getPhone() {
        return getUserDetails().getPhone();
    }

    /**
     * 获取头像信息
     */
    public static String getHeadUrl() {
        return getUserDetails().getHeadUrl();
    }

    /**
     * 获取部门信息
     */
    public static String getDepartment() {
        return getUserDetails().getDepartment();
    }

    /**
     * 获取职位
     */
    public static String getPosition() {
        return getUserDetails().getPosition();
    }

    /**
     * 获取角色
     */
    public static Set<String> getRoles() {
        return getUserDetails().getRoles();
    }

    /**
     * 获取权限
     */
    public static Set<String> getPermissions() {
        return getUserDetails().getPermissions();
    }

    /**
     * 获取模块
     */
    public static Set<String> getModules() {
        return getUserDetails().getModules();
    }

    /**
     * 获取客户端--来源(前台或后台等)
     */
    public static String getFrom() {
        return getUserDetails().getFrom();
    }

    /**
     * 获取用户IP
     */
    public static String getIp() {
        return getUserDetails().getIp();
    }

}
