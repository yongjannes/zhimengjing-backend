package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Title: UserTagVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 用户标签 VO
 */
@Data
@Schema(description = "用户标签VO")
public class UserTagVO {

    /** 标签ID */
    @Schema(description = "标签ID")
    private Long id;

    /** 标签名称 */
    @Schema(description = "标签名称")
    private String tagName;

    /** 标签颜色 */
    @Schema(description = "标签颜色")
    private String color;
}
