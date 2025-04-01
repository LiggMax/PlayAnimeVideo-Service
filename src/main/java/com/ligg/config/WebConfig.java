package com.ligg.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 配置跨域
    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**") // 允许跨域访问的URL模式
                .allowedOrigins("http://localhost:3000") // 仅允许 http://localhost:3000 的请求
                .allowedHeaders("*") // 允许任何头
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowCredentials(true) // 允许cookies跨域
                .maxAge(3600) // 缓存时间，单位秒
                .allowedHeaders("*"); // 允许任何请求头
    }
}
