package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * @Title: DreamQueryDTO
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 梦境查询数据传输对象，用于分页和条件查询梦境记录
 */
@Data
@Schema(description = "梦境查询DTO")
public class DreamQueryDTO {

    /** 用户名，可用于按用户搜索 */
    @Schema(description = "用户名")
    private String userName;

    /** 梦境标题，可用于模糊匹配查询 */
    @Schema(description = "梦境标题")
    private String title;

    /** 梦境分类ID，用于按分类筛选 */
    @Schema(description = "分类ID")
    private Integer categoryId;

    /** 梦境标签ID列表，用于按标签筛选，可多选 */
    @Schema(description = "标签ID列表")
    private List<Integer> tagIds;

    /** 梦境日期查询起始时间 */
    @Schema(description = "做梦日期开始")
    private LocalDate dreamDateStart;

    /** 梦境日期查询结束时间 */
    @Schema(description = "做梦日期结束")
    private LocalDate dreamDateEnd;

    /** 梦境状态: 1-正常, 2-审核中, 3-已审核, 4-已拒绝 */
    @Schema(description = "状态:1-正常,2-审核中,3-已审核,4-已拒绝")
    private Integer status;

    /** 分页参数：页码，默认1 */
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    /** 分页参数：每页条数，默认10 */
    @Schema(description = "每页大小", defaultValue = "10")
    private Integer pageSize = 10;
}
