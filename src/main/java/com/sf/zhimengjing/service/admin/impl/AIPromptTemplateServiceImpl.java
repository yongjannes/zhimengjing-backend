package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.ai.AIPromptTemplateDTO;
import com.sf.zhimengjing.entity.admin.AIPromptTemplate;
import com.sf.zhimengjing.mapper.admin.AIPromptTemplateMapper;
import com.sf.zhimengjing.service.admin.AIPromptTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Title: AIPromptTemplateServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: AI提示词模板服务实现类，提供模板管理相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIPromptTemplateServiceImpl extends ServiceImpl<AIPromptTemplateMapper, AIPromptTemplate> implements AIPromptTemplateService {

    @Override
    public IPage<AIPromptTemplateDTO> getTemplates(Page<AIPromptTemplateDTO> page, String templateType) {
        // 构造查询条件
        LambdaQueryWrapper<AIPromptTemplate> wrapper = new LambdaQueryWrapper<AIPromptTemplate>()
                .eq(StringUtils.hasText(templateType), AIPromptTemplate::getTemplateType, templateType)
                .orderByDesc(AIPromptTemplate::getIsActive)
                .orderByDesc(AIPromptTemplate::getCreateTime);

        // 执行分页查询
        IPage<AIPromptTemplate> entityPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);

        // 转换为 DTO 并返回
        return entityPage.convert(this::convertToDTO);
    }

    @Override
    public AIPromptTemplateDTO getTemplateDetail(String templateCode) {
        AIPromptTemplate template = this.getOne(new LambdaQueryWrapper<AIPromptTemplate>()
                .eq(AIPromptTemplate::getTemplateCode, templateCode));

        if (template == null) {
            throw new GeneralBusinessException("模板不存在：" + templateCode);
        }

        return convertToDTO(template);
    }

    @Override
    @Transactional
    public boolean createTemplate(AIPromptTemplateDTO.TemplateRequestDTO requestDTO, Long operatorId) {
        // 检查模板编码是否已存在
        boolean exists = this.count(new LambdaQueryWrapper<AIPromptTemplate>()
                .eq(AIPromptTemplate::getTemplateCode, requestDTO.getTemplateCode())) > 0;
        if (exists) {
            throw new GeneralBusinessException("模板编码已存在：" + requestDTO.getTemplateCode());
        }

        // 创建提示词模板
        AIPromptTemplate template = new AIPromptTemplate();
        BeanUtils.copyProperties(requestDTO, template);
        template.setVersion(1);
        template.setIsActive(true);
        template.setCreatedBy(operatorId);

        return this.save(template);
    }

    @Override
    @Transactional
    public boolean updateTemplate(String templateCode, AIPromptTemplateDTO.TemplateRequestDTO requestDTO, Long operatorId) {
        AIPromptTemplate template = this.getOne(new LambdaQueryWrapper<AIPromptTemplate>()
                .eq(AIPromptTemplate::getTemplateCode, templateCode));

        if (template == null) {
            throw new GeneralBusinessException("模板不存在：" + templateCode);
        }

        // 更新模板信息，版本号+1
        BeanUtils.copyProperties(requestDTO, template);
        template.setVersion(template.getVersion() + 1);

        return this.updateById(template);
    }

    @Override
    @Transactional
    public boolean deleteTemplate(String templateCode, Long operatorId) {
        AIPromptTemplate template = this.getOne(new LambdaQueryWrapper<AIPromptTemplate>()
                .eq(AIPromptTemplate::getTemplateCode, templateCode));

        if (template == null) {
            throw new GeneralBusinessException("模板不存在：" + templateCode);
        }

        return this.removeById(template.getId());
    }

    @Override
    @Transactional
    public boolean toggleTemplateStatus(String templateCode, Boolean isActive, Long operatorId) {
        AIPromptTemplate template = this.getOne(new LambdaQueryWrapper<AIPromptTemplate>()
                .eq(AIPromptTemplate::getTemplateCode, templateCode));

        if (template == null) {
            throw new GeneralBusinessException("模板不存在：" + templateCode);
        }

        template.setIsActive(isActive);
        return this.updateById(template);
    }

    @Override
    public String renderTemplate(String templateCode, Map<String, Object> variables) {
        AIPromptTemplate template = this.getOne(new LambdaQueryWrapper<AIPromptTemplate>()
                .eq(AIPromptTemplate::getTemplateCode, templateCode)
                .eq(AIPromptTemplate::getIsActive, true));

        if (template == null) {
            throw new GeneralBusinessException("活跃模板不存在：" + templateCode);
        }

        log.info("渲染模板：{}", template.getTemplateContent());
        return renderTemplateContent(template.getTemplateContent(), variables);
    }

    @Override
    public String testTemplate(AIPromptTemplateDTO.TemplateTestDTO testDTO) {
        AIPromptTemplate template = this.getOne(new LambdaQueryWrapper<AIPromptTemplate>()
                .eq(AIPromptTemplate::getTemplateCode, testDTO.getTemplateCode()));

        if (template == null) {
            throw new GeneralBusinessException("模板不存在：" + testDTO.getTemplateCode());
        }

        // 使用测试变量渲染模板
        String renderedContent = renderTemplateContent(template.getTemplateContent(), testDTO.getTestVariables());

        // 如果有测试内容，可以进一步处理
        if (StringUtils.hasText(testDTO.getTestContent())) {
            // 在渲染后的模板中替换测试内容占位符
            renderedContent = renderedContent.replace("{{dream_content}}", testDTO.getTestContent());
        }

        return renderedContent;
    }

    @Override
    public List<String> getAllTemplateTypes() {
        return this.list().stream()
                .map(AIPromptTemplate::getTemplateType)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public String getTemplateContentByModelAndType(String modelCode, String templateType) {
        log.info("根据模型编码和模板类型查找提示词模板：modelCode={}, templateType={}", modelCode, templateType);

        // 优先查找特定模型的模板
        AIPromptTemplate specificTemplate = this.getOne(new LambdaQueryWrapper<AIPromptTemplate>()
                .eq(AIPromptTemplate::getModelCode, modelCode)
                .eq(AIPromptTemplate::getTemplateType, templateType)
                .eq(AIPromptTemplate::getIsActive, true)
                .orderByDesc(AIPromptTemplate::getVersion)
                .last("limit 1"));

        if (specificTemplate != null) {
            log.info("找到特定模型的模板：templateCode={}", specificTemplate.getTemplateCode());
            return specificTemplate.getTemplateContent();
        }

        // 如果没有找到特定模型的模板，查找通用模板（modelCode为null）
        AIPromptTemplate generalTemplate = this.getOne(new LambdaQueryWrapper<AIPromptTemplate>()
                .isNull(AIPromptTemplate::getModelCode)
                .eq(AIPromptTemplate::getTemplateType, templateType)
                .eq(AIPromptTemplate::getIsActive, true)
                .orderByDesc(AIPromptTemplate::getVersion)
                .last("limit 1"));

        if (generalTemplate != null) {
            log.info("找到通用模板：templateCode={}", generalTemplate.getTemplateCode());
            return generalTemplate.getTemplateContent();
        }

        log.warn("未找到模型 {} 类型为 {} 的提示词模板", modelCode, templateType);
        return null;
    }

    /**
     * 渲染模板内容
     * 支持 {{variable}} 格式的变量替换
     */
    private String renderTemplateContent(String templateContent, Map<String, Object> variables) {
        if (!StringUtils.hasText(templateContent) || variables == null || variables.isEmpty()) {
            return templateContent;
        }

        String result = templateContent;

        // 使用正则表达式匹配 {{variable}} 格式的变量
        Pattern pattern = Pattern.compile("\\{\\{(\\w+)\\}\\}");
        Matcher matcher = pattern.matcher(templateContent);

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.get(variableName);

            if (value != null) {
                result = result.replace("{{" + variableName + "}}", value.toString());
            }
        }

        return result;
    }

    /** 实体转换 DTO */
    private AIPromptTemplateDTO convertToDTO(AIPromptTemplate entity) {
        if (entity == null) return null;

        AIPromptTemplateDTO dto = new AIPromptTemplateDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}