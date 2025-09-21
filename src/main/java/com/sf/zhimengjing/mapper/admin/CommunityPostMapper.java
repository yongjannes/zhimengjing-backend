package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.PostQueryDTO;
import com.sf.zhimengjing.entity.admin.CommunityPost;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Title: CommunityPostMapper
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.community
 * @description: 社区帖子 Mapper 接口，继承 MyBatis-Plus BaseMapper
 * 提供帖子分页查询、详情查询及统计功能
 */
public interface CommunityPostMapper extends BaseMapper<CommunityPost> {

    /**
     * 分页查询帖子列表
     * @param page 分页对象
     * @param queryDTO 查询条件封装对象
     * @return 分页后的帖子列表
     */
    IPage<CommunityPost> selectPostPage(IPage<CommunityPost> page, @Param("query") PostQueryDTO queryDTO);

    /**
     * 根据ID查询帖子详情
     * @param id 帖子ID
     * @return 帖子详情对象
     */
    CommunityPost selectPostDetailById(@Param("id") Long id);

    /**
     * 根据时间范围统计帖子数量
     * @param params 参数集合，例如 startTime、endTime
     * @return 指定时间范围内的帖子数量
     */
    Long countByTimeRange(@Param("params") Map<String, Object> params);
}
