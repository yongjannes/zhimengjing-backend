package com.sf.zhimengjing.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Title: WebConfig
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.config
 * @description: 配置全局跨域规则
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有路径生效
                .allowedOrigins("*") //允许所有源地址
                // .allowedOrigins("https://sf.com","https://sf.com ") // 允许的源地址（数组）
                .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS") // 允许的请求方法
                .allowedHeaders("*"); // 允许的请求头
    }
}
