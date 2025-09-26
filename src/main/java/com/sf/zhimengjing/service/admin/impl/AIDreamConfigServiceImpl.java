package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.ai.AIDreamConfigDTO;
import com.sf.zhimengjing.entity.admin.AIDreamConfig;
import com.sf.zhimengjing.entity.admin.AIModel;
import com.sf.zhimengjing.mapper.admin.AIDreamConfigMapper;
import com.sf.zhimengjing.mapper.admin.AIPromptTemplateMapper;
import com.sf.zhimengjing.service.admin.AIDreamConfigService;
import com.sf.zhimengjing.service.admin.AIModelService;
import com.sf.zhimengjing.service.admin.AIPromptTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class AIDreamConfigServiceImpl extends ServiceImpl<AIDreamConfigMapper, AIDreamConfig> implements AIDreamConfigService {

    private final AIModelService aiModelService;
    private final AIPromptTemplateMapper promptTemplateMapper;
    private final AIPromptTemplateService aiPromptTemplateService;

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
        dto.setCustomPrompt("");

        // 根据默认解析模式获取对应的模板类型
        String targetTemplateType = getTemplateTypeByAnalysisMode("comprehensive");
        String templateContent = null;

        // 尝试获取对应解析模式的通用模板
        if (StringUtils.hasText(targetTemplateType)) {
            templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                    null, targetTemplateType);  // modelCode为null表示通用模板
        }

        // 如果没有找到，回退到默认模板类型
        if (!StringUtils.hasText(templateContent)) {
            String[] fallbackTypes = {"system_prompt", "dream_analysis", "ai_system", "default_prompt"};
            for (String fallbackType : fallbackTypes) {
                templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                        null, fallbackType);
                if (StringUtils.hasText(templateContent)) {
                    break;
                }
            }
        }

        dto.setCustomPrompt(StringUtils.hasText(templateContent) ? templateContent : "");
        return dto;
    }


    /**
     * 为指定模型创建默认配置
     */
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

        dto.setCustomPrompt("");

        // 根据默认解析模式获取对应的模板类型
        String targetTemplateType = getTemplateTypeByAnalysisMode("comprehensive");
        String templateContent = null;
        // 先尝试特定模型的模板
        if (StringUtils.hasText(targetTemplateType)) {
            templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                    modelCode, targetTemplateType);
        }

        // 如果没有特定模型的模板，尝试通用模板
        if (!StringUtils.hasText(templateContent) && StringUtils.hasText(targetTemplateType)) {
            templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                    null, targetTemplateType);
        }

        // 如果还是没有，回退到默认模板类型
        if (!StringUtils.hasText(templateContent)) {
            String[] fallbackTypes = {"system_prompt", "dream_analysis", "ai_system", "default_prompt"};
            for (String fallbackType : fallbackTypes) {
                templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                        modelCode, fallbackType);
                if (StringUtils.hasText(templateContent)) {
                    break;
                }
                templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                        null, fallbackType);
                if (StringUtils.hasText(templateContent)) {
                    break;
                }
            }
        }

        dto.setCustomPrompt(StringUtils.hasText(templateContent) ? templateContent : "");

        return dto;
    }


    /**
     * 实体转换 DTO
     */
    private AIDreamConfigDTO convertToDTO(AIDreamConfig entity) {
        if (entity == null) return null;

        AIDreamConfigDTO dto = new AIDreamConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        // 如果 custom_prompt 为空，从提示词模板系统获取
        if (!StringUtils.hasText(dto.getCustomPrompt())) {
            log.info("模型 {} 的 custom_prompt 为空，尝试从模板系统获取", entity.getModelCode());


            // 根据解析模式映射到对应的模板类型
            String targetTemplateType = getTemplateTypeByAnalysisMode(entity.getAnalysisMode());
            log.info("根据解析模式 {} 映射到模板类型: {}", entity.getAnalysisMode(), targetTemplateType);

            String templateContent = null;
            String foundTemplateType = null;

            // 首先尝试获取特定解析模式对应的模板类型
            if (StringUtils.hasText(targetTemplateType)) {
                // 先尝试特定模型的模板
                templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                        entity.getModelCode(), targetTemplateType);
                if (StringUtils.hasText(templateContent)) {
                    foundTemplateType = targetTemplateType;
                    log.info("成功从特定模型模板获取到提示词，模型: {}, 类型: {}, 长度: {}",
                            entity.getModelCode(), targetTemplateType, templateContent.length());
                } else {
                    // 如果没有特定模型的模板，尝试通用模板
                    templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                            null, targetTemplateType);
                    if (StringUtils.hasText(templateContent)) {
                        foundTemplateType = targetTemplateType;
                        log.info("成功从通用模板获取到提示词，模型: {}, 类型: {}, 长度: {}",
                                entity.getModelCode(), targetTemplateType, templateContent.length());
                    }
                }
            }

            // 如果还没找到，回退到默认的模板类型
            if (!StringUtils.hasText(templateContent)) {
                log.warn("未找到解析模式 {} 对应的模板，尝试默认模板类型", entity.getAnalysisMode());
                String[] fallbackTemplateTypes = {"system_prompt", "dream_analysis", "ai_system", "default_prompt"};

                for (String templateType : fallbackTemplateTypes) {
                    // 先尝试特定模型的模板
                    templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                            entity.getModelCode(), templateType);
                    if (StringUtils.hasText(templateContent)) {
                        foundTemplateType = templateType;
                        log.info("成功从特定模型模板获取到提示词，模型: {}, 类型: {}, 长度: {}",
                                entity.getModelCode(), templateType, templateContent.length());
                        break;
                    }

                    templateContent = aiPromptTemplateService.getTemplateContentByModelAndType(
                            null, templateType);

                    if (StringUtils.hasText(templateContent)) {
                        foundTemplateType = templateType;
                        log.info("成功从通用模板获取到提示词，模型: {}, 类型: {}, 长度: {}",
                                entity.getModelCode(), templateType, templateContent.length());
                        break;
                    }
                }
            }

            if (StringUtils.hasText(templateContent)) {
                dto.setCustomPrompt(templateContent);
                log.info("模型 {} 最终使用模板类型: {}", entity.getModelCode(), foundTemplateType);
            } else {
                List<String> availableTypes = aiPromptTemplateService.getAllTemplateTypes();
                log.warn("未找到模型 {} 解析模式 {} 的任何提示词模板，数据库中可用类型: {}",
                        entity.getModelCode(), entity.getAnalysisMode(), availableTypes);
            }
        } else {
            log.info("模型 {} 使用数据库中的 custom_prompt，长度: {}", entity.getModelCode(), dto.getCustomPrompt().length());
        }

        return dto;
    }

    /**
     * 根据解析模式映射到对应的模板类型
     */
    private String getTemplateTypeByAnalysisMode(String analysisMode) {
        if (!StringUtils.hasText(analysisMode)) {
            return null;
        }

        return switch (analysisMode.toLowerCase()) {
            case "comprehensive" -> "dream_analysis_system";
            case "psychology" -> "emotion_analysis";
            case "symbolic" -> "tag_generation";
            case "emotion" -> "emotion_analysis";
            case "basic" -> "dream_analysis";
            default -> {
                log.warn("未知的解析模式: {}, 使用默认模板类型", analysisMode);
                yield "dream_analysis_system";
            }
        };
    }


}