package com.sf.zhimengjing.controller.admin;

import com.sf.zhimengjing.common.config.RedisChatMemoryRepository;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.ai.PausedMessageDTO;
import com.sf.zhimengjing.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Tag(name = "聊天记录接口", description = "管理用户的聊天历史记录")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryController {

    private final ChatMemoryRepository chatMemoryRepository;

    /**
     * 获取RedisChatMemoryRepository实例，用于访问扩展功能
     *
     * @return RedisChatMemoryRepository实例
     * @throws GeneralBusinessException 如果注入的不是RedisChatMemoryRepository实现
     */
    private RedisChatMemoryRepository getRedisChatMemoryRepository() {
        if (!(chatMemoryRepository instanceof RedisChatMemoryRepository)) {
            throw new GeneralBusinessException("当前ChatMemoryRepository实现不支持此操作");
        }
        return (RedisChatMemoryRepository) chatMemoryRepository;
    }

    @Operation(summary = "1. 获取当前用户的所有会话ID列表", description = "返回一个包含所有会话ID的列表，按最近聊天时间排序")
    @GetMapping("/history/ids")
    public List<String> getConversationIds() {
        String userId = SecurityUtils.getUserId().toString();
        log.info("用户 [userId={}] 正在获取会话ID列表...", userId);

        List<String> conversationIds = getRedisChatMemoryRepository().findConversationIds(userId);

        if (CollectionUtils.isEmpty(conversationIds)) {
            log.warn("用户 [userId={}] 的会话ID列表为空，将向前端返回提示信息。", userId);
            throw new GeneralBusinessException("您还没有任何会话记录");
        }

        log.info("用户 [userId={}] 成功获取到 {} 个会话ID。", userId, conversationIds.size());
        return conversationIds;
    }

    @Operation(summary = "2. 获取指定会话的完整消息记录", description = "根据会话ID获取该会话的所有聊天消息")
    @GetMapping("/history/{conversationId}")
    public List<Message> getConversationById(
            @Parameter(description = "会话的唯一标识ID", required = true) @PathVariable String conversationId) {
        String userId = SecurityUtils.getUserId().toString();
        log.info("用户 [userId={}] 正在获取会话 [conversationId={}] 的消息记录...", userId, conversationId);

        List<Message> messages = getRedisChatMemoryRepository().findByConversationId(userId, conversationId);

        // 如果列表为空，则说明会话不存在
        if (CollectionUtils.isEmpty(messages)) {
            log.warn("用户 [userId={}] 尝试访问一个不存在或为空的会话 [conversationId={}]。", userId, conversationId);
            throw new GeneralBusinessException("该会话不存在或没有消息记录");
        }

        log.info("用户 [userId={}] 成功获取到会话 [conversationId={}] 的 {} 条消息记录。", userId, conversationId, messages.size());
        return messages;
    }

    @Operation(summary = "3. 保存单条中断的消息", description = "用于前端在WebSocket中断或刷新页面时，保存最后一条用户或AI的消息")
    @PostMapping("/save-message")
    public void saveMessage(@RequestBody PausedMessageDTO request) {
        String userId = Objects.requireNonNull(SecurityUtils.getUserId(), "用户未登录").toString();
        log.info("用户 [userId={}] 正在向会话 [conversationId={}] 保存一条类型为 '{}' 的中断消息...",
                userId, request.getConversationId(), request.getMessageType());

        Message message;
        if ("AI".equalsIgnoreCase(request.getMessageType())) {
            message = new AssistantMessage(request.getText());
        } else {
            message = new UserMessage(request.getText());
        }

        getRedisChatMemoryRepository().add(userId, request.getConversationId(), Collections.singletonList(message));
        log.info("用户 [userId={}] 的中断消息已成功保存至会话 [conversationId={}]。", userId, request.getConversationId());
    }

    @Operation(summary = "4. 删除指定会话的聊天记录", description = "根据会话ID删除用户的所有相关聊天记录")
    @DeleteMapping("/history/{conversationId}")
    public Boolean deleteConversationById(
            @Parameter(description = "要删除的会话的唯一标识ID", required = true) @PathVariable String conversationId) {
        String userId = SecurityUtils.getUserId().toString();
        log.info("用户 [userId={}] 正在删除会话 [conversationId={}]...", userId, conversationId);

        // 使用 findByConversationId 来检查会话是否存在
        if (CollectionUtils.isEmpty(getRedisChatMemoryRepository().findByConversationId(userId, conversationId))) {
            log.warn("用户 [userId={}] 尝试删除一个不存在的会话 [conversationId={}]。", userId, conversationId);
            throw new GeneralBusinessException("该会话不存在");
        }

        getRedisChatMemoryRepository().deleteByConversationId(userId, conversationId);

        log.info("用户 [userId={}] 的会话 [conversationId={}] 已成功删除。", userId, conversationId);
        return true;
    }
}