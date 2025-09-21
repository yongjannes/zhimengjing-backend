package com.sf.zhimengjing.common.model.dto;

import lombok.Data;
import java.util.List;

/**
 * @Title: CategoryQueryDTO
 * @Author: 殇枫
 * @Package: com.dreamanalysis.admin.dto
 * @description: 梦境分类查询条件 DTO，用于接收前端查询请求参数，
 *               支持分页、排序、过滤和树形结构查询。
 */
@Data
public class CategoryQueryDTO {

    /**
     * 分类名称（支持模糊查询）
     */
    private String name;

    /**
     * 分类描述（支持模糊查询）
     */
    private String description;

    /**
     * 父分类ID，用于筛选指定父分类下的子分类
     */
    private Integer parentId;

    /**
     * 分类层级（1-顶级，2-二级，以此类推）
     */
    private Integer level;

    /**
     * 是否启用（true-启用，false-禁用）
     */
    private Boolean isActive;

    /**
     * 是否系统内置分类（true-系统分类，false-用户自建）
     */
    private Boolean isSystem;

    /**
     * 最小梦境数量，用于筛选梦境数量 >= minDreamCount 的分类
     */
    private Integer minDreamCount;

    /**
     * 最大梦境数量，用于筛选梦境数量 <= maxDreamCount 的分类
     */
    private Integer maxDreamCount;

    /**
     * 排除的分类ID列表
     */
    private List<Integer> excludeIds;

    /**
     * 排序字段，默认 "sortOrder"
     */
    private String sortField = "sortOrder";

    /**
     * 排序方向，ASC-升序，DESC-降序，默认 "ASC"
     */
    private String sortDirection = "ASC";

    /**
     * 当前页码，默认 1
     */
    private Integer page = 1;

    /**
     * 每页数量，默认 20
     */
    private Integer pageSize = 20;

    /**
     * 是否返回树形结构，默认 false
     */
    private Boolean includeTree = false;

    /**
     * 树形结构最大深度，默认 3
     */
    private Integer maxDepth = 3;
}
