# 梦境解析应用后端服务（zhimengjing-backend）

## 项目介绍

`zhimengjing-backend`是“梦境解析应用”的后端服务，为Web管理后台和C端小程序提供全面的数据接口和业务逻辑支持。该项目采用Spring Boot分层架构，旨在实现一个功能完备、可扩展的梦境分析平台。

## 技术栈

根据 `pom.xml` 和项目代码，本项目已确认使用的核心技术包括：

- **核心框架**: Spring Boot 3.3.0
- **数据库**: MySQL 8.0+
- **持久层**: MyBatis Plus
- **缓存**: Redis 6.0+
- **安全框架**: Spring Security
- **认证机制**: JWT（JSON Web Token）
- **API文档**: Knife4j (Swagger 3.0)
- **工具库**: Lombok, Hutool
- **日志**: Logback + SLF4J

此外，根据设计文档，项目规划中还将集成以下技术：

- **AI集成**: Spring AI（用于集成OpenAI、文心一言等大模型）
- **异步通信**: RabbitMQ
- **文件存储**: 阿里云OSS或MinIO
- **全文搜索**: Elasticsearch

## 项目结构

```
zhimengjing-backend/
├── src/
│   ├── main/
│   │   ├── java/com/sf/zhimengjing/
│   │   │   ├── common/              # 通用代码（配置、常量、异常、工具等）
│   │   │   ├── controller/          # 接口控制器
│   │   │   ├── entity/              # 数据库实体类
│   │   │   ├── handler/             # MyBatis自动填充处理器
│   │   │   ├── mapper/              # 数据访问层
│   │   │   └── service/             # 服务层（业务逻辑）
│   │   └── resources/
│   │       ├── application.yml      # 应用主配置
│   │       ├── application-dev.yml  # 开发环境配置
│   │       ├── mapper/              # MyBatis XML映射文件
│   │       └── ...
│   └── test/                        # 测试代码
├── pom.xml                          # Maven依赖管理文件
└── README.md
```

## 已实现的核心功能与组件

- **统一结果封装**：通过 `Result` 类和 `ResultAdvice` 切面实现了统一的API响应格式，简化了控制器的代码。
- **全局异常处理**：`GlobalExceptionHandler` 捕获并处理全局异常，为客户端返回友好的错误提示，提高了系统的健壮性。
- **认证与授权**：已配置Spring Security，并实现了 `JwtAuthenticationTokenFilter` 过滤器来验证和处理JWT令牌，确保了API的安全性。
- **重复提交拦截**：通过自定义注解 `@RepeatSubmit` 和切面 `RepeatSubmitAspect`，实现了防止表单重复提交的功能。
- **数据库集成**：使用了MyBatis Plus作为ORM框架，并配置了分页插件。`User`实体和`UserMapper`接口已经定义，为用户模块提供了数据基础。
- **工具类**: 提供了 `JwtUtils` 来处理 JWT 令牌的生成和解析，以及 `CodeGenerator` 用于自动化生成代码。

## 快速开始



1. **环境准备**: 确保已安装 JDK 17+, Maven, MySQL, Redis。
2. **配置**: 修改 `src/main/resources/application.yml` 和 `application-dev.yml` 中的数据库、Redis等连接信息。
3. **启动**: 在IDE中运行 `zhimengjingApplication.java`。
4. **接口文档**: 项目启动后，访问 `http://localhost:8888/doc.html` 查看API文档。