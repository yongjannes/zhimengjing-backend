package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.model.dto.CommunityPostQueryDTO;
import com.sf.zhimengjing.common.model.vo.CommunityPostListVO;
import com.sf.zhimengjing.common.model.vo.CommunityPostStatisticsVO;
import com.sf.zhimengjing.entity.admin.CommunityPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Title: CommunityPostMapper
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.admin
 * @Description: 社区帖子Mapper接口
 */
@Mapper
public interface CommunityPostMapper extends BaseMapper<CommunityPost> {

    /**
     * 分页查询帖子列表
     *
     * @param page 分页对象
     * @param queryDTO 查询条件
     * @return 帖子列表
     */
    IPage<CommunityPostListVO> selectPostListPage(Page<CommunityPostListVO> page, @Param("query") CommunityPostQueryDTO queryDTO);

    /**
     * 查询帖子统计信息
     *
     * @return 统计信息
     */
    CommunityPostStatisticsVO selectPostStatistics();
}