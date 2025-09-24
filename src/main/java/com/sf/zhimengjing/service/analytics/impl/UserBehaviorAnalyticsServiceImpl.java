package com.sf.zhimengjing.service.analytics.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.model.dto.analytics.UserBehaviorAnalyticsDTO;
import com.sf.zhimengjing.common.model.vo.analytics.*;
import com.sf.zhimengjing.entity.analytics.UserBehavior;
import com.sf.zhimengjing.mapper.analytics.UserBehaviorMapper;
import com.sf.zhimengjing.service.analytics.UserBehaviorAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Title: UserBehaviorAnalyticsServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.analytics.impl
 * @Description: 用户行为分析服务实现类，提供用户行为数据统计分析功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserBehaviorAnalyticsServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior> implements UserBehaviorAnalyticsService {

    private final UserBehaviorMapper userBehaviorMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取用户行为统计
     */
    @Override
    public IPage<UserBehaviorStatsVO> getUserBehaviorStats(UserBehaviorAnalyticsDTO dto) {
        log.info("开始获取用户行为统计，参数：{}", dto);

        // 构造分页对象
        IPage<UserBehavior> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 构造查询条件
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<UserBehavior>()
                .eq(dto.getUserId() != null, UserBehavior::getUserId, dto.getUserId())
                .eq(dto.getBehaviorType() != null, UserBehavior::getBehaviorType, dto.getBehaviorType())
                .eq(dto.getDeviceType() != null, UserBehavior::getDeviceType, dto.getDeviceType())
                .eq(dto.getOsType() != null, UserBehavior::getOsType, dto.getOsType())
                .ge(dto.getStartTime() != null, UserBehavior::getBehaviorTime, dto.getStartTime())
                .le(dto.getEndTime() != null, UserBehavior::getBehaviorTime, dto.getEndTime())
                .orderByDesc(UserBehavior::getBehaviorTime);

        // 执行分页查询
        IPage<UserBehavior> entityPage = this.page(page, wrapper);

        // 将 IPage<UserBehavior> 转换为 IPage<UserBehaviorStatsVO>
        return entityPage.convert(userBehavior -> {
            UserBehaviorStatsVO vo = new UserBehaviorStatsVO();
            BeanUtils.copyProperties(userBehavior, vo);
            vo.setUsername(getUsernameById(userBehavior.getUserId()));
            // 注意：原始的聚合逻辑 aggregateUserBehaviorStats 是对整个分页结果进行聚合的，
            // 而不是单条转换。如果需要聚合，则需要单独处理。
            // 当前的 convert 是逐条转换，适合展示列表。
            return vo;
        });
    }

    /**
     * 获取用户活跃度分析
     */
    @Override
    public UserActivityAnalysisVO getUserActivityAnalysis(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("开始分析用户活跃度，用户ID：{}，时间范围：{} - {}", userId, startTime, endTime);

        // 查询用户行为数据
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<UserBehavior>()
                .eq(UserBehavior::getUserId, userId)
                .ge(startTime != null, UserBehavior::getBehaviorTime, startTime)
                .le(endTime != null, UserBehavior::getBehaviorTime, endTime)
                .orderByAsc(UserBehavior::getBehaviorTime);

        List<UserBehavior> behaviors = this.list(wrapper);

        if (CollectionUtils.isEmpty(behaviors)) {
            return UserActivityAnalysisVO.builder()
                    .userId(userId)
                    .activityLevel("inactive")
                    .activityScore(0.0)
                    .build();
        }

        // 计算活跃度指标
        return calculateActivityAnalysis(userId, behaviors);
    }

    /**
     * 获取用户留存率分析
     */
    @Override
    public UserRetentionAnalysisVO getUserRetentionAnalysis(LocalDate startDate, LocalDate endDate) {
        log.info("开始分析用户留存率，日期范围：{} - {}", startDate, endDate);

        // 获取新用户注册数据
        Map<LocalDate, Set<Long>> newUsersMap = getNewUsersByDate(startDate, endDate);

        // 计算留存率
        return calculateRetentionAnalysis(newUsersMap, startDate, endDate);
    }

    /**
     * 获取用户转化漏斗分析
     */
    @Override
    public ConversionFunnelVO getConversionFunnelAnalysis(String funnelType, LocalDate startDate, LocalDate endDate) {
        log.info("开始转化漏斗分析，类型：{}，日期范围：{} - {}", funnelType, startDate, endDate);

        List<String> funnelSteps = getFunnelSteps(funnelType);
        List<ConversionFunnelVO.FunnelStepVO> stepVOList = new ArrayList<>();

        int totalUsers = 0;
        int previousStepUsers = 0;

        for (int i = 0; i < funnelSteps.size(); i++) {
            String stepName = funnelSteps.get(i);
            int currentStepUsers = getUserCountForStep(stepName, startDate, endDate);

            if (i == 0) {
                totalUsers = currentStepUsers;
                previousStepUsers = currentStepUsers;
            }

            BigDecimal conversionRate = totalUsers > 0 ?
                    new BigDecimal(currentStepUsers).divide(new BigDecimal(totalUsers), 4, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;

            BigDecimal dropOffRate = previousStepUsers > 0 ?
                    new BigDecimal(previousStepUsers - currentStepUsers).divide(new BigDecimal(previousStepUsers), 4, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;

            stepVOList.add(ConversionFunnelVO.FunnelStepVO.builder()
                    .stepName(stepName)
                    .userCount(currentStepUsers)
                    .conversionRate(conversionRate)
                    .dropOffRate(dropOffRate)
                    .build());

            previousStepUsers = currentStepUsers;
        }

        // 构建漏斗摘要
        ConversionFunnelVO.FunnelSummaryVO summary = buildFunnelSummary(stepVOList, totalUsers);

        BigDecimal overallConversionRate = totalUsers > 0 ?
                new BigDecimal(previousStepUsers).divide(new BigDecimal(totalUsers), 4, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        return ConversionFunnelVO.builder()
                .funnelType(funnelType)
                .steps(stepVOList)
                .overallConversionRate(overallConversionRate)
                .summary(summary)
                .build();
    }

    /**
     * 批量保存用户行为数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveUserBehavior(List<UserBehavior> behaviors) {
        log.info("开始批量保存用户行为数据，数量：{}", behaviors.size());

        if (CollectionUtils.isEmpty(behaviors)) {
            return;
        }

        // 数据清洗和去重
        List<UserBehavior> cleanedBehaviors = behaviors.stream()
                .filter(this::isValidBehavior)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(cleanedBehaviors)) {
            this.saveBatch(cleanedBehaviors);

            // 更新实时统计缓存
            updateRealtimeStatsCache(cleanedBehaviors);
        }

        log.info("批量保存用户行为数据完成，实际保存数量：{}", cleanedBehaviors.size());
    }

    /**
     * 聚合用户行为统计数据 (注意：此方法在新的getUserBehaviorStats中未被直接使用)
     */
    private List<UserBehaviorStatsVO> aggregateUserBehaviorStats(List<UserBehavior> behaviors) {
        Map<String, List<UserBehavior>> groupedBehaviors = behaviors.stream()
                .collect(Collectors.groupingBy(behavior ->
                        behavior.getUserId() + "_" + behavior.getBehaviorType() + "_" + behavior.getDeviceType()));

        return groupedBehaviors.entrySet().stream()
                .map(entry -> {
                    List<UserBehavior> userBehaviors = entry.getValue();
                    UserBehavior first = userBehaviors.get(0);

                    long totalStayDuration = userBehaviors.stream()
                            .mapToLong(b -> b.getStayDuration() != null ? b.getStayDuration() : 0L)
                            .sum();

                    double avgStayDuration = userBehaviors.size() > 0 ?
                            (double) totalStayDuration / userBehaviors.size() : 0.0;

                    Set<String> sessionIds = userBehaviors.stream()
                            .map(UserBehavior::getSessionId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    return UserBehaviorStatsVO.builder()
                            .userId(first.getUserId())
                            .username(getUsernameById(first.getUserId()))
                            .behaviorType(first.getBehaviorType())
                            .behaviorCount(userBehaviors.size())
                            .totalStayDuration(totalStayDuration)
                            .avgStayDuration(avgStayDuration)
                            .deviceType(first.getDeviceType())
                            .sessionCount(sessionIds.size())
                            .lastBehaviorTime(userBehaviors.stream()
                                    .map(UserBehavior::getBehaviorTime)
                                    .max(LocalDateTime::compareTo)
                                    .orElse(null))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 计算用户活跃度分析
     */
    private UserActivityAnalysisVO calculateActivityAnalysis(Long userId, List<UserBehavior> behaviors) {
        // 计算基础指标
        int totalVisits = behaviors.size();
        long totalStayMinutes = behaviors.stream()
                .mapToLong(b -> b.getStayDuration() != null ? b.getStayDuration() : 0L)
                .sum() / 60;

        Set<String> uniqueSessions = behaviors.stream()
                .map(UserBehavior::getSessionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        double avgSessionMinutes = uniqueSessions.size() > 0 ?
                (double) totalStayMinutes / uniqueSessions.size() : 0.0;

        Set<LocalDate> activeDays = behaviors.stream()
                .map(b -> b.getBehaviorTime().toLocalDate())
                .collect(Collectors.toSet());

        // 计算活跃度评分
        double activityScore = calculateActivityScore(totalVisits, totalStayMinutes, activeDays.size());
        String activityLevel = getActivityLevel(activityScore);

        // 构建每日活跃趋势
        List<DailyActivityVO> dailyTrend = buildDailyActivityTrend(behaviors);

        // 行为分布统计
        Map<String, Integer> behaviorDistribution = behaviors.stream()
                .collect(Collectors.groupingBy(
                        UserBehavior::getBehaviorType,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)));

        // 设备使用情况
        Map<String, Integer> deviceUsage = behaviors.stream()
                .filter(b -> b.getDeviceType() != null)
                .collect(Collectors.groupingBy(
                        UserBehavior::getDeviceType,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)));

        return UserActivityAnalysisVO.builder()
                .userId(userId)
                .activityLevel(activityLevel)
                .activityScore(activityScore)
                .totalVisits(totalVisits)
                .totalStayMinutes(totalStayMinutes)
                .avgSessionMinutes(avgSessionMinutes)
                .activeDays(activeDays.size())
                .dailyTrend(dailyTrend)
                .behaviorDistribution(behaviorDistribution)
                .deviceUsage(deviceUsage)
                .build();
    }

    /**
     * 计算活跃度评分
     */
    private double calculateActivityScore(int totalVisits, long totalStayMinutes, int activeDays) {
        // 访问次数权重：40%
        double visitScore = Math.min(totalVisits / 100.0, 1.0) * 40;

        // 停留时长权重：35%
        double stayScore = Math.min(totalStayMinutes / 300.0, 1.0) * 35;

        // 活跃天数权重：25%
        double dayScore = Math.min(activeDays / 30.0, 1.0) * 25;

        return visitScore + stayScore + dayScore;
    }

    /**
     * 获取活跃度等级
     */
    private String getActivityLevel(double score) {
        if (score >= 80) return "very_high";
        if (score >= 60) return "high";
        if (score >= 40) return "medium";
        if (score >= 20) return "low";
        return "very_low";
    }

    /**
     * 验证行为数据有效性
     */
    private boolean isValidBehavior(UserBehavior behavior) {
        return behavior != null &&
                behavior.getUserId() != null &&
                behavior.getBehaviorType() != null &&
                behavior.getBehaviorTime() != null;
    }

    /**
     * 更新实时统计缓存
     */
    private void updateRealtimeStatsCache(List<UserBehavior> behaviors) {
        try {
            String today = LocalDate.now().toString();

            // 更新今日行为统计
            behaviors.forEach(behavior -> {
                String key = "realtime:behavior:" + today + ":" + behavior.getBehaviorType();
                redisTemplate.opsForValue().increment(key, 1);
                redisTemplate.expire(key, Duration.ofDays(7));
            });

            // 更新活跃用户数
            Set<Long> activeUsers = behaviors.stream()
                    .map(UserBehavior::getUserId)
                    .collect(Collectors.toSet());

            String activeUserKey = "realtime:active_users:" + today;
            activeUsers.forEach(userId ->
                    redisTemplate.opsForSet().add(activeUserKey, userId.toString()));
            redisTemplate.expire(activeUserKey, Duration.ofDays(7));

        } catch (Exception e) {
            log.error("更新实时统计缓存失败", e);
        }
    }

    // 其他辅助方法的实现...
    private String getUsernameById(Long userId) {
        // 模拟：从用户服务获取用户名
        return "user_" + userId;
    }

    private Map<LocalDate, Set<Long>> getNewUsersByDate(LocalDate startDate, LocalDate endDate) {
        // 模拟：实现获取新用户数据的逻辑
        return new HashMap<>();
    }

    private UserRetentionAnalysisVO calculateRetentionAnalysis(Map<LocalDate, Set<Long>> newUsersMap, LocalDate startDate, LocalDate endDate) {
        // 模拟：实现留存率计算逻辑
        return UserRetentionAnalysisVO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .dayOneRetention(BigDecimal.ZERO)
                .daySevenRetention(BigDecimal.ZERO)
                .dayThirtyRetention(BigDecimal.ZERO)
                .build();
    }

    private List<String> getFunnelSteps(String funnelType) {
        // 根据漏斗类型返回步骤
        switch (funnelType) {
            case "vip_conversion":
                return Arrays.asList("注册", "首次登录", "梦境提交", "VIP页面访问", "VIP购买");
            case "dream_submission":
                return Arrays.asList("访问首页", "点击记录梦境", "填写梦境内容", "提交梦境");
            default:
                return Arrays.asList("访问", "注册", "活跃", "付费");
        }
    }

    private int getUserCountForStep(String stepName, LocalDate startDate, LocalDate endDate) {
        // 模拟：实现获取步骤用户数的逻辑
        return new Random().nextInt(100);
    }

    private ConversionFunnelVO.FunnelSummaryVO buildFunnelSummary(List<ConversionFunnelVO.FunnelStepVO> steps, int totalUsers) {
        // 找到最大流失步骤
        String maxDropOffStep = steps.stream()
                .max(Comparator.comparing(ConversionFunnelVO.FunnelStepVO::getDropOffRate))
                .map(ConversionFunnelVO.FunnelStepVO::getStepName)
                .orElse("N/A");

        List<String> suggestions = Arrays.asList(
                "优化 " + maxDropOffStep + " 步骤的用户体验",
                "增加引导提示和帮助信息",
                "简化操作流程",
                "提供更好的激励机制"
        );

        return ConversionFunnelVO.FunnelSummaryVO.builder()
                .totalUsers(totalUsers)
                .convertedUsers(steps.isEmpty() ? 0 : steps.get(steps.size() - 1).getUserCount())
                .maxDropOffStep(maxDropOffStep)
                .optimizationSuggestions(suggestions)
                .build();
    }

    private List<DailyActivityVO> buildDailyActivityTrend(List<UserBehavior> behaviors) {
        // 模拟：构建每日活跃趋势数据
        return new ArrayList<>();
    }
}