package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.ai.AIModelDTO;
import com.sf.zhimengjing.entity.admin.AIModel;

import java.time.LocalDate;
import java.util.List;

/**
 * @Title: AIModelService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: AI模型服务接口，提供AI模型相关业务操作
 */
public interface AIModelService extends IService<AIModel> {

    /** 获取可用AI模型列表（分页） */
    IPage<AIModelDTO> getAvailableModels(Page<AIModelDTO> page, String provider);

    /** 获取单个AI模型详情 */
    AIModelDTO getModelDetail(String modelCode);

    /** 创建AI模型 */
    boolean createModel(AIModelDTO.ModelRequestDTO requestDTO, Long operatorId);

    /** 更新AI模型配置 */
    boolean updateModel(String modelCode, AIModelDTO.ModelRequestDTO requestDTO, Long operatorId);

    /** 删除AI模型 */
    boolean deleteModel(String modelCode, Long operatorId);

    /** 切换默认模型 */
    boolean switchDefaultModel(String modelCode, Long operatorId);

    /** 启用/禁用AI模型 */
    boolean toggleModelStatus(String modelCode, Boolean isAvailable, Long operatorId);

    /** 获取AI模型统计信息 */
    List<AIModelDTO.ModelStatsVO> getModelStats(LocalDate startDate, LocalDate endDate);

    /** 测试AI模型连接 */
    boolean testModelConnection(String modelCode);

    /** 获取所有提供商列表 */
    List<String> getAllProviders();
}