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
}
