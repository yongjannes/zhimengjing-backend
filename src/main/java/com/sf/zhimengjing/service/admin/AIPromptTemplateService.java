package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.ai.AIPromptTemplateDTO;
import com.sf.zhimengjing.entity.admin.AIPromptTemplate;

import java.util.List;
import java.util.Map;

/**
 * @Title: AIPromptTemplateService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: AI提示词模板服务接口，提供模板管理相关业务操作
 */
public interface AIPromptTemplateService extends IService<AIPromptTemplate> {

    /** 获取提示词模板列表（分页） */
    IPage<AIPromptTemplateDTO> getTemplates(Page<AIPromptTemplateDTO> page, String templateType);

    /** 根据模板编码获取模板详情 */
    AIPromptTemplateDTO getTemplateDetail(String templateCode);

    /** 创建提示词模板 */
    boolean createTemplate(AIPromptTemplateDTO.TemplateRequestDTO requestDTO, Long operatorId);

    /** 更新提示词模板 */
    boolean updateTemplate(String templateCode, AIPromptTemplateDTO.TemplateRequestDTO requestDTO, Long operatorId);

    /** 删除提示词模板 */
    boolean deleteTemplate(String templateCode, Long operatorId);

    /** 切换模板状态 */
    boolean toggleTemplateStatus(String templateCode, Boolean isActive, Long operatorId);

    /** 渲染模板 */
    String renderTemplate(String templateCode, Map<String, Object> variables);

    /** 测试模板 */
    String testTemplate(AIPromptTemplateDTO.TemplateTestDTO testDTO);

    /** 获取所有模板类型 */
    List<String> getAllTemplateTypes();

    /** 根据模型编码和模板类型获取提示词模板内容 */
    String getTemplateContentByModelAndType(String modelCode, String templateType);


}