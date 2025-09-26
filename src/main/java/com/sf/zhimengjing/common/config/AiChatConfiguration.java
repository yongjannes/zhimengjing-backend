package com.sf.zhimengjing.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: AiChatConfiguration
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.config
 * @description: AI 聊天相关配置类，集中配置 ChatClient、向量库、记忆等组件
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiChatConfiguration {

    /**
     * 配置会话记忆存储（ChatMemoryRepository）
     * 使用自定义 RedisChatMemoryRepository，把消息持久化到 Redis
     *
     * @param stringRedisTemplate Redis 客户端
     * @param objectMapper Jackson 对象序列化工具
     * @return ChatMemoryRepository Redis 实现的会话记忆存储
     */
    @Bean
    public ChatMemoryRepository chatMemoryRepository(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisChatMemoryRepository(
                stringRedisTemplate,
                objectMapper,
                "chat:",          // 消息前缀
                "chat:ids:",      // 会话ID集合前缀
                60 * 60 * 24      // 消息过期时间：24 小时
        );
    }

    /**
     * ChatClient工厂，用于管理不同模型的ChatClient实例
     */
    @Bean
    public ChatClientFactory chatClientFactory(ChatMemoryRepository chatMemoryRepository) {
        return new ChatClientFactory(chatMemoryRepository);
    }

    /**
     * ChatClient工厂实现类
     * 为不同的模型创建和缓存ChatClient实例，每个都使用统一的记忆管理
     */
    @Component
    public static class ChatClientFactory {

        private final ChatMemoryRepository chatMemoryRepository;
        private final Map<String, ChatClient> clientCache = new ConcurrentHashMap<>();

        public ChatClientFactory(ChatMemoryRepository chatMemoryRepository) {
            this.chatMemoryRepository = chatMemoryRepository;
        }

        /**
         * 获取指定模型的ChatClient，支持自定义系统提示词
         *
         * @param modelCode 模型编码，用作缓存key
         * @param chatModel 聊天模型实例
         * @param systemPrompt 系统提示词
         * @return ChatClient实例
         */
        public ChatClient getChatClient(String modelCode, ChatModel chatModel, String systemPrompt) {
            // 使用modelCode + systemPrompt的hash作为缓存key，确保不同系统提示词的ChatClient被分别缓存
            String cacheKey = modelCode + ":" + (systemPrompt != null ? systemPrompt.hashCode() : "default");

            return clientCache.computeIfAbsent(cacheKey, key -> {
                log.info("创建新的ChatClient实例，cacheKey={}", key);
                return buildChatClient(chatModel, systemPrompt);
            });
        }

        /**
         * 获取默认的ChatClient（无自定义系统提示词）
         */
        public ChatClient getChatClient(String modelCode, ChatModel chatModel) {
            return getChatClient(modelCode, chatModel, null);
        }

        /**
         * 构建ChatClient实例
         */
        private ChatClient buildChatClient(ChatModel chatModel, String systemPrompt) {
            // 构建会话记忆对象，设置窗口最大保留 20 条消息
            ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .chatMemoryRepository(chatMemoryRepository)
                    .maxMessages(20)
                    .build();

            ChatClient.Builder builder = ChatClient.builder(chatModel)
                    .defaultAdvisors(
                            new SimpleLoggerAdvisor(),                         // 日志打印
                            MessageChatMemoryAdvisor.builder(chatMemory).build() // 对话记忆
                    );

            // 如果有自定义系统提示词，则设置
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                builder.defaultSystem(systemPrompt);
            }

            return builder.build();
        }

        /**
         * 清除指定模型的缓存
         */
        public void clearCache(String modelCode) {
            log.info("清除模型ChatClient缓存，modelCode={}", modelCode);
            clientCache.entrySet().removeIf(entry -> entry.getKey().startsWith(modelCode + ":"));
        }

        /**
         * 清除所有缓存
         */
        public void clearAllCache() {
            log.info("清除所有ChatClient缓存");
            clientCache.clear();
        }

        /**
         * 获取当前缓存的ChatClient数量
         */
        public int getCacheSize() {
            return clientCache.size();
        }
    }
}
