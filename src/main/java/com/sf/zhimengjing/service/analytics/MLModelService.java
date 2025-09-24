package com.sf.zhimengjing.service.analytics;

import com.sf.zhimengjing.common.model.dto.analytics.MLModelTrainDTO;
import com.sf.zhimengjing.common.model.vo.analytics.MLModelVO;
import com.sf.zhimengjing.common.model.vo.analytics.ModelPerformanceVO;
import com.sf.zhimengjing.common.model.vo.analytics.PredictionResultVO;
import com.sf.zhimengjing.entity.analytics.MLModel;

import java.util.List;
import java.util.Map;

/**
 * @Title: MLModelService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.analytics
 * @description: 机器学习模型服务接口
 *               提供模型训练、部署、预测以及性能评估等功能。
 */
public interface MLModelService {
    /**
     * 训练模型
     */
    MLModel trainModel(MLModelTrainDTO trainDTO);

    /**
     * 部署模型
     */
    void deployModel(Long modelId);

    /**
     * 预测
     */
    PredictionResultVO predict(Long modelId, Map<String, Object> inputData);

    /**
     * 获取模型性能指标
     */
    ModelPerformanceVO getModelPerformance(Long modelId);

    /**
     * 获取可用模型列表
     */
    List<MLModelVO> getAvailableModels(String modelType);
}