package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.DreamQueryDTO;
import com.sf.zhimengjing.common.model.vo.DreamListVO;
import com.sf.zhimengjing.common.model.vo.DreamStatisticsVO;
import com.sf.zhimengjing.entity.admin.DreamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @Title: DreamRecordMapper
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.mapper
 * @Description: 管理梦境数据访问层接口
 */
@Mapper
public interface DreamRecordMapper extends BaseMapper<DreamRecord> {



    /**
     *分页连接查询梦境列表，直接返回VO
     * @param page 分页对象
     * @param query 查询条件 DTO
     * @return 分页后的 VO 列表
     */
    IPage<DreamListVO> selectDreamListPage(IPage<DreamListVO> page, @Param("query") DreamQueryDTO query);

    /**
     * 一次性查询所有梦境统计数据
     *
     * @param todayStart 今日开始时间
     * @param weekStart  本周开始时间
     * @param monthStart 本月开始时间
     * @return 梦境统计VO
     */
    DreamStatisticsVO selectDreamStatistics(
            @Param("todayStart") LocalDateTime todayStart,
            @Param("weekStart") LocalDateTime weekStart,
            @Param("monthStart") LocalDateTime monthStart
    );

}
