package com.gduf.config;

import com.gduf.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/community/**")
//                .addPathPatterns("/script/**")
//                .addPathPatterns("/users/**")
//                .excludePathPatterns("/users/login")
//                .excludePathPatterns("/users/register");
    }
}
