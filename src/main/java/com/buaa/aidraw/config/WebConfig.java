package com.buaa.aidraw.config;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public JWTInterceptor jwtInterceptor() {
        return new JWTInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("开始注册自定义拦截器...");
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/user/**",
                        "/notifications/**",
                        "/ai/**",
                        "/element/**")
                .excludePathPatterns(
                        "/user/register",
                        "/user/login",
                        "/notifications/publish");
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedHeaders("*")
//                .allowCredentials(false) // 设置为 false，或者干脆不设置，因为默认值就是 false
//                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
//                .maxAge(3600);
//    }

    /*
    @Configuration
public class ProviderMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")// 项目中的所有接口都支持跨域
          .allowedOrigins("http://localhost:8088") //允许哪些域能访问我们的跨域资源
          .allowedMethods("*")//允许的访问方法"POST", "GET", "PUT", "OPTIONS", "DELETE"等
          .allowedHeaders("*");//允许所有的请求header访问，可以自定义设置任意请求头信息
    }
}
     */
}