package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.admin.DreamCategoryRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: DreamCategoryRelationMapper
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.admin
 * @description: 梦境分类关系 Mapper 接口，继承 MyBatis-Plus BaseMapper，
 *               提供对 dream_category_relations 表的 CRUD 操作，
 *               支持祖先-后代分类关系查询和维护。
 */
@Mapper
public interface DreamCategoryRelationMapper extends BaseMapper<DreamCategoryRelation> {
}
