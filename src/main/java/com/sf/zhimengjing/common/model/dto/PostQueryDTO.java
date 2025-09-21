package com.sf.zhimengjing.common.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Title: PostQueryDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto.community
 * @description: 社区帖子查询DTO，用于封装查询条件，实现分页、过滤、模糊搜索等功能
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "社区帖子查询DTO")
public class PostQueryDTO extends UserQueryDTO {

    /**
     * 帖子标题，支持模糊查询
     */
    @ApiModelProperty("帖子标题（模糊查询）")
    private String title;

    /**
     * 分类ID，用于按分类过滤帖子
     */
    @ApiModelProperty("分类ID")
    private Integer categoryId;

    /**
     * 帖子状态：
     * 0 - 待审核
     * 1 - 已通过
     * 2 - 已拒绝
     * 3 - 已删除
     */
    @ApiModelProperty("状态：0-待审核，1-已通过，2-已拒绝，3-已删除")
    private Integer status;

    /**
     * 是否置顶，true-置顶，false-非置顶
     */
    @ApiModelProperty("是否置顶")
    private Boolean isTop;

    /**
     * 是否热门，true-热门，false-非热门
     */
    @ApiModelProperty("是否热门")
    private Boolean isHot;

    /** 帖子开始时间 */
    private LocalDateTime startDate;

    /** 帖子结束时间 */
    private LocalDateTime endDate;

}
