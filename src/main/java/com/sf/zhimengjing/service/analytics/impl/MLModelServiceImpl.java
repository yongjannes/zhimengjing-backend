package com.sf.zhimengjing.service.analytics.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.analytics.MLModelTrainDTO;
import com.sf.zhimengjing.common.model.vo.analytics.MLModelVO;
import com.sf.zhimengjing.common.model.vo.analytics.ModelPerformanceVO;
import com.sf.zhimengjing.common.model.vo.analytics.PredictionResultVO;
import com.sf.zhimengjing.entity.analytics.MLModel;
import com.sf.zhimengjing.mapper.analytics.MLModelMapper;
import com.sf.zhimengjing.service.analytics.MLModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Title: MLModelServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.analytics.impl
 * @Description: 机器学习模型服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MLModelServiceImpl extends ServiceImpl<MLModelMapper, MLModel> implements MLModelService {

    private final MLModelMapper mlModelMapper;

    /**
     * 训练模型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MLModel trainModel(MLModelTrainDTO trainDTO) {
        log.info("开始训练模型，模型类型：{}，模型名称：{}", trainDTO.getModelType(), trainDTO.getModelName());

        try {
            // 【修正】移除本地方法校验，依赖于 Controller 层的 @Valid 注解
            // 检查是否已存在相同名称的模型
            LambdaQueryWrapper<MLModel> wrapper = new LambdaQueryWrapper<MLModel>()
                    .eq(MLModel::getModelName, trainDTO.getModelName());

            if (this.getOne(wrapper) != null) {
                throw new GeneralBusinessException("已存在同名的模型");
            }

            // 【修正】使用 Setter 创建模型实体，而非 Builder
            MLModel model = new MLModel();
            model.setModelName(trainDTO.getModelName());
            model.setModelType(trainDTO.getModelType());

            // 【修正】为 version 和 algorithm 设置默认值或从参数中提取
            model.setModelVersion("1.0.0"); // 设置默认版本号
            model.setAlgorithmName(
                    trainDTO.getHyperparameters().getOrDefault("algorithm", "default_algorithm").toString()
            );

            // 【修正】序列化参数为JSON字符串
            model.setModelParameters(JSONUtil.toJsonStr(trainDTO.getHyperparameters()));

            // 【修正】从 datasetConfig 中获取训练数据大小
            Object dataSize = trainDTO.getDatasetConfig().get("size");
            if (dataSize instanceof Number) {
                model.setTrainingDataSize(((Number) dataSize).longValue());
            }

            model.setModelStatus("training"); // 设置初始状态
            // createTime 和 updateTime 将由 MyMetaObjectHandler 自动填充

            // 保存模型记录
            this.save(model);

            // 启动异步训练任务
            startAsyncTraining(model);

            // 【修正】使用 getId() 获取模型ID
            log.info("模型训练任务已启动，模型ID：{}", model.getId());
            return model;

        } catch (GeneralBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("模型训练启动失败", e);
            throw new GeneralBusinessException("模型训练启动失败：" + e.getMessage());
        }
    }

    /**
     * 部署模型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deployModel(Long modelId) {
        log.info("开始部署模型，模型ID：{}", modelId);

        MLModel model = this.getById(modelId);
        if (model == null) {
            throw new GeneralBusinessException("模型不存在");
        }

        if (!"completed".equals(model.getModelStatus())) {
            throw new GeneralBusinessException("只有训练完成的模型才能部署");
        }

        try {
            // 检查模型性能是否满足部署要求
            if (model.getAccuracyScore() != null &&
                    model.getAccuracyScore().compareTo(new BigDecimal("70.0")) < 0) {
                throw new GeneralBusinessException("模型准确率过低，不满足部署要求");
            }

            // 停用其他同类型的已部署模型
            LambdaQueryWrapper<MLModel> wrapper = new LambdaQueryWrapper<MLModel>()
                    .eq(MLModel::getModelType, model.getModelType())
                    .eq(MLModel::getModelStatus, "deployed")
                    .ne(MLModel::getId, modelId); // 【修正】使用 getId()

            List<MLModel> deployedModels = this.list(wrapper);
            for (MLModel deployedModel : deployedModels) {
                deployedModel.setModelStatus("disabled");
                // updateTime 会自动更新
                this.updateById(deployedModel);
            }

            // 部署当前模型
            model.setModelStatus("deployed");
            model.setDeployedAt(LocalDateTime.now());
            this.updateById(model);

            log.info("模型部署成功，模型ID：{}", modelId);

        } catch (GeneralBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("模型部署失败，模型ID：{}", modelId, e);
            throw new GeneralBusinessException("模型部署失败：" + e.getMessage());
        }
    }

    /**
     * 预测
     */
    @Override
    public PredictionResultVO predict(Long modelId, Map<String, Object> inputData) {
        log.info("开始模型预测，模型ID：{}，输入数据键数量：{}", modelId, inputData.size());

        MLModel model = this.getById(modelId);
        if (model == null) {
            throw new GeneralBusinessException("模型不存在");
        }

        if (!"deployed".equals(model.getModelStatus())) {
            throw new GeneralBusinessException("只有已部署的模型才能进行预测");
        }

        try {
            validateInputData(model.getModelType(), inputData);
            Map<String, Object> predictionResult = performPrediction(model, inputData);
            BigDecimal confidenceScore = calculateConfidence(model, predictionResult);
            String explanation = generateExplanation(model.getModelType(), predictionResult);
            List<String> recommendations = generateRecommendations(model.getModelType(), predictionResult);

            return PredictionResultVO.builder()
                    .modelName(model.getModelName())
                    .predictionType(model.getModelType())
                    .predictionResult(predictionResult)
                    .confidenceScore(confidenceScore)
                    .predictionTime(LocalDateTime.now())
                    .explanation(explanation)
                    .recommendedActions(recommendations)
                    .build();

        } catch (GeneralBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("模型预测失败，模型ID：{}", modelId, e);
            throw new GeneralBusinessException("模型预测失败：" + e.getMessage());
        }
    }

    /**
     * 获取模型性能指标
     */
    @Override
    public ModelPerformanceVO getModelPerformance(Long modelId) {
        log.info("获取模型性能指标，模型ID：{}", modelId);

        MLModel model = this.getById(modelId);
        if (model == null) {
            throw new GeneralBusinessException("模型不存在");
        }

        String performanceGrade = calculatePerformanceGrade(model);
        List<String> improvements = generateImprovementSuggestions(model);

        return ModelPerformanceVO.builder()
                .accuracyScore(model.getAccuracyScore() != null ? model.getAccuracyScore() : BigDecimal.ZERO)
                .precisionScore(model.getPrecisionScore() != null ? model.getPrecisionScore() : BigDecimal.ZERO)
                .recallScore(model.getRecallScore() != null ? model.getRecallScore() : BigDecimal.ZERO)
                .f1Score(model.getF1Score() != null ? model.getF1Score() : BigDecimal.ZERO)
                .aucScore(new BigDecimal("0.85")) // 示例值
                .lossValue(new BigDecimal("0.15")) // 示例值
                .performanceGrade(performanceGrade)
                .improvementSuggestions(improvements)
                .build();
    }

    /**
     * 获取可用模型列表
     */
    @Override
    public List<MLModelVO> getAvailableModels(String modelType) {
        log.info("获取可用模型列表，模型类型：{}", modelType);

        LambdaQueryWrapper<MLModel> wrapper = new LambdaQueryWrapper<MLModel>()
                .eq(modelType != null, MLModel::getModelType, modelType)
                .in(MLModel::getModelStatus, Arrays.asList("completed", "deployed"))
                .orderByDesc(MLModel::getCreateTime); // 【修正】使用 getCreateTime

        List<MLModel> models = this.list(wrapper);

        return models.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    // --- 私有辅助方法 ---

    // 【修正】此方法已不再需要，校验由DTO注解完成
    // private void validateTrainDTO(MLModelTrainDTO trainDTO) { ... }

    private void startAsyncTraining(MLModel model) {
        log.info("启动异步训练任务，模型ID：{}", model.getId()); // 【修正】使用 getId()

        // 模拟训练完成后更新状态
        new Thread(() -> {
            try {
                Thread.sleep(5000); // 模拟训练时间

                // 更新模型状态和性能指标
                model.setModelStatus("completed");
                model.setAccuracyScore(new BigDecimal("85.6"));
                model.setPrecisionScore(new BigDecimal("84.2"));
                model.setRecallScore(new BigDecimal("87.1"));
                model.setF1Score(new BigDecimal("85.6"));
                // updateTime 会自动更新

                this.updateById(model);
                log.info("模型训练完成，模型ID：{}", model.getId()); // 【修正】使用 getId()

            } catch (Exception e) {
                log.error("模型训练失败，模型ID：{}", model.getId(), e); // 【修正】使用 getId()
            }
        }).start();
    }

    // ... 其他私有方法保持不变 ...

    private void validateInputData(String modelType, Map<String, Object> inputData) {
        switch (modelType) {
            case "emotion_analysis":
                if (!inputData.containsKey("content")) {
                    throw new GeneralBusinessException("情感分析模型需要content参数");
                }
                break;
            case "behavior_prediction":
                if (!inputData.containsKey("userId") || !inputData.containsKey("behaviorHistory")) {
                    throw new GeneralBusinessException("行为预测模型需要userId和behaviorHistory参数");
                }
                break;
            default:
                if (inputData.isEmpty()) {
                    throw new GeneralBusinessException("输入数据不能为空");
                }
        }
    }

    private Map<String, Object> performPrediction(MLModel model, Map<String, Object> inputData) {
        Map<String, Object> result = new HashMap<>();
        switch (model.getModelType()) {
            case "emotion_analysis":
                result.put("emotion", "positive");
                result.put("score", 0.85);
                result.put("distribution", Map.of("positive", 0.85, "negative", 0.10, "neutral", 0.05));
                break;
            case "behavior_prediction":
                result.put("next_action", "vip_purchase");
                result.put("probability", 0.72);
                break;
            default:
                result.put("prediction", "unknown");
                result.put("confidence", 0.5);
        }
        return result;
    }

    private BigDecimal calculateConfidence(MLModel model, Map<String, Object> predictionResult) {
        Object confidence = predictionResult.get("confidence");
        if (confidence != null) return new BigDecimal(confidence.toString());
        Object score = predictionResult.get("score");
        if (score != null) return new BigDecimal(score.toString());
        Object probability = predictionResult.get("probability");
        if (probability != null) return new BigDecimal(probability.toString());
        return new BigDecimal("0.75");
    }

    private String generateExplanation(String modelType, Map<String, Object> predictionResult) {
        switch (modelType) {
            case "emotion_analysis": return "基于文本内容的情感分析，识别出主要情感倾向";
            case "behavior_prediction": return "基于用户历史行为数据预测下一步可能的操作";
            default: return "基于机器学习模型的预测结果";
        }
    }

    private List<String> generateRecommendations(String modelType, Map<String, Object> predictionResult) {
        List<String> recommendations = new ArrayList<>();
        switch (modelType) {
            case "emotion_analysis":
                String emotion = (String) predictionResult.get("emotion");
                if ("negative".equals(emotion)) {
                    recommendations.add("建议关注用户心理健康");
                    recommendations.add("可以推荐积极正面的内容");
                } else if ("positive".equals(emotion)) {
                    recommendations.add("用户情感状态良好");
                }
                break;
            case "behavior_prediction":
                String nextAction = (String) predictionResult.get("next_action");
                if ("vip_purchase".equals(nextAction)) {
                    recommendations.add("推荐VIP相关优惠");
                    recommendations.add("发送个性化VIP推广信息");
                }
                break;
            default:
                recommendations.add("根据预测结果制定相应策略");
        }
        return recommendations;
    }

    private String calculatePerformanceGrade(MLModel model) {
        BigDecimal accuracy = model.getAccuracyScore();
        if (accuracy == null) return "unknown";
        if (accuracy.compareTo(new BigDecimal("90")) >= 0) return "excellent";
        if (accuracy.compareTo(new BigDecimal("80")) >= 0) return "good";
        if (accuracy.compareTo(new BigDecimal("70")) >= 0) return "fair";
        return "poor";
    }

    private List<String> generateImprovementSuggestions(MLModel model) {
        List<String> suggestions = new ArrayList<>();
        BigDecimal accuracy = model.getAccuracyScore();
        if (accuracy != null && accuracy.compareTo(new BigDecimal("80")) < 0) {
            suggestions.add("增加训练数据量以提升模型准确率");
            suggestions.add("考虑调整模型参数或使用更复杂的算法");
        }
        if (suggestions.isEmpty()) {
            suggestions.add("模型性能良好，继续监控并定期更新");
        }
        return suggestions;
    }

    private MLModelVO convertToVO(MLModel model) {
        ModelPerformanceVO performance = ModelPerformanceVO.builder()
                .accuracyScore(model.getAccuracyScore())
                .precisionScore(model.getPrecisionScore())
                .recallScore(model.getRecallScore())
                .f1Score(model.getF1Score())
                .performanceGrade(calculatePerformanceGrade(model))
                .build();

        return MLModelVO.builder()
                .modelId(model.getId()) // 【修正】使用 getId()
                .modelName(model.getModelName())
                .modelType(model.getModelType())
                .modelVersion(model.getModelVersion())
                .algorithmName(model.getAlgorithmName())
                .trainingDataSize(model.getTrainingDataSize())
                .performance(performance)
                .modelStatus(model.getModelStatus())
                .deployedAt(model.getDeployedAt())
                .createTime(model.getCreateTime()) // 【修正】使用 getCreateTime()
                .build();
    }
}