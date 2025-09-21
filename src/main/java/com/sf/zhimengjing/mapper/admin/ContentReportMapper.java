package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.ReportQueryDTO;
import com.sf.zhimengjing.entity.admin.ContentReport;
import org.apache.ibatis.annotations.Param;

/**
 * @Title: ContentReportMapper
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.community
 * @description: 内容举报 Mapper 接口，继承 MyBatis-Plus BaseMapper
 * 提供举报分页查询及详情查询功能
 */
public interface ContentReportMapper extends BaseMapper<ContentReport> {

    /**
     * 分页查询举报列表
     * @param page 分页对象
     * @param queryDTO 查询条件封装对象
     * @return 分页后的举报列表
     */
    IPage<ContentReport> selectReportPage(IPage<ContentReport> page, @Param("query") ReportQueryDTO queryDTO);

    /**
     * 根据ID查询举报详情
     * @param id 举报ID
     * @return 举报实体
     */
    ContentReport selectReportDetailById(@Param("id") Long id);
}
