package com.sf.zhimengjing.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: TagVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @description: 梦境标签视图对象（VO）
 */
@Data
@Schema(description = "梦境标签VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagVO {

    /**
     * 标签ID
     */
    @Schema(description = "标签ID", example = "1")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称", example = "噩梦")
    private String name;

    /**
     * 标签颜色
     */
    @Schema(description = "标签颜色", example = "#FF5733")
    private String color;

    /**
     * 标签描述
     */
    @Schema(description = "标签描述", example = "表示令人恐惧的梦境")
    private String description;

    /**
     * 使用次数
     */
    @Schema(description = "使用次数", example = "120")
    private Integer usageCount;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用", example = "true")
    private Boolean isActive;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}