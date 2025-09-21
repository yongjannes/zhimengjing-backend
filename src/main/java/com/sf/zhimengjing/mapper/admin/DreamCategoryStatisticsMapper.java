package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.admin.DreamCategoryStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: DreamCategoryStatisticsMapper
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.admin
 * @description: 梦境分类统计 Mapper 接口，继承 MyBatis-Plus BaseMapper，
 *               提供对 dream_categories_statistics 表的 CRUD 操作，
 *               用于存储和查询分类相关的统计数据，如梦境数量、审核状态及平均睡眠质量。
 */
@Mapper
public interface DreamCategoryStatisticsMapper extends BaseMapper<DreamCategoryStatistics> {
}
