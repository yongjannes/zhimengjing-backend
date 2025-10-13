package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: CommunityPostQueryDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 社区帖子查询DTO
 */
@Data
@Schema(description = "社区帖子查询DTO")
public class CommunityPostQueryDTO {

    /** 用户名 */
    @Schema(description = "用户名")
    private String userName;

    /** 帖子标题 */
    @Schema(description = "帖子标题")
    private String title;

    /** 帖子内容 */
    @Schema(description = "帖子内容")
    private String content;

    /** 状态：0-待审核，1-已通过，2-已拒绝，3-已删除 */
    @Schema(description = "状态:0-待审核,1-已通过,2-已拒绝,3-已删除")
    private Integer status;

    /** 是否置顶:0-否,1-是 */
    @Schema(description = "是否置顶:0-否,1-是")
    private Integer isTop;

    /** 是否热门:0-否,1-是 */
    @Schema(description = "是否热门:0-否,1-是")
    private Integer isHot;

    /** 分类ID */
    @Schema(description = "分类ID")
    private Integer categoryId;

    /** 创建时间-开始 */
    @Schema(description = "创建时间-开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTimeStart;

    /** 创建时间-结束 */
    @Schema(description = "创建时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTimeEnd;

    /** 当前页码 */
    @Schema(description = "当前页码")
    private Integer pageNum;

    /** 每页数量 */
    @Schema(description = "每页数量")
    private Integer pageSize;
}