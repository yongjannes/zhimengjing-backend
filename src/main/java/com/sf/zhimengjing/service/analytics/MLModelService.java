package com.sf.zhimengjing.service.analytics;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.analytics.MLModelTrainDTO;
import com.sf.zhimengjing.common.model.vo.analytics.MLModelVO;
import com.sf.zhimengjing.common.model.vo.analytics.ModelPerformanceVO;
import com.sf.zhimengjing.common.model.vo.analytics.PredictionResultVO;
import com.sf.zhimengjing.entity.analytics.MLModel;

import java.util.List;
import java.util.Map;

/**
 * @Title: MLModelService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.analytics
 * @Description: 机器学习模型服务接口
 */
public interface MLModelService extends IService<MLModel> {

    /**
     * 训练模型
     *
     * @param trainDTO 模型训练参数
     * @return 模型基本信息
     */
    MLModelVO trainModel(MLModelTrainDTO trainDTO);

    /**
     * 部署模型
     *
     * @param modelId 模型ID
     */
    void deployModel(Long modelId);

    /**
     * 预测
     *
     * @param modelId   模型ID
     * @param inputData 输入数据
     * @return 预测结果
     */
    PredictionResultVO predict(Long modelId, Map<String, Object> inputData);

    /**
     * 获取模型性能指标
     *
     * @param modelId 模型ID
     * @return 模型性能
     */
    ModelPerformanceVO getModelPerformance(Long modelId);

    /**
     * 获取可用模型列表
     *
     * @param modelType 模型类型
     * @return 可用模型列表
     */
    List<MLModelVO> getAvailableModels(String modelType);
}