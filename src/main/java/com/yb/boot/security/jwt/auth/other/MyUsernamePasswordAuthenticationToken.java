package com.yb.boot.security.jwt.auth.other;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * author yangbiao
 * Description:重写security的token类,添加一个字段来判断登录者是前台登录还是后台登录用户
 * date 2018/11/30
 */
public class MyUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = -1948491632281558748L;

    /**
     * 来自什么用户的登录
     */
    private String from;

    public MyUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public MyUsernamePasswordAuthenticationToken(Object principal, Object credentials, String from) {
        super(principal, credentials);
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
