package com.sf.zhimengjing.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 保留项目中原有的 RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // 使用String序列化key
        template.setKeySerializer(new StringRedisSerializer());
        // 使用我们配置的 ObjectMapper 进行序列化
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
        return template;
    }

    /**
     * 新增 StringRedisTemplate 的 Bean，专门用于处理字符串类型的 Redis 操作
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * [核心修正] 将此 ObjectMapper 定义为项目中的主要 ObjectMapper。
     * 这样，整个应用程序（包括登录日志记录）在需要进行JSON序列化时，
     * 都会使用这个正确配置的实例，从而解决 LocalDateTime 的序列化问题。
     * @return ObjectMapper 实例
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册 Java 8 的时间模块，以支持 LocalDateTime 等类型的序列化和反序列化
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}