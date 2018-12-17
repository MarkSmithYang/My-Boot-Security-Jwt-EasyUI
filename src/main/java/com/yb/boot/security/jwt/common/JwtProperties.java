package com.yb.boot.security.jwt.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
  * Description: jwt的信息封装类
  * author yangbiao
  * date 2018/11/21
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * jwt发布者
     */
    private String iss;

    /**
     * jwt接收方
     */
    private String aud;

    /**
     * 签名秘钥
     */
    private String secret;

    /**
     * 过期时间-毫秒
     */
    private int expireSeconds;

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
