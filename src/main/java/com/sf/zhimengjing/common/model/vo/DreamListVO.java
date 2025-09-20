package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: DreamListVO
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 用于展示梦境列表的视图对象，包含基本信息、统计数据和标签
 */
@Data
@Schema(description = "梦境列表VO")
public class DreamListVO {

    /** 梦境ID，唯一标识一条梦境记录 */
    @Schema(description = "梦境ID")
    private Long id;

    /** 用户名，记录梦境的所属用户 */
    @Schema(description = "用户名")
    private String userName;

    /** 梦境标题 */
    @Schema(description = "梦境标题")
    private String title;

    /** 分类名称，例如“噩梦”、“奇幻梦”等 */
    @Schema(description = "分类名称")
    private String categoryName;

    /** 梦境发生日期 */
    @Schema(description = "做梦日期")
    private LocalDate dreamDate;

    /** 是否公开展示给其他用户 */
    @Schema(description = "是否公开")
    private Boolean isPublic;

    /** 状态文本，用于前端显示，例如“正常”、“审核中”等 */
    @Schema(description = "状态文本")
    private String statusText;

    /** 浏览次数，统计该梦境被查看的次数 */
    @Schema(description = "浏览次数")
    private Integer viewCount;

    /** 点赞数，统计该梦境被点赞的次数 */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /** 梦境记录创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 标签列表，用于描述梦境的主题或情绪等 */
    @Schema(description = "标签列表")
    private List<String> tags;
}
