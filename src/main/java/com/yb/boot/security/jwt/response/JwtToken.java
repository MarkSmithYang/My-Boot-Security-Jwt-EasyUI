package com.yb.boot.security.jwt.response;

import java.io.Serializable;

/**
 * Description:登录成功返回的数据封装类
 * author yangbiao
 * date 2018/11/30
 */
public class JwtToken implements Serializable {
    private static final long serialVersionUID = -5679643008444921620L;

    /**
     * 访问用token
     */
    public String accessToken;

    /**
     * 刷新用token
     */
    public String refreshToken;

    /**
     * token过期时间
     */
    public int tokenExpire;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getTokenExpire() {
        return tokenExpire;
    }

    public void setTokenExpire(int tokenExpire) {
        this.tokenExpire = tokenExpire;
    }
}
