package com.cybertunnel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 安检岗位部署图 —— 告诉 Spring 哪些请求要过 AuthInterceptor
 *
 * 规则：
 *  - /api/**           所有业务接口都过安检（必须带有效 token）
 *  - /api/users/register  放行：注册前还没有账号，当然不能要求已登录
 *  - /api/users/login     放行：登录就是为了拿 token，必须先能进来
 *  - 静态页面(html/css/图片)不在 /api 下，天然不受影响
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/users/register",
                        "/api/users/login"
                );
    }
}
