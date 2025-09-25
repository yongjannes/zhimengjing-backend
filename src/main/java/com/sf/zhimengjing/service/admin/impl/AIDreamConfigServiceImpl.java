package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.ai.AIDreamConfigDTO;
import com.sf.zhimengjing.entity.admin.AIDreamConfig;
import com.sf.zhimengjing.entity.admin.AIModel;
import com.sf.zhimengjing.mapper.admin.AIDreamConfigMapper;
import com.sf.zhimengjing.service.admin.AIDreamConfigService;
import com.sf.zhimengjing.service.admin.AIModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: AIDreamConfigServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: AI梦境解析配置服务实现类，提供梦境解析配置相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AIDreamConfigServiceImpl extends ServiceImpl<AIDreamConfigMapper, AIDreamConfig> implements AIDreamConfigService {

    private final AIModelService aiModelService;

    @Override
    public List<AIDreamConfigDTO> getAllDreamConfigs() {
        List<AIDreamConfig> configs = this.list(new LambdaQueryWrapper<AIDreamConfig>()
                .orderByDesc(AIDreamConfig::getIsActive)
                .orderByAsc(AIDreamConfig::getCreateTime));

        return configs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AIDreamConfigDTO getDreamConfigByModel(String modelCode) {
        AIDreamConfig config = this.getOne(new LambdaQueryWrapper<AIDreamConfig>()
                .eq(AIDreamConfig::getModelCode, modelCode));

        if (config == null) {
            // 如果没有配置，返回默认配置
            return createDefaultConfigForModel(modelCode);
        }

        return convertToDTO(config);
    }

    @Override
    @Transactional
    public boolean saveDreamConfig(AIDreamConfigDTO.DreamConfigRequestDTO requestDTO, Long operatorId) {
        // 验证AI模型是否存在
        AIModel model = aiModelService.getOne(new LambdaQueryWrapper<AIModel>()
                .eq(AIModel::getModelCode, requestDTO.getModelCode()));
        if (model == null) {
            throw new GeneralBusinessException("AI模型不存在：" + requestDTO.getModelCode());
        }

        // 查找是否已存在配置
        AIDreamConfig existingConfig = this.getOne(new LambdaQueryWrapper<AIDreamConfig>()
                .eq(AIDreamConfig::getModelCode, requestDTO.getModelCode()));

        if (existingConfig != null) {
            // 更新现有配置
            BeanUtils.copyProperties(requestDTO, existingConfig);
            existingConfig.setIsActive(true);
            return this.updateById(existingConfig);
        } else {
            // 创建新配置
            AIDreamConfig newConfig = new AIDreamConfig();
            BeanUtils.copyProperties(requestDTO, newConfig);
            newConfig.setIsActive(true);
            return this.save(newConfig);
        }
    }

    @Override
    @Transactional
    public boolean deleteDreamConfig(String modelCode, Long operatorId) {
        AIDreamConfig config = this.getOne(new LambdaQueryWrapper<AIDreamConfig>()
                .eq(AIDreamConfig::getModelCode, modelCode));

        if (config == null) {
            throw new GeneralBusinessException("配置不存在：" + modelCode);
        }

        return this.removeById(config.getId());
    }

    @Override
    @Transactional
    public boolean toggleConfigStatus(String modelCode, Boolean isActive, Long operatorId) {
        AIDreamConfig config = this.getOne(new LambdaQueryWrapper<AIDreamConfig>()
                .eq(AIDreamConfig::getModelCode, modelCode));

        if (config == null) {
            throw new GeneralBusinessException("配置不存在：" + modelCode);
        }

        config.setIsActive(isActive);
        return this.updateById(config);
    }

    @Override
    public String testDreamAnalysis(String modelCode, String dreamContent) {
        AIDreamConfigDTO config = getDreamConfigByModel(modelCode);

        // TODO: 实现具体的AI梦境解析测试逻辑
        // 这里应该调用相应的AI API进行梦境解析测试

        return "这是一个测试解析结果，基于模型：" + modelCode + "，配置模式：" + config.getAnalysisMode() +
                "。实际使用中，这里会调用真实的AI API进行梦境解析。";
    }

    @Override
    public AIDreamConfigDTO getDefaultConfig() {
        AIDreamConfig defaultConfig = this.getOne(new LambdaQueryWrapper<AIDreamConfig>()
                .eq(AIDreamConfig::getIsActive, true)
                .orderByAsc(AIDreamConfig::getCreateTime)
                .last("limit 1"));

        if (defaultConfig != null) {
            return convertToDTO(defaultConfig);
        }

        // 如果没有任何配置，返回系统默认配置
        AIDreamConfigDTO dto = new AIDreamConfigDTO();
        dto.setAnalysisMode("comprehensive");
        dto.setAnalysisDepth("detailed");
        dto.setLanguageStyle("friendly");
        dto.setAnalysisLength("medium");
        dto.setEnableEmotionAnalysis(true);
        dto.setEnableTagGeneration(true);
        dto.setEnableSuggestion(true);
        dto.setCustomPrompt("你是一位专业的梦境解析师，具有深厚的心理学背景...");

        return dto;
    }

    /** 为指定模型创建默认配置 */
    private AIDreamConfigDTO createDefaultConfigForModel(String modelCode) {
        AIDreamConfigDTO dto = new AIDreamConfigDTO();
        dto.setModelCode(modelCode);
        dto.setAnalysisMode("comprehensive");
        dto.setAnalysisDepth("detailed");
        dto.setLanguageStyle("friendly");
        dto.setAnalysisLength("medium");
        dto.setEnableEmotionAnalysis(true);
        dto.setEnableTagGeneration(true);
        dto.setEnableSuggestion(true);
        dto.setIsActive(false);

        // 根据不同AI模型设置不同的默认提示词
        if (StringUtils.hasText(modelCode)) {
            switch (modelCode) {
                case "deepseek-chat":
                    dto.setCustomPrompt("你是一位专业的梦境解析师，擅长从心理学角度进行深入分析，能够从多个维度解读梦境含义...");
                    break;
                case "glm-4":
                    dto.setCustomPrompt("基于心理学理论，对梦境进行专业分析，注重深层心理含义和情感解读...");
                    break;
                case "qwen-turbo":
                    dto.setCustomPrompt("从象征意义角度解读梦境内容，关注文化和情感层面，提供温暖的分析...");
                    break;
                default:
                    dto.setCustomPrompt("你是一位专业的梦境解析师，具有深厚的心理学背景...");
            }
        }

        return dto;
    }

    /** 实体转换 DTO */
    private AIDreamConfigDTO convertToDTO(AIDreamConfig entity) {
        if (entity == null) return null;

        AIDreamConfigDTO dto = new AIDreamConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}