package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.UserQueryDTO;
import com.sf.zhimengjing.common.model.vo.UserDetailVO;
import com.sf.zhimengjing.common.model.vo.UserGrowthTrendVO;
import com.sf.zhimengjing.common.model.vo.UserListVO;
import com.sf.zhimengjing.common.model.vo.UserStatisticsVO;
import com.sf.zhimengjing.entity.User;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: UserService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service
 * @description: 用户服务接口，提供用户管理相关操作
 */
public interface UserService {

    /**
     * 分页查询用户列表
     *
     * @param userQueryDTO 查询条件
     * @return 分页后的用户列表
     */
    IPage<UserListVO> pageUsers(UserQueryDTO userQueryDTO);

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserDetailVO getUserDetail(Long userId);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 用户状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 批量更新用户状态
     *
     * @param userIds 用户ID列表
     * @param status 用户状态
     */
    void batchUpdateUserStatus(List<Long> userIds, Integer status);

    /**
     * 删除用户（支持批量）
     *
     * @param ids 用户ID字符串，多个用逗号分隔
     */
    void deleteUsers(String ids);
    /**
     * 获取用户统计信息
     *
     * @return 用户统计 VO
     */
    UserStatisticsVO getUserStatistics();

    /**
     * 导出用户数据到 Excel 文件
     *
     * 根据指定的查询条件，查询用户数据并将其导出为 Excel 文件，
     * 通过 HttpServletResponse 直接返回给前端进行下载。
     *
     * @param userQueryDTO 查询条件对象，用于筛选要导出的用户数据
     * @param response HttpServletResponse 对象，用于将生成的 Excel 文件写入响应流
     */
    void exportUsers(UserQueryDTO userQueryDTO, HttpServletResponse response);


    /**
     * 获取用户增长趋势
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户增长趋势列表
     */
    List<UserGrowthTrendVO> getUserGrowthTrend(
            LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 更新普通用户信息
     * @param user 用户信息
     */
    void updateUserInfo(User user);

    /**
     * 根据ID获取用户基本信息（用于编辑表单，清除敏感信息）
     * @param userId 用户ID
     * @return User实体（不含敏感信息）
     */
    User getUserBasicInfo(Long userId);
}
