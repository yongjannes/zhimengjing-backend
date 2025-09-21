package com.sf.zhimengjing.common.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Title: ReportQueryDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto.community
 * @description: 内容举报查询DTO，用于封装举报查询条件，实现分页、过滤和状态筛选功能
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "内容举报查询DTO")
public class ReportQueryDTO extends UserQueryDTO {

    /**
     * 内容类型：
     * 1 - 帖子
     * 2 - 评论
     */
    @ApiModelProperty("内容类型：1-帖子，2-评论")
    private Integer contentType;

    /**
     * 举报类型（可根据业务自定义，例如：1-垃圾广告，2-违规内容，3-辱骂等）
     */
    @ApiModelProperty("举报类型：1-垃圾广告...")
    private Integer reportType;

    /**
     * 处理状态：
     * 0 - 待处理
     * 1 - 已处理
     * 2 - 已驳回
     */
    @ApiModelProperty("处理状态：0-待处理，1-已处理，2-已驳回")
    private Integer status;

    /** 举报开始时间 */
    private LocalDateTime startDate;

    /** 举报结束时间 */
    private LocalDateTime endDate;
}
