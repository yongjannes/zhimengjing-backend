package com.sf.zhimengjing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.common.model.vo.UserGrowthTrendVO;
import com.sf.zhimengjing.entity.User;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: UserMapper
 * @Author 殇枫
 * @Package com.sf.zhimengjing.mapper
 * @description: 用户表 Mapper 接口
 */

public interface UserMapper extends BaseMapper<User> {
    /**
     * 获取用户增长趋势数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 增长趋势列表
     */
    List<UserGrowthTrendVO> getUserGrowthTrend(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
