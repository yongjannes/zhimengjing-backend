package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.admin.DreamCategoryAttribute;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: DreamCategoryAttributeMapper
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.admin
 * @description: 梦境分类属性 Mapper 接口，继承 MyBatis-Plus BaseMapper，
 *               提供对 dream_categories_attributes 表的 CRUD 操作。
 */
@Mapper
public interface DreamCategoryAttributeMapper extends BaseMapper<DreamCategoryAttribute> {
}
