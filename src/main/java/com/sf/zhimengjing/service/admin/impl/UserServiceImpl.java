package com.sf.zhimengjing.service.admin.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.UserQueryDTO;
import com.sf.zhimengjing.common.model.vo.*;
import com.sf.zhimengjing.entity.User;
import com.sf.zhimengjing.mapper.UserMapper;
import com.sf.zhimengjing.service.admin.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: UserServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.admin.impl
 * @description: 用户服务实现类，继承 MyBatis-Plus 的 ServiceImpl 并实现 UserService 接口。
 *               提供用户管理相关操作，包括分页查询用户、获取用户详情、修改用户状态、
 *               批量操作、删除用户、统计用户数据、导出用户以及获取用户增长趋势等功能。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    /**
     * 分页查询用户列表
     *
     * 根据前端传入的查询条件（用户名、手机号、邮箱、状态、用户等级、注册时间区间等）进行分页查询，
     * 并将查询结果转换为前端所需的 UserListVO 对象。
     *
     * @param userQueryDTO 用户查询条件 DTO，包括分页信息、查询条件等
     * @return 分页封装的用户列表 VO 对象（IPage<UserListVO>）
     */
    @Override
    public IPage<UserListVO> pageUsers(UserQueryDTO userQueryDTO) {
        // 创建 LambdaQueryWrapper，用于构建动态查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 只查询未删除的用户（deleteFlag = 0）
        queryWrapper.eq(User::getDeleteFlag, 0);

        // 根据用户名进行模糊查询
        if (StringUtils.isNotBlank(userQueryDTO.getUsername())) {
            queryWrapper.like(User::getUsername, userQueryDTO.getUsername());
        }
        // 根据手机号进行模糊查询
        if (StringUtils.isNotBlank(userQueryDTO.getPhone())) {
            queryWrapper.like(User::getPhone, userQueryDTO.getPhone());
        }
        // 根据邮箱进行模糊查询
        if (StringUtils.isNotBlank(userQueryDTO.getEmail())) {
            queryWrapper.like(User::getEmail, userQueryDTO.getEmail());
        }
        // 根据用户状态精确查询
        if (userQueryDTO.getStatus() != null) {
            queryWrapper.eq(User::getStatus, userQueryDTO.getStatus());
        }
        // 根据用户等级精确查询
        if (userQueryDTO.getUserLevel() != null) {
            queryWrapper.eq(User::getUserLevel, userQueryDTO.getUserLevel());
        }
        // 注册开始时间查询（大于等于）
        if (userQueryDTO.getRegisterStartTime() != null) {
            queryWrapper.ge(User::getCreateTime, userQueryDTO.getRegisterStartTime());
        }
        // 注册结束时间查询（小于等于）
        if (userQueryDTO.getRegisterEndTime() != null) {
            queryWrapper.le(User::getCreateTime, userQueryDTO.getRegisterEndTime());
        }

        // 按创建时间倒序排序，最新注册用户排在前面
        queryWrapper.orderByDesc(User::getCreateTime);

        // 创建分页对象，前端传入页码和每页条数
        Page<User> page = new Page<>(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        // 执行分页查询
        IPage<User> userPage = this.page(page, queryWrapper);

        // 将查询结果转换为前端需要的 VO 对象
        Page<UserListVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserListVO> voList = userPage.getRecords().stream()
                .map(this::convertToUserListVO) // 调用转换方法，将实体 User 转为 UserListVO
                .toList();
        voPage.setRecords(voList); // 设置转换后的记录到分页对象中

        // 返回封装好的分页 VO 数据
        return voPage;
    }

    /**
     * 获取用户详细信息
     *
     * 根据用户ID查询用户实体，如果用户存在且未删除，则将实体转换为 UserDetailVO 返回给前端。
     * 如果用户不存在或已删除，则抛出业务异常。
     *
     * @param userId 用户ID
     * @return UserDetailVO 用户详细信息 VO
     * @throws GeneralBusinessException 当用户不存在或已删除时抛出异常
     */
    @Override
    public UserDetailVO getUserDetail(Long userId) {
        // 根据用户ID查询用户实体
        User user = this.getById(userId);

        // 如果用户不存在或者已删除，抛出业务异常
        if (user == null || user.getDeleteFlag() == 1) {
            throw new GeneralBusinessException("用户不存在");
        }

        // 将 User 实体转换为前端需要的 UserDetailVO 并返回
        return convertToUserDetailVO(user);
    }


    /**
     * 修改单个用户的状态
     *
     * 根据用户ID修改用户的状态（如正常、禁用、待审核等）。
     * 如果用户不存在或已删除，则抛出业务异常。
     *
     * @param userId 用户ID
     * @param status 新的用户状态值
     * @throws GeneralBusinessException 当用户不存在或已删除时抛出异常
     */
    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        // 根据用户ID查询用户实体
        User user = this.getById(userId);

        // 如果用户不存在或者已删除，抛出业务异常
        if (user == null || user.getDeleteFlag() == 1) {
            throw new GeneralBusinessException("用户不存在");
        }

        // 设置新的用户状态
        user.setStatus(status);
        // 更新修改时间为当前时间
        user.setUpdateTime(LocalDateTime.now());
        // 将修改后的用户实体更新到数据库
        this.updateById(user);
    }

    /**
     * 批量修改用户状态
     *
     * 根据传入的用户ID列表，将对应用户的状态统一修改为指定状态。
     * 只修改未删除的有效用户，如果列表为空或没有找到有效用户，则抛出业务异常。
     *
     * @param userIds 用户ID列表
     * @param status 新的用户状态值
     * @throws GeneralBusinessException 当用户ID列表为空或没有找到有效用户时抛出异常
     */
    @Override
    @Transactional
    public void batchUpdateUserStatus(List<Long> userIds, Integer status) {
        // 校验用户ID列表是否为空
        if (userIds == null || userIds.isEmpty()) {
            throw new GeneralBusinessException("用户ID列表不能为空");
        }

        // 构建查询条件，只查询列表中指定的用户且未删除的用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .in(User::getId, userIds)        // 用户ID在列表中
                .eq(User::getDeleteFlag, 0);     // 未删除

        // 执行查询，获取符合条件的用户列表
        List<User> users = this.list(queryWrapper);

        // 如果没有找到有效用户，则抛出异常
        if (users.isEmpty()) {
            throw new GeneralBusinessException("没有找到有效的用户");
        }

        // 遍历用户列表，修改状态并更新时间
        users.forEach(user -> {
            user.setStatus(status);                // 设置新的状态
            user.setUpdateTime(LocalDateTime.now()); // 更新时间为当前时间
        });

        // 批量更新用户信息到数据库
        this.updateBatchById(users);
    }

    /**
     * 删除单个用户（逻辑删除）
     *
     * 根据用户ID对用户进行逻辑删除（将 deleteFlag 设置为 1）。
     * 如果用户不存在或已删除，则抛出业务异常。
     *
     * @param userId 用户ID
     * @throws GeneralBusinessException 当用户不存在或已删除时抛出异常
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // 根据用户ID查询用户实体
        User user = this.getById(userId);

        // 校验用户是否存在或已删除
        if (user == null || user.getDeleteFlag() == 1) {
            throw new GeneralBusinessException("用户不存在");
        }

        // 设置逻辑删除标志
        user.setDeleteFlag(1);
        // 更新修改时间为当前时间
        user.setUpdateTime(LocalDateTime.now());
        // 将修改后的用户实体更新到数据库
        this.updateById(user);
    }

    /**
     * 批量删除用户（逻辑删除）
     *
     * 根据传入的用户ID列表，将对应用户进行逻辑删除（将 deleteFlag 设置为 1）。
     * 只操作未删除的有效用户，如果列表为空或没有找到有效用户，则抛出业务异常。
     *
     * @param userIds 用户ID列表
     * @throws GeneralBusinessException 当用户ID列表为空或没有找到有效用户时抛出异常
     */
    @Override
    @Transactional
    public void batchDeleteUsers(List<Long> userIds) {
        // 校验用户ID列表是否为空
        if (userIds == null || userIds.isEmpty()) {
            throw new GeneralBusinessException("用户ID列表不能为空");
        }

        // 构建查询条件，只查询列表中指定的用户且未删除的用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .in(User::getId, userIds)        // 用户ID在列表中
                .eq(User::getDeleteFlag, 0);     // 未删除

        // 执行查询，获取符合条件的用户列表
        List<User> users = this.list(queryWrapper);

        // 如果没有找到有效用户，则抛出异常
        if (users.isEmpty()) {
            throw new GeneralBusinessException("没有找到有效的用户");
        }

        // 遍历用户列表，设置逻辑删除标志并更新时间
        users.forEach(user -> {
            user.setDeleteFlag(1);                // 设置删除标志
            user.setUpdateTime(LocalDateTime.now()); // 更新时间
        });

        // 批量更新用户信息到数据库
        this.updateBatchById(users);
    }

    /**
     * 获取用户统计信息
     *
     * 统计系统中用户的各种信息，包括：
     * - 总用户数
     * - 正常用户数
     * - 禁用用户数
     * - 待审核用户数
     * - 已认证用户数
     * - VIP 用户数
     * - 今日新增用户数
     * - 本周新增用户数
     * - 本月新增用户数
     *
     * @return UserStatisticsVO 封装好的用户统计信息对象
     */
    @Override
    public UserStatisticsVO getUserStatistics() {
        UserStatisticsVO statistics = new UserStatisticsVO();

        // 统计总用户数（未删除）
        statistics.setTotalUsers(
                userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getDeleteFlag, 0))
        );

        // 统计正常用户数（状态=1且未删除）
        statistics.setNormalUsers(
                userMapper.selectCount(new LambdaQueryWrapper<User>()
                        .eq(User::getStatus, 1)
                        .eq(User::getDeleteFlag, 0))
        );

        // 统计禁用用户数（状态=0且未删除）
        statistics.setDisabledUsers(
                userMapper.selectCount(new LambdaQueryWrapper<User>()
                        .eq(User::getStatus, 0)
                        .eq(User::getDeleteFlag, 0))
        );

        // 统计待审核用户数（状态=2且未删除）
        statistics.setPendingUsers(
                userMapper.selectCount(new LambdaQueryWrapper<User>()
                        .eq(User::getStatus, 2)
                        .eq(User::getDeleteFlag, 0))
        );

        // 统计已认证用户数（isVerified=1且未删除）
        statistics.setVerifiedUsers(
                userMapper.selectCount(new LambdaQueryWrapper<User>()
                        .eq(User::getIsVerified, 1)
                        .eq(User::getDeleteFlag, 0))
        );

        // 统计VIP用户数（userLevel=2且未删除）
        statistics.setVipUsers(
                userMapper.selectCount(new LambdaQueryWrapper<User>()
                        .eq(User::getUserLevel, 2)
                        .eq(User::getDeleteFlag, 0))
        );

        // 计算今日新增用户数
        LocalDateTime todayStart = java.time.LocalDate.now().atStartOfDay(); // 今日零点
        LocalDateTime todayEnd = todayStart.plusDays(1); // 明日零点
        statistics.setTodayNewUsers(
                userMapper.selectCount(new LambdaQueryWrapper<User>()
                        .between(User::getCreateTime, todayStart, todayEnd)
                        .eq(User::getDeleteFlag, 0))
        );

        // 计算本周新增用户数（周一到今天）
        LocalDateTime weekStart = todayStart.minusDays(todayStart.getDayOfWeek().getValue() - 1); // 本周周一零点
        statistics.setWeekNewUsers(
                userMapper.selectCount(new LambdaQueryWrapper<User>()
                        .between(User::getCreateTime, weekStart, todayEnd)
                        .eq(User::getDeleteFlag, 0))
        );

        // 计算本月新增用户数（本月1号到今天）
        LocalDateTime monthStart = todayStart.withDayOfMonth(1); // 本月1号零点
        statistics.setMonthNewUsers(
                userMapper.selectCount(new LambdaQueryWrapper<User>()
                        .between(User::getCreateTime, monthStart, todayEnd)
                        .eq(User::getDeleteFlag, 0))
        );

        // 返回封装好的统计信息对象
        return statistics;
    }

    /**
     * 导出用户数据到 Excel 文件（完整实现）
     *
     * 根据查询条件筛选用户数据，并将其导出为 Excel 文件，
     * 通过 HttpServletResponse 直接返回给前端进行下载。
     * 使用 EasyExcel 写入 Excel，并设置响应头实现文件下载。
     *
     * @param userQueryDTO 查询条件对象，用于筛选要导出的用户数据
     * @param response HttpServletResponse 对象，用于将生成的 Excel 文件写入响应流
     * @throws GeneralBusinessException 当导出过程中发生 IOException 时抛出异常
     */
    @Override
    public void exportUsers(UserQueryDTO userQueryDTO, HttpServletResponse response) {
        LambdaQueryWrapper<User> queryWrapper = buildUserQueryWrapper(userQueryDTO);
        List<User> userList = userMapper.selectList(queryWrapper);

        List<UserExcelVO> excelVOList = userList.stream()
                .map(this::convertToUserExcelVO)
                .collect(Collectors.toList());

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String fileName = URLEncoder.encode("用户数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), UserExcelVO.class)
                    .sheet("用户列表")
                    .doWrite(excelVOList);
        } catch (IOException e) {
            log.error("导出用户数据Excel异常: ", e);
            throw new GeneralBusinessException("导出用户数据失败");
        }
    }


    /**
     * 获取用户增长趋势
     *
     * 统计指定时间范围内每天的新增用户数量，用于分析用户增长情况。
     *
     * @param startTime 起始时间（包含当天）
     * @param endTime   结束时间（包含当天）
     * @return List<UserGrowthTrendVO> 每天新增用户数量的列表，每个元素包含日期和对应的新增用户数
     */
    @Override
    public List<UserGrowthTrendVO> getUserGrowthTrend(LocalDateTime startTime, LocalDateTime endTime) {
        // 打印日志，便于排查查询时间范围
        log.info("查询用户增长趋势: startTime={}, endTime={}", startTime, endTime);

        // 调用 Mapper 层方法执行 SQL 查询，返回增长趋势列表
        return userMapper.getUserGrowthTrend(startTime, endTime);
    }



    /**
     * 将 User 实体对象转换为 UserExcelVO，用于 Excel 导出。
     *
     * @param user 用户实体对象
     * @return UserExcelVO 对象，包含适合导出的字段和格式化文本
     */
    private UserExcelVO convertToUserExcelVO(User user) {
        UserExcelVO excelVO = new UserExcelVO();
        excelVO.setUsername(user.getUsername());
        excelVO.setNickname(user.getNickname());
        excelVO.setEmail(user.getEmail());
        excelVO.setPhone(user.getPhone());
        excelVO.setGender(getGenderText(user.getGender())); // 转换性别为文本
        excelVO.setUserLevel(getUserLevelText(user.getUserLevel())); // 转换用户等级为文本
        excelVO.setStatus(getStatusText(user.getStatus())); // 转换状态为文本
        excelVO.setBalance(user.getBalance());
        excelVO.setAvailablePoints(user.getAvailablePoints());
        excelVO.setRegisterSource(user.getRegisterSource());
        excelVO.setCreateTime(user.getCreateTime());
        return excelVO;
    }

    /**
     * 根据 UserQueryDTO 构建查询条件的 LambdaQueryWrapper。
     *
     * @param userQueryDTO 用户查询条件 DTO
     * @return LambdaQueryWrapper<User> 查询条件封装对象
     */
    private LambdaQueryWrapper<User> buildUserQueryWrapper(UserQueryDTO userQueryDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDeleteFlag, 0); // 只查询未删除的用户

        // 添加模糊查询条件
        if (StringUtils.isNotBlank(userQueryDTO.getUsername())) {
            queryWrapper.like(User::getUsername, userQueryDTO.getUsername());
        }
        if (StringUtils.isNotBlank(userQueryDTO.getPhone())) {
            queryWrapper.like(User::getPhone, userQueryDTO.getPhone());
        }
        if (StringUtils.isNotBlank(userQueryDTO.getEmail())) {
            queryWrapper.like(User::getEmail, userQueryDTO.getEmail());
        }

        // 添加精确匹配条件
        if (userQueryDTO.getStatus() != null) {
            queryWrapper.eq(User::getStatus, userQueryDTO.getStatus());
        }
        if (userQueryDTO.getUserLevel() != null) {
            queryWrapper.eq(User::getUserLevel, userQueryDTO.getUserLevel());
        }

        // 添加时间范围查询
        if (userQueryDTO.getRegisterStartTime() != null) {
            queryWrapper.ge(User::getCreateTime, userQueryDTO.getRegisterStartTime());
        }
        if (userQueryDTO.getRegisterEndTime() != null) {
            queryWrapper.le(User::getCreateTime, userQueryDTO.getRegisterEndTime());
        }

        // 按创建时间倒序排序
        queryWrapper.orderByDesc(User::getCreateTime);
        return queryWrapper;
    }



    /**
     * 将 User 实体转换为用户列表展示 VO
     *
     * 适用于分页列表或表格展示，只包含主要信息和文本描述。
     *
     * @param user 用户实体
     * @return UserListVO 用户列表展示对象
     */
    private UserListVO convertToUserListVO(User user) {
        UserListVO vo = new UserListVO();

        // 基本信息
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setBirthday(user.getBirthday());
        vo.setProvince(user.getProvince());
        vo.setCity(user.getCity());
        vo.setUserLevel(user.getUserLevel());
        vo.setUserType(user.getUserType());
        vo.setStatus(user.getStatus());
        vo.setRegisterSource(user.getRegisterSource());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setLastLoginIp(user.getLastLoginIp());
        vo.setLoginCount(user.getLoginCount());
        vo.setAvailablePoints(user.getAvailablePoints());
        vo.setBalance(user.getBalance());
        vo.setTotalConsume(user.getTotalConsume());
        vo.setIsVerified(user.getIsVerified());
        vo.setInviteCode(user.getInviteCode());
        vo.setCreateTime(user.getCreateTime());

        // 设置文本描述，用于前端显示
        vo.setGenderText(getGenderText(user.getGender()));
        vo.setUserLevelText(getUserLevelText(user.getUserLevel()));
        vo.setStatusText(getStatusText(user.getStatus()));
        vo.setVerifiedText(getVerifiedText(user.getIsVerified()));

        return vo;
    }

    /**
     * 将 User 实体转换为用户详情 VO
     *
     * 适用于用户详情页面，包含更多字段和账户相关信息。
     *
     * @param user 用户实体
     * @return UserDetailVO 用户详情对象
     */
    private UserDetailVO convertToUserDetailVO(User user) {
        UserDetailVO vo = new UserDetailVO();

        // 基本信息
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setBirthday(user.getBirthday());
        vo.setProvince(user.getProvince());
        vo.setCity(user.getCity());
        vo.setDistrict(user.getDistrict());
        vo.setAddress(user.getAddress());
        vo.setSignature(user.getSignature());
        vo.setUserLevel(user.getUserLevel());
        vo.setUserType(user.getUserType());
        vo.setStatus(user.getStatus());
        vo.setRegisterSource(user.getRegisterSource());
        vo.setRegisterIp(user.getRegisterIp());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setLastLoginIp(user.getLastLoginIp());
        vo.setLoginCount(user.getLoginCount());
        vo.setTotalPoints(user.getTotalPoints());
        vo.setAvailablePoints(user.getAvailablePoints());
        vo.setBalance(user.getBalance());
        vo.setTotalConsume(user.getTotalConsume());
        vo.setIsVerified(user.getIsVerified());
        vo.setVerifiedName(user.getVerifiedName());
        vo.setVerifiedCard(user.getVerifiedCard());
        vo.setVerifiedTime(user.getVerifiedTime());
        vo.setInviteCode(user.getInviteCode());
        vo.setInvitedBy(user.getInvitedBy());
        vo.setWechatOpenid(user.getWechatOpenid());
        vo.setWechatUnionid(user.getWechatUnionid());
        vo.setQqOpenid(user.getQqOpenid());
        vo.setWeiboUid(user.getWeiboUid());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        // 设置文本描述，用于前端显示
        vo.setGenderText(getGenderText(user.getGender()));
        vo.setUserLevelText(getUserLevelText(user.getUserLevel()));
        vo.setStatusText(getStatusText(user.getStatus()));
        vo.setVerifiedText(getVerifiedText(user.getIsVerified()));

        // 简化统计计数，真实实现可查询相关表
        vo.setPointsRecordsCount(0);
        vo.setBalanceRecordsCount(0);
        vo.setLoginLogsCount(0);

        return vo;
    }

    /**
     * 根据 gender 整数值返回文本描述
     *
     * @param gender 性别，1=男，2=女
     * @return 文本描述
     */
    private String getGenderText(Integer gender) {
        if (gender == null) return "未知";
        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    /**
     * 根据用户等级返回文本描述
     *
     * @param userLevel 用户等级，1=普通用户，2=VIP，3=高级VIP
     * @return 文本描述
     */
    private String getUserLevelText(Integer userLevel) {
        if (userLevel == null) return "未知";
        return switch (userLevel) {
            case 1 -> "普通用户";
            case 2 -> "VIP用户";
            case 3 -> "高级VIP用户";
            default -> "未知";
        };
    }

    /**
     * 根据用户状态返回文本描述
     *
     * @param status 用户状态，0=禁用，1=正常，2=待审核
     * @return 文本描述
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "禁用";
            case 1 -> "正常";
            case 2 -> "待审核";
            default -> "未知";
        };
    }

    /**
     * 根据是否认证返回文本描述
     *
     * @param isVerified 认证状态，1=已认证，0=未认证
     * @return 文本描述
     */
    private String getVerifiedText(Integer isVerified) {
        if (isVerified == null) return "未认证";
        return isVerified == 1 ? "已认证" : "未认证";
    }

}