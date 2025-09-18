package com.sf.zhimengjing.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Title: RedisConfig
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.config
 * @description: redis序列化方式
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // 设置key和value的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 设置key的序列化器为StringRedisSerializer
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer()); // 设置value的序列化器为JdkSerializationRedisSerializer
        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); // 设置hash key的序列化器为StringRedisSerializer
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer()); // 设置hash value的序列化器为JdkSerializationRedisSerializer
        redisTemplate.afterPropertiesSet(); // 初始化RedisTemplate
        return redisTemplate; // 返回配置好的RedisTemplate
    }
}
