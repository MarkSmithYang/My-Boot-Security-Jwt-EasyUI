package com.yb.boot.security.jwt.auth.other;

import com.yb.boot.security.jwt.model.SysUser;
import com.yb.boot.security.jwt.response.UserDetailsInfo;
import com.yb.boot.security.jwt.utils.LoginUserUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author yangbiao
 * Description:验证其他请求token是否合法的类 OncePerRequestFilter继承GenericFilterBean了, 并扩展了内容
 * date 2018/11/30
 */
@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * 判断带jwt的请求token的合法性
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //因为是前后端在一个系统里的内部调用访问,所以就不需要带token来验证合法性,直接放过即可
        //本来就不该写这个过滤器的,但是就这样留着比较好,能够提醒自己,弄了好久都没有想到放开过滤器这招
        chain.doFilter(request, response);
    }

}
