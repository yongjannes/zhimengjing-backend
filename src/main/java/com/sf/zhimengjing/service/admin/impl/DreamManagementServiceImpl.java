package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.model.dto.DreamQueryDTO;
import com.sf.zhimengjing.common.model.vo.DreamListVO;
import com.sf.zhimengjing.common.model.vo.DreamStatisticsVO;
import com.sf.zhimengjing.entity.admin.DreamRecord;
import com.sf.zhimengjing.mapper.admin.DreamRecordMapper;
import com.sf.zhimengjing.service.admin.DreamManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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

    /**
     * 分页查询梦境列表
     * @param dto 查询条件，包括标题、分类、状态、日期范围、分页信息等
     * @return 分页后的DreamListVO列表
     */
    @Override
    public IPage<DreamListVO> pageDreams(DreamQueryDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<DreamRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(dto.getTitle()), DreamRecord::getTitle, dto.getTitle());
        queryWrapper.eq(dto.getCategoryId() != null, DreamRecord::getCategoryId, dto.getCategoryId());
        queryWrapper.eq(dto.getStatus() != null, DreamRecord::getStatus, dto.getStatus());
        queryWrapper.ge(dto.getDreamDateStart() != null, DreamRecord::getDreamDate, dto.getDreamDateStart());
        queryWrapper.le(dto.getDreamDateEnd() != null, DreamRecord::getDreamDate, dto.getDreamDateEnd());
        queryWrapper.orderByDesc(DreamRecord::getCreateTime);

        // 创建分页对象
        Page<DreamRecord> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<DreamRecord> dreamPage = dreamRecordMapper.selectPage(page, queryWrapper);

        // 转换为VO对象
        Page<DreamListVO> voPage = new Page<>(dreamPage.getCurrent(), dreamPage.getSize(), dreamPage.getTotal());
        voPage.setRecords(dreamPage.getRecords().stream()
                .map(this::convertToDreamListVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    /**
     * 获取梦境统计信息
     * @return DreamStatisticsVO，包含总梦境数、审核状态统计、公开梦境数及新增梦境数
     */
    @Override
    public DreamStatisticsVO getDreamStatistics() {
        DreamStatisticsVO vo = new DreamStatisticsVO();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 总梦境数
        vo.setTotalDreams(dreamRecordMapper.selectCount(
                new LambdaQueryWrapper<DreamRecord>().eq(DreamRecord::getDeleteFlag, 0)));

        // 各状态梦境数
        vo.setPendingDreams(dreamRecordMapper.selectCount(
                new LambdaQueryWrapper<DreamRecord>().eq(DreamRecord::getStatus, 2)
                        .eq(DreamRecord::getDeleteFlag, 0)));
        vo.setApprovedDreams(dreamRecordMapper.selectCount(
                new LambdaQueryWrapper<DreamRecord>().eq(DreamRecord::getStatus, 3)
                        .eq(DreamRecord::getDeleteFlag, 0)));
        vo.setRejectedDreams(dreamRecordMapper.selectCount(
                new LambdaQueryWrapper<DreamRecord>().eq(DreamRecord::getStatus, 4)
                        .eq(DreamRecord::getDeleteFlag, 0)));

        // 公开梦境数
        vo.setPublicDreams(dreamRecordMapper.selectCount(
                new LambdaQueryWrapper<DreamRecord>().eq(DreamRecord::getIsPublic, 1)
                        .eq(DreamRecord::getDeleteFlag, 0)));

        // 今日新增梦境数
        vo.setTodayNewDreams(dreamRecordMapper.selectCount(
                new LambdaQueryWrapper<DreamRecord>().ge(DreamRecord::getCreateTime, todayStart)
                        .eq(DreamRecord::getDeleteFlag, 0)));

        // 本周新增梦境数
        LocalDateTime weekStart = todayStart.minusDays(todayStart.getDayOfWeek().getValue() - 1);
        vo.setWeekNewDreams(dreamRecordMapper.selectCount(
                new LambdaQueryWrapper<DreamRecord>().ge(DreamRecord::getCreateTime, weekStart)
                        .eq(DreamRecord::getDeleteFlag, 0)));

        // 本月新增梦境数
        LocalDateTime monthStart = todayStart.withDayOfMonth(1);
        vo.setMonthNewDreams(dreamRecordMapper.selectCount(
                new LambdaQueryWrapper<DreamRecord>().ge(DreamRecord::getCreateTime, monthStart)
                        .eq(DreamRecord::getDeleteFlag, 0)));

        return vo;
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
