package com.sf.zhimengjing.common.model.dto.ai;

import lombok.Data;

/**
 * @Title: PausedMessageDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto.ai
 * @description:
 */
@Data
public class PausedMessageDTO {
    private String conversationId;
    private String text;
    private String messageType; // "USER" or "AI"
}