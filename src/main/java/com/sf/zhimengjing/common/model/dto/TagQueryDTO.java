package com.sf.zhimengjing.common.model.dto;

import lombok.Data;

/**
 * @Title: TagQueryDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @description: 梦境标签查询条件 DTO
 */
@Data
public class TagQueryDTO {


    /**
     * 标签名称（支持模糊查询）
     */
    private String name;

    /**
     * 是否启用（true-启用，false-禁用）
     */
    private Boolean isActive;

    /**
     * 最小使用次数
     */
    private Integer minUsageCount;

    /**
     * 最大使用次数
     */
    private Integer maxUsageCount;

    /**
     * 排序字段，默认 "usageCount"
     */
    private String sortField = "usageCount";

    /**
     * 排序方向，ASC-升序，DESC-降序，默认 "DESC"
     */
    private String sortDirection = "DESC";

    /**
     * 当前页码，默认 1
     */
    private Integer pageNum = 1;

    /**
     * 每页数量，默认 10
     */
    private Integer pageSize = 10;
}