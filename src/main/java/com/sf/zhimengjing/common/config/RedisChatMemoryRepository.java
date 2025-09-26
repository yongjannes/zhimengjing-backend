package com.sf.zhimengjing.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sf.zhimengjing.common.util.SecurityUtils;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.content.Media;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Title: RedisChatMemoryRepository
 * @Author 殇枫
 * @Package com.sf.springtemplate.common.config
 * @description: 基于 Redis 的支持用户隔离的聊天记忆仓库
 */
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final String prefix;
    private final String conversationIdsSetPrefix;
    private final long timeout;

    /**
     * 构造函数
     *
     * @param redisTemplate          操作 Redis 的模板
     * @param objectMapper           JSON 序列化工具
     * @param prefix                 聊天记录的 Key 前缀
     * @param conversationIdsSetPrefix 会话ID集合的 Key 前缀
     * @param timeoutSeconds         过期时间（秒）
     */
    public RedisChatMemoryRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper,
                                     String prefix, String conversationIdsSetPrefix, long timeoutSeconds) {
        this.stringRedisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.prefix = prefix.endsWith(":") ? prefix : prefix + ":";
        this.conversationIdsSetPrefix = conversationIdsSetPrefix.endsWith(":") ? conversationIdsSetPrefix : conversationIdsSetPrefix + ":";
        this.timeout = timeoutSeconds;
    }

    // ================== ChatMemoryRepository 接口实现 ==================

    @Override
    public List<String> findConversationIds() {
        // 1、安全地获取当前用户ID
        String userId = String.valueOf(SecurityUtils.getUserId());
        // 2、调用用户隔离的方法
        return findConversationIds(userId);
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        // 1、从复合 conversationId 中解析出 userId 和 真实的 conversationId
        String[] parts = conversationId.split(":", 2);
        if (parts.length < 2) return List.of();
        String userId = parts[0];
        String actualConversationId = parts[1];
        // 2、调用用户隔离的方法
        return findByConversationId(userId, actualConversationId);
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        // 1、从复合 conversationId 中解析出 userId 和 真实的 conversationId
        String[] parts = conversationId.split(":", 2);
        if (parts.length < 2) return;
        String userId = parts[0];
        String actualConversationId = parts[1];
        // 2、调用用户隔离的方法
        saveAll(userId, actualConversationId, messages);
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        // 1、从复合 conversationId 中解析出 userId 和 真实的 conversationId
        String[] parts = conversationId.split(":", 2);
        if (parts.length < 2) return;
        String userId = parts[0];
        String actualConversationId = parts[1];
        // 2、调用用户隔离的方法
        deleteByConversationId(userId, actualConversationId);
    }

    // ================== 用户隔离方法 ==================

    /**
     * 根据用户ID查找其所有的会话ID
     *
     * @param userId 用户ID
     * @return 会话ID列表
     */
    public List<String> findConversationIds(String userId) {
        // 1、校验用户ID
        checkUserId(userId);
        // 2、从Redis的ZSet中按分数（时间戳）倒序获取所有会话ID
        Set<String> conversationIds = stringRedisTemplate.opsForZSet()
                .reverseRange(conversationIdsSetPrefix + userId, 0, -1);
        // 3、处理Redis返回null的情况
        return conversationIds == null ? List.of() : new ArrayList<>(conversationIds);
    }

    /**
     * 根据用户ID和会话ID查找聊天记录
     *
     * @param userId         用户ID
     * @param conversationId 会话ID
     * @return 消息列表
     */
    public List<Message> findByConversationId(String userId, String conversationId) {
        // 1、校验ID
        checkUserIdAndConversationId(userId, conversationId);
        // 2、从Redis的List中获取该会话的所有消息（JSON格式）
        List<String> list = stringRedisTemplate.opsForList()
                .range(prefix + userId + ":" + conversationId, 0, -1);
        // 3、如果列表为空，直接返回空列表
        if (list == null || list.isEmpty()) return List.of();
        // 4、将JSON字符串列表通过流操作，反序列化为Message对象列表
        return list.stream()
                .map(json -> {
                    try {
                        return deserializeMessage(json);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 在现有会话末尾追加消息
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @param messages 要追加的消息列表
     */
    public void add(String userId, String conversationId, List<Message> messages) {
        // 1、校验ID
        checkUserIdAndConversationId(userId, conversationId);
        // 2、如果消息列表为空，则不执行任何操作
        if (messages == null || messages.isEmpty()) {
            return;
        }

        // 3、拼接用于存储消息列表的Redis Key
        String conversationKey = prefix + userId + ":" + conversationId;

        // 4、将Message对象列表序列化为JSON字符串列表
        List<String> list = messages.stream()
                .map(msg -> {
                    try {
                        return objectMapper.writeValueAsString(msg);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("消息序列化失败", e);
                    }
                })
                .collect(Collectors.toList());

        // 5、使用 rightPushAll 将新消息追加到Redis列表的末尾
        stringRedisTemplate.opsForList().rightPushAll(conversationKey, list);

        // 6、更新会话在ZSet中的分数（时间戳），以实现会话置顶
        stringRedisTemplate.opsForZSet().add(conversationIdsSetPrefix + userId, conversationId, System.currentTimeMillis());

        // 7、刷新消息列表和会话ID集合的过期时间
        stringRedisTemplate.expire(conversationKey, timeout, TimeUnit.SECONDS);
        stringRedisTemplate.expire(conversationIdsSetPrefix + userId, timeout, TimeUnit.SECONDS);
    }


    /**
     * 覆盖式保存一个会话的全部消息
     *
     * @param userId         用户ID
     * @param conversationId 会话ID
     * @param messages       要保存的完整消息列表
     */
    public void saveAll(String userId, String conversationId, List<Message> messages) {
        // 1、校验ID
        checkUserIdAndConversationId(userId, conversationId);
        // 2、拼接Redis Key
        String conversationKey = prefix + userId + ":" + conversationId;
        // 3、先删除旧的会话记录，以实现覆盖效果
        stringRedisTemplate.delete(conversationKey);

        // 4、如果消息为空，则仅执行删除操作
        if (messages == null || messages.isEmpty()) return;

        // 5、序列化消息列表
        List<String> list = messages.stream()
                .map(msg -> {
                    try {
                        return objectMapper.writeValueAsString(msg);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("消息序列化失败", e);
                    }
                })
                .collect(Collectors.toList());

        // 6、将新消息写入Redis列表
        stringRedisTemplate.opsForList().rightPushAll(conversationKey, list);
        // 7、更新会话时间戳
        stringRedisTemplate.opsForZSet().add(conversationIdsSetPrefix + userId, conversationId, System.currentTimeMillis());
        // 8、刷新过期时间
        stringRedisTemplate.expire(conversationKey, timeout, TimeUnit.SECONDS);
        stringRedisTemplate.expire(conversationIdsSetPrefix + userId, timeout, TimeUnit.SECONDS);
    }

    /**
     * 根据用户ID和会话ID删除聊天记录
     *
     * @param userId         用户ID
     * @param conversationId 会话ID
     */
    public void deleteByConversationId(String userId, String conversationId) {
        // 1、校验ID
        checkUserIdAndConversationId(userId, conversationId);
        // 2、删除存储消息的Redis List
        stringRedisTemplate.delete(prefix + userId + ":" + conversationId);
        // 3、从该用户的会话ID集合（ZSet）中移除对应的会话ID
        stringRedisTemplate.opsForZSet().remove(conversationIdsSetPrefix + userId, conversationId);
    }

    // ================== 工具方法 ==================


    /**
     * 检查用户ID是否为空
     */
    private void checkUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId不能为空");
        }
    }

    /**
     * 检查用户ID和会话ID是否为空
     */
    private void checkUserIdAndConversationId(String userId, String conversationId) {
        checkUserId(userId);
        if (conversationId == null || conversationId.isEmpty()) {
            throw new IllegalArgumentException("conversationId不能为空");
        }
    }

    /**
     * 将JSON字符串反序列化为Spring AI的Message对象
     *
     * @param json 消息的JSON字符串
     * @return Message对象
     * @throws IOException 解析失败时抛出异常
     */
    public Message deserializeMessage(String json) throws IOException {
        // 1、将JSON字符串解析为Jackson的JsonNode树
        JsonNode node = objectMapper.readTree(json);
        if (!node.has("messageType")) throw new IllegalArgumentException("缺少 messageType 字段");

        // 2、提取公共字段
        String type = node.get("messageType").asText();
        String text = node.has("text") ? node.get("text").asText() : "";
        Map<String, Object> metadata = node.has("metadata") ?
                objectMapper.convertValue(node.get("metadata"), new TypeReference<>() {}) : new HashMap<>();
        List<Media> mediaList = node.has("media") ? getMediaList(node) : List.of();

        // 3、根据`messageType`字段的值，使用switch语句创建不同类型的Message对象
        return switch (MessageType.valueOf(type)) {
            case SYSTEM -> new SystemMessage(text);
            case USER -> UserMessage.builder().text(text).metadata(metadata).media(mediaList).build();
            case ASSISTANT -> {
                // 专门处理ASSISTANT类型的toolCalls字段
                List<AssistantMessage.ToolCall> toolCalls = node.has("toolCalls") ?
                        objectMapper.convertValue(node.get("toolCalls"), new TypeReference<>() {}) : List.of();
                yield new AssistantMessage(text, metadata, toolCalls, mediaList);
            }
            default -> throw new IllegalArgumentException("未知 messageType: " + type);
        };
    }

    /**
     * 从JsonNode中解析出Media列表
     */
    private List<Media> getMediaList(JsonNode node) throws IOException {
        // 1、初始化一个空列表
        List<Media> mediaList = new ArrayList<>();
        // 2、遍历JSON中`media`字段的数组
        for (JsonNode mediaNode : node.get("media")) {
            // 3、使用Builder模式构建Media对象
            Media.Builder builder = Media.builder();
            if (mediaNode.has("mimeType")) {
                JsonNode mime = mediaNode.get("mimeType");
                builder.mimeType(new MimeType(mime.get("type").asText(), mime.get("subtype").asText()));
            }
            if (mediaNode.has("data")) {
                String data = mediaNode.get("data").asText();
                if (data.startsWith("http://") || data.startsWith("https://")) builder.data(new URL(data));
                else builder.data(Base64.getDecoder().decode(data));
            }
            // 4、将构建好的Media对象添加到列表中
            mediaList.add(builder.build());
        }
        // 5、返回列表
        return mediaList;
    }
}