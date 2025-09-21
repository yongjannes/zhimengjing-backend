package com.sf.zhimengjing.common.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: CategoryVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @description: 梦境分类视图对象（VO），用于前端展示分类信息，
 *               包含基本信息、统计数据、层级关系及创建/更新信息。
 */
@Data
@Schema(description = "梦境分类VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryVO {

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long id;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称", example = "梦境日记")
    private String name;

    /**
     * 分类描述
     */
    @Schema(description = "分类描述", example = "用于记录用户梦境的分类")
    private String description;

    /**
     * 分类图标URL
     */
    @Schema(description = "分类图标URL", example = "https://example.com/icon.png")
    private String icon;

    /**
     * 分类颜色（#RRGGBB）
     */
    @Schema(description = "分类颜色", example = "#FF5733")
    private String color;

    /**
     * 父分类ID（0表示顶级分类）
     */
    @Schema(description = "父分类ID", example = "0")
    private Integer parentId;

    /**
     * 分类层级（1-顶级，2-二级，以此类推）
     */
    @Schema(description = "分类层级", example = "1")
    private Integer level;

    /**
     * 排序序号（越小越靠前）
     */
    @Schema(description = "排序序号", example = "0")
    private Integer sortOrder;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用", example = "true")
    private Boolean isActive;

    /**
     * 是否系统内置分类
     */
    @Schema(description = "是否系统内置分类", example = "false")
    private Boolean isSystem;

    /**
     * 梦境数量
     */
    @Schema(description = "梦境数量", example = "120")
    private Integer dreamCount;

    /**
     * 子分类数量
     */
    @Schema(description = "子分类数量", example = "5")
    private Integer subCategoryCount;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1001")
    private Long createBy;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID", example = "1002")
    private Long updateBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-09-21T13:00:00")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2025-09-21T15:00:00")
    private LocalDateTime updateTime;

    /**
     * 子分类列表（树形结构，用于展示层级关系）
     */
    @Schema(description = "子分类列表")
    private List<CategoryVO> children;
}
