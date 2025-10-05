package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: AvatarVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 头像VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "头像VO")
public class AvatarVO {
    /**
     * 可访问的URL
     */
    @Schema(description = "可访问的URL")
    private String url;

    /**
     * URL过期时间的毫秒时间戳
     */
    @Schema(description = "URL过期时间的毫秒时间戳")
    private Long expiresAt;
}