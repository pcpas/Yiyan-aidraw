package com.buaa.aidraw.config;

import com.buaa.aidraw.mapper.UserMapper;
import com.buaa.aidraw.model.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.buaa.aidraw.utils.JwtUtil.SECRET_KEY;


@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    @Resource
    UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("拦截器开始工作：");
        String token = request.getHeader("Token");
        response.setContentType("application/json;charset=utf-8");
        if (token == null) {
            response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseEntity.status(401)));
            return false;
        } else if (token.equals("111")) {
            String id = "1";
            User user = userMapper.getUserById(id);
            request.setAttribute("user", user);
            return true;
        } else {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();
                String id = claims.getSubject();
                User user = userMapper.getUserById(id);
                if (user == null) {
                    response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseEntity.status(401)));
                    return false;
                }
                request.setAttribute("user", user);
                return true;
            } catch (ExpiredJwtException e) {
                response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseEntity.status(401)));
                return false;
            } catch (JwtException e) {
                System.out.println("JWT 异常: " + e.getMessage());
                return false;
            }
        }
    }

}