package com.yb.boot.security.jwt.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Description:用户信息展示给前端的封装类
 * author yangbiao
 * date 2018/11/30
 */
public class UserDetailsInfo implements Serializable {
    private static final long serialVersionUID = 4313694248931257246L;

    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像信息
     */
    private String headUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户部门
     */
    private String department;

    /**
     * 用户职位
     */
    private String position;

    /**
     * 用户电话
     */
    private String phone;

    /**
     * 用户IP
     */
    private String ip;

    /**
     * 用户类型(前台或后台等)
     */
    private String from;

    /**
     * 用户权限
     */
    private Set<String> permissions = new HashSet<>();

    /**
     * 用户角色
     */
    private Set<String> roles = new HashSet<>();//实例化后可以直接get使用

    /**
     * 用户的模块(菜单)信息
     */
    private Set<String> modules = new HashSet<>();

    @Override
    public String toString() {
        return "UserDetailsInfo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", createTime=" + createTime +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", phone='" + phone + '\'' +
                ", ip='" + ip + '\'' +
                ", from='" + from + '\'' +
                ", permissions=" + permissions +
                ", roles=" + roles +
                ", modules=" + modules +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getModules() {
        return modules;
    }

    public void setModules(Set<String> modules) {
        this.modules = modules;
    }
}
