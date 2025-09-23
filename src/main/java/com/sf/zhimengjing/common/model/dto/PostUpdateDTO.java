package com.sf.zhimengjing.common.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Title: PostUpdateDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto.community
 * @description: 社区帖子更新DTO，用于封装更新帖子信息的请求数据
 */
@Data
@ApiModel(description = "帖子更新DTO")
public class PostUpdateDTO {

    /**
     * 帖子ID，必填，用于标识需要更新的帖子
     */
    @NotNull(message = "帖子ID不能为空")
    @ApiModelProperty(value = "帖子ID", required = true)
    private Long id;

    /**
     * 帖子标题，可选
     */
    @ApiModelProperty("帖子标题")
    private String title;

    /**
     * 帖子内容，可选
     */
    @ApiModelProperty("帖子内容")
    private String content;

    /**
     * 分类ID，可选，用于修改帖子所属分类
     */
    @ApiModelProperty("分类ID")
    private Integer categoryId;

    /**
     * 标签列表，可选，用于更新帖子标签
     */
    @ApiModelProperty("标签列表")
    private List<String> tags;

    // 添加以下字段以支持全面更新
    @ApiModelProperty("是否公开")
    private Boolean isAnonymous;

    @ApiModelProperty("是否置顶")
    private Boolean isTop;

    @ApiModelProperty("是否热门")
    private Boolean isHot;

    @ApiModelProperty("帖子状态（0-待审核, 1-已发布, 2-已拒绝, 3-已删除）")
    private Integer status;

    @ApiModelProperty("拒绝原因")
    private String rejectReason;

    @ApiModelProperty("管理员备注")
    private String adminRemark;
}