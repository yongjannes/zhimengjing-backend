package com.sf.zhimengjing.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Title: Knife4jConfig
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.config
 * @description: Knife4j API文档增强配置 (最终更正版)
 */
@Configuration
public class Knife4jConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";
    private static final String API_BASE_PACKAGE = "com.sf.zhimengjing.controller";

    /**
     * OpenAPI 基础信息配置
     * @return OpenAPI 配置对象
     */
    @Bean
    public OpenAPI springShopOpenApi() {
        return new OpenAPI()
                .info(new Info().title("织梦境 - 项目接口文档")
                        .description("欢迎来到“织梦境”，一个集梦境记录、AI智能解析、数据洞察与社区分享于一体的综合性平台。")
                        .version("v1.0.0")
                        .termsOfService("https://github.com/yongjannes")
                        .contact(new Contact().name("殇枫")
                                .email("yongjannes@gmail.com")));
    }

    /**
     * 分组一：公共接口 (无需认证)
     * 包含登录、验证码等所有在SecurityConfig中放行的路径
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .displayName("01. 公共接口 (Public)")
                .group("01-public-api")
                // **指定要扫描的Controller包**
                .packagesToScan(API_BASE_PACKAGE)
                // 匹配白名单路径
                .pathsToMatch(
                        "/user/login",
                        "/captcha/**",
                        "/api/file/**"
                )
                .build();
    }

    /**
     * 分组二：用户核心功能接口 (需要认证)
     * 对应文档中的C端核心业务
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .displayName("02. 用户核心功能 (User)")
                .group("02-user-api")
                // **指定要扫描的Controller包**
                .packagesToScan(API_BASE_PACKAGE)
                .pathsToMatch(
                        "/user/**",
                        "/dream/**",
                        "/report/**",
                        "/community/**",
                        "/reminder/**",
                        "/vip/**",
                        "/api/**"
                )
                // 排除已经分到公共组的接口
                .pathsToExclude("/user/login")
                .build();
    }

    /**
     * 分组三：后台管理系统接口 (需要Admin权限认证)
     * 对应文档中的B端后台管理功能
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .displayName("03. 后台管理系统 (Admin)")
                .group("03-admin-api")
                // **指定要扫描的Controller包，包括其子包**
                .packagesToScan(API_BASE_PACKAGE)
                // 匹配所有 /admin/ 开头的路径
                .pathsToMatch("/admin/**")
                .build();
    }
}