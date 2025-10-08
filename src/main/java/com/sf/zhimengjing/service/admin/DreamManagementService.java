package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.DreamAuditDTO;
import com.sf.zhimengjing.common.model.dto.DreamQueryDTO;
import com.sf.zhimengjing.common.model.vo.DreamListVO;
import com.sf.zhimengjing.common.model.vo.DreamStatisticsVO;
import com.sf.zhimengjing.entity.admin.DreamRecord;

import java.util.List;

/**
 * @Title: DreamManagementService
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: 后台梦境管理服务接口，用于提供梦境数据的分页查询和统计功能。
 */
public interface DreamManagementService {

    /**
     * 分页查询梦境列表
     * @param dreamQueryDTO 查询条件，包括用户名、标题、分类、标签、日期范围等
     * @return 分页后的梦境列表，包含分页信息
     */
    IPage<DreamListVO> pageDreams(DreamQueryDTO dreamQueryDTO);

    /**
     * 获取梦境统计信息
     * @return DreamStatisticsVO对象，包含总梦境数、审核状态统计、公开梦境数及新增梦境数等
     */
    DreamStatisticsVO getDreamStatistics();

    /**
     * 获取梦境详情
     * @param id 梦境ID
     * @return 梦境详情
     */
    DreamRecord getDreamDetail(Long id);

    /**
     * 审核梦境
     * @param auditDTO
     */
    void auditDreams(DreamAuditDTO auditDTO);

    /**
     * 删除梦境
     * @param dreamIds
     */
    void deleteDreams(List<Long> dreamIds);
}
