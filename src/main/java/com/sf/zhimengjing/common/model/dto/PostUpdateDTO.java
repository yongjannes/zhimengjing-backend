package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "帖子更新DTO")
public class PostUpdateDTO {

    /**
     * 帖子ID，必填，用于标识需要更新的帖子
     */
    @NotNull(message = "帖子ID不能为空")
    @Schema(description = "帖子ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 帖子标题，可选
     */
    @Schema(description = "帖子标题")
    private String title;

    /**
     * 帖子内容，可选
     */
    @Schema(description = "帖子内容")
    private String content;

    /**
     * 分类ID，可选，用于修改帖子所属分类
     */
    @Schema(description = "分类ID")
    private Integer categoryId;

    /**
     * 标签列表，可选，用于更新帖子标签
     */
    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "是否公开")
    private Boolean isAnonymous;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "是否热门")
    private Boolean isHot;

    @Schema(description = "帖子状态（0-待审核, 1-已发布, 2-已拒绝, 3-已删除）")
    private Integer status;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "管理员备注")
    private String adminRemark;
}