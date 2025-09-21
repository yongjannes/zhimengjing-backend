package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.CommentQueryDTO;
import com.sf.zhimengjing.entity.admin.CommunityComment;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Title: CommunityCommentMapper.xml
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.community
 * @description: 社区评论 Mapper 接口，继承 MyBatis-Plus BaseMapper
 * 提供评论分页查询及时间范围统计功能
 */
public interface CommunityCommentMapper extends BaseMapper<CommunityComment> {

    /**
     * 分页查询评论列表
     * @param page 分页对象
     * @param queryDTO 查询条件封装对象
     * @return 分页后的评论列表
     */
    IPage<CommunityComment> selectCommentPage(IPage<CommunityComment> page, @Param("query") CommentQueryDTO queryDTO);

    /**
     * 根据时间范围统计评论数量
     * @param params 参数集合，例如 startTime、endTime
     * @return 指定时间范围内的评论数量
     */
    Long countByTimeRange(@Param("params") Map<String, Object> params);
}
