package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.ai.AIModelDTO;
import com.sf.zhimengjing.entity.admin.AIModel;
import com.sf.zhimengjing.entity.admin.AIUsageStatistics;
import com.sf.zhimengjing.mapper.admin.AIModelMapper;
import com.sf.zhimengjing.mapper.admin.AIUsageStatisticsMapper;
import com.sf.zhimengjing.service.admin.AIModelService;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: AIModelServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: AI模型服务实现类，提供AI模型相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AIModelServiceImpl extends ServiceImpl<AIModelMapper, AIModel> implements AIModelService {

    private final AIUsageStatisticsMapper usageStatisticsMapper;
    private final StringEncryptor stringEncryptor;


    @Override
    public IPage<AIModelDTO> getAvailableModels(Page<AIModelDTO> page, String provider) {
        // 构造查询条件
        LambdaQueryWrapper<AIModel> wrapper = new LambdaQueryWrapper<AIModel>()
                .eq(StringUtils.hasText(provider), AIModel::getProvider, provider)
                .orderByDesc(AIModel::getIsDefault)
                .orderByAsc(AIModel::getCreateTime);

        // 执行分页查询
        IPage<AIModel> entityPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);

        // 转换为 DTO 并返回
        return entityPage.convert(this::convertToDTO);
    }

    @Override
    public AIModelDTO getModelDetail(String modelCode) {
        AIModel model = this.getOne(new LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, modelCode));
        if (model == null) {
            throw new GeneralBusinessException("AI模型不存在：" + modelCode);
        }
        return convertToDTO(model);
    }

    @Override
    @Transactional
    public boolean createModel(AIModelDTO.ModelRequestDTO requestDTO, Long operatorId) {
        // 检查模型编码是否已存在
        boolean exists = this.count(new LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, requestDTO.getModelCode())) > 0;
        if (exists) {
            throw new GeneralBusinessException("模型编码已存在：" + requestDTO.getModelCode());
        }

        // 创建AI模型
        AIModel model = new AIModel();
        BeanUtils.copyProperties(requestDTO, model);

        if (StringUtils.hasText(requestDTO.getApiKey())) {
            model.setApiKey(stringEncryptor.encrypt(requestDTO.getApiKey()));
        }

        model.setIsAvailable(true);
        model.setIsDefault(false);

        return this.save(model);
    }

    @Override
    @Transactional
    public boolean updateModel(String modelCode, AIModelDTO.ModelRequestDTO requestDTO, Long operatorId) {
        AIModel model = this.getOne(new LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, modelCode));
        if (model == null) {
            throw new GeneralBusinessException("AI模型不存在：" + modelCode);
        }

        // 更新模型信息
        BeanUtils.copyProperties(requestDTO, model);
        if (StringUtils.hasText(requestDTO.getApiKey())) {
            model.setApiKey(stringEncryptor.encrypt(requestDTO.getApiKey()));
        }

        return this.updateById(model);
    }

    @Override
    @Transactional
    public boolean deleteModel(String modelCode, Long operatorId) {
        AIModel model = this.getOne(new LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, modelCode));
        if (model == null) {
            throw new GeneralBusinessException("AI模型不存在：" + modelCode);
        }

        if (Boolean.TRUE.equals(model.getIsDefault())) {
            throw new GeneralBusinessException("默认模型不能删除");
        }

        return this.removeById(model.getId());
    }

    @Override
    @Transactional
    public boolean switchDefaultModel(String modelCode, Long operatorId) {
        // 取消当前默认模型
        LambdaUpdateWrapper<AIModel> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AIModel::getIsDefault, true)
                .set(AIModel::getIsDefault, false);
        this.update(updateWrapper);

        // 设置新的默认模型
        AIModel model = this.getOne(new LambdaQueryWrapper<AIModel>()
                .eq(AIModel::getModelCode, modelCode));
        if (model == null) {
            throw new GeneralBusinessException("AI模型不存在：" + modelCode);
        }

        model.setIsDefault(true);
        model.setIsAvailable(true);
        return this.updateById(model);
    }


    @Override
    @Transactional
    public boolean toggleModelStatus(String modelCode, Boolean isAvailable, Long operatorId) {
        AIModel model = this.getOne(new LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, modelCode));
        if (model == null) {
            throw new GeneralBusinessException("AI模型不存在：" + modelCode);
        }

        if (Boolean.TRUE.equals(model.getIsDefault()) && Boolean.FALSE.equals(isAvailable)) {
            throw new GeneralBusinessException("默认模型不能禁用");
        }

        model.setIsAvailable(isAvailable);
        return this.updateById(model);
    }

    @Override
    public List<AIModelDTO.ModelStatsVO> getModelStats(LocalDate startDate, LocalDate endDate) {
        // 设置默认时间范围
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // 查询统计数据
        LambdaQueryWrapper<AIUsageStatistics> wrapper = new LambdaQueryWrapper<AIUsageStatistics>()
                .ge(AIUsageStatistics::getStatDate, startDate)
                .le(AIUsageStatistics::getStatDate, endDate);

        List<AIUsageStatistics> statistics = usageStatisticsMapper.selectList(wrapper);

        // 按模型分组统计
        return statistics.stream()
                .collect(Collectors.groupingBy(AIUsageStatistics::getModelCode))
                .entrySet().stream()
                .map(entry -> {
                    String modelCode = entry.getKey();
                    List<AIUsageStatistics> modelStats = entry.getValue();

                    AIModelDTO.ModelStatsVO vo = new AIModelDTO.ModelStatsVO();
                    vo.setModelCode(modelCode);
                    vo.setTotalCalls(modelStats.stream().mapToLong(s -> s.getTotalRequests().longValue()).sum());
                    vo.setSuccessCalls(modelStats.stream().mapToLong(s -> s.getSuccessRequests().longValue()).sum());
                    vo.setTotalCost(modelStats.stream().reduce(java.math.BigDecimal.ZERO,
                            (sum, s) -> sum.add(s.getTotalCost()), java.math.BigDecimal::add));

                    // 计算平均响应时间和成功率
                    double avgResponseTime = modelStats.stream().mapToDouble(s -> s.getAvgResponseTime().doubleValue()).average().orElse(0.0);
                    vo.setAvgResponseTime((int) avgResponseTime);
                    vo.setSuccessRate(vo.getTotalCalls() > 0 ? (double) vo.getSuccessCalls() / vo.getTotalCalls() * 100 : 0.0);

                    // 获取模型名称
                    AIModel model = this.getOne(new LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, modelCode));
                    vo.setModelName(model != null ? model.getModelName() : modelCode);

                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean testModelConnection(String modelCode) {
        AIModel model = this.getOne(new LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, modelCode));
        if (model == null) {
            throw new GeneralBusinessException("AI模型不存在：" + modelCode);
        }

        // TODO: 实现具体的AI模型连接测试逻辑
        // 这里应该根据不同的提供商调用相应的API进行测试

        return true; // 暂时返回true，实际应该进行真实的连接测试
    }

    @Override
    public List<String> getAllProviders() {
        return this.list().stream()
                .map(AIModel::getProvider)
                .distinct()
                .collect(Collectors.toList());
    }

    /** 实体转换 DTO */
    private AIModelDTO convertToDTO(AIModel entity) {
        if (entity == null) return null;

        AIModelDTO dto = new AIModelDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }


    /**
     * 在真正需要调用AI服务时，需要先解密apiKey
     * 示例方法 (此方法仅为演示，实际应在AI调用处实现)
     */
    public String getDecryptedApiKey(String modelCode) {
        AIModel aiModel = this.lambdaQuery().eq(AIModel::getModelCode, modelCode).one();
        if (aiModel != null && StringUtils.hasText(aiModel.getApiKey())) {
            return stringEncryptor.decrypt(aiModel.getApiKey());
        }
        return null;
    }
}