package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.admin.DreamCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: DreamCategoryMapper
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.admin
 * @description: 梦境分类 Mapper 接口，继承 MyBatis-Plus BaseMapper，
 *               提供对 dream_categories 表的 CRUD 操作。
 */
@Mapper
public interface DreamCategoryMapper extends BaseMapper<DreamCategory> {
}
