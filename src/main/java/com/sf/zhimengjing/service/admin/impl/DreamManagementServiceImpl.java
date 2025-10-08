package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.model.dto.DreamAuditDTO;
import com.sf.zhimengjing.common.model.dto.DreamQueryDTO;
import com.sf.zhimengjing.common.model.vo.DreamListVO;
import com.sf.zhimengjing.common.model.vo.DreamStatisticsVO;
import com.sf.zhimengjing.common.util.SecurityUtils;
import com.sf.zhimengjing.entity.admin.DreamRecord;
import com.sf.zhimengjing.entity.admin.DreamReviewLog;
import com.sf.zhimengjing.mapper.admin.DreamRecordMapper;
import com.sf.zhimengjing.mapper.admin.DreamReviewLogMapper;
import com.sf.zhimengjing.service.admin.DreamManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: DreamManagementServiceImpl
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: 后台梦境管理服务实现类，提供梦境列表分页查询、梦境统计等功能，用于管理员对用户梦境数据进行管理和分析。
 */

@Service
@RequiredArgsConstructor
public class DreamManagementServiceImpl implements DreamManagementService {

    private final DreamRecordMapper dreamRecordMapper;
    private final DreamReviewLogMapper dreamReviewLogMapper;

    /**
     * 分页查询梦境列表
     * @param dto 查询条件，包括标题、分类、状态、日期范围、分页信息等
     * @return 分页后的DreamListVO列表
     */
    @Override
    public IPage<DreamListVO> pageDreams(DreamQueryDTO dto) {
        Page<DreamListVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return dreamRecordMapper.selectDreamListPage(page, dto);
    }

    /**
     * 获取梦境统计信息
     * @return DreamStatisticsVO，包含总梦境数、审核状态统计、公开梦境数及新增梦境数
     */
    @Override
    public DreamStatisticsVO getDreamStatistics() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart = todayStart.minusDays(todayStart.getDayOfWeek().getValue() - 1);
        LocalDateTime monthStart = todayStart.withDayOfMonth(1);

        return dreamRecordMapper.selectDreamStatistics(todayStart, weekStart, monthStart);
    }

    @Override
    public DreamRecord getDreamDetail(Long id) {
        return dreamRecordMapper.selectById(id);
    }

    /**
     * 审核梦境
     * @param auditDTO 梦境审核信息
     */
    @Override
    @Transactional
    public void auditDreams(DreamAuditDTO auditDTO) {

        Long reviewerId = SecurityUtils.getUserId();  // 当前审核管理员ID
        LocalDateTime now = LocalDateTime.now();

        for (Long dreamId : auditDTO.getDreamIds()) {
            // 查询原始状态
            DreamRecord oldDream = dreamRecordMapper.selectById(dreamId);
            if (oldDream == null) continue;

            Integer oldStatus = oldDream.getStatus();

            // === 更新 dream_record 表 ===
            DreamRecord dream = new DreamRecord();
            dream.setId(dreamId);
            dream.setStatus(auditDTO.getStatus());
            dream.setReviewNotes(auditDTO.getReviewNotes());
            dream.setReviewedAt(now);

            if (auditDTO.getStatus() == 4 && auditDTO.getRejectReason() != null) {
                dream.setReviewNotes(auditDTO.getRejectReason());
            }

            dreamRecordMapper.updateById(dream);

            // === 新增 dream_review_logs 日志 ===
            DreamReviewLog log = new DreamReviewLog();
            log.setDreamId(dreamId);
            log.setReviewerId(reviewerId);
            log.setAction(auditDTO.getStatus() == 3 ? "APPROVE" : "REJECT");
            log.setPreviousStatus(oldStatus);
            log.setNewStatus(auditDTO.getStatus());
            log.setReason(auditDTO.getStatus() == 4 ? auditDTO.getRejectReason() : auditDTO.getReviewNotes());
            log.setCreateTime(now);

            dreamReviewLogMapper.insert(log);
        }
    }


    @Override
    @Transactional
    public void deleteDreams(List<Long> dreamIds) {
        if (dreamIds != null && !dreamIds.isEmpty()) {
            dreamRecordMapper.deleteBatchIds(dreamIds);
        }
    }

    /**
     * 将DreamRecord实体转换为DreamListVO
     * @param dream DreamRecord实体对象
     * @return DreamListVO视图对象
     */
    private DreamListVO convertToDreamListVO(DreamRecord dream) {
        DreamListVO vo = new DreamListVO();
        vo.setId(dream.getId());
        vo.setTitle(dream.getTitle());
        vo.setDreamDate(dream.getDreamDate());
        vo.setIsPublic(dream.getIsPublic() == 1);
        vo.setStatusText(getStatusText(dream.getStatus()));
        vo.setViewCount(dream.getViewCount());
        vo.setLikeCount(dream.getLikeCount());
        vo.setCreateTime(dream.getCreateTime());
        // TODO: 查询并设置 userName, categoryName, tags
        return vo;
    }

    /**
     * 根据状态值获取状态文本
     * @param status 状态码
     * @return 状态文本描述
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 1 -> "正常";
            case 2 -> "审核中";
            case 3 -> "已审核";
            case 4 -> "已拒绝";
            default -> "未知";
        };
    }
}
