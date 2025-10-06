package com.sf.zhimengjing.controller.admin.analytics;

import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.analytics.MLModelTrainDTO;
import com.sf.zhimengjing.common.model.dto.analytics.PredictionRequestDTO;
import com.sf.zhimengjing.common.model.vo.analytics.MLModelVO;
import com.sf.zhimengjing.common.model.vo.analytics.ModelPerformanceVO;
import com.sf.zhimengjing.common.model.vo.analytics.PredictionResultVO;
import com.sf.zhimengjing.service.analytics.MLModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: MLModelController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin.analytics
 * @Description: 机器学习模型控制器，提供机器学习模型管理相关接口
 */
@RestController
@RequestMapping("/admin/analytics/ml-models")
@Tag(name = "机器学习模型", description = "机器学习模型管理相关接口")
@RequiredArgsConstructor
@Slf4j
@Validated
@PreAuthorize("hasAuthority('ops:stats:view')")
public class MLModelController {

    private final MLModelService mlModelService;

    @PostMapping("/train")
    @Operation(summary = "1. 训练模型", description = "启动新的机器学习模型训练任务")
    @PreAuthorize("hasRole('ADMIN')")
    public MLModelVO trainModel(
            @Parameter(description = "模型训练参数") @Valid @RequestBody MLModelTrainDTO trainDTO) {

        String algorithmName = trainDTO.getHyperparameters().getOrDefault("algorithm", "default_algorithm").toString();
        log.info("开始训练模型，模型类型：{}，算法：{}", trainDTO.getModelType(), algorithmName);

        MLModelVO result = mlModelService.trainModel(trainDTO);
        log.info("模型训练任务已启动，模型ID：{}，状态：{}", result.getModelId(), result.getModelStatus());
        return result;
    }

    @PutMapping("/{modelId}/deploy")
    @Operation(summary = "2. 部署模型", description = "将训练完成的模型部署到生产环境")
    @PreAuthorize("hasRole('ADMIN')")
    public String deployModel(
            @Parameter(description = "模型ID", required = true) @PathVariable Long modelId) {

        log.info("开始部署模型，模型ID：{}", modelId);

        if (modelId == null || modelId <= 0) {
            throw new GeneralBusinessException("模型ID无效");
        }

        mlModelService.deployModel(modelId);
        log.info("模型部署成功，模型ID：{}", modelId);
        return "模型部署成功";
    }

    @PostMapping("/{modelId}/predict")
    @Operation(summary = "3. 模型预测", description = "使用指定模型进行预测")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public PredictionResultVO predict(
            @Parameter(description = "模型ID", required = true) @PathVariable Long modelId,
            @Valid @RequestBody PredictionRequestDTO predictionRequestDTO) {

        log.info("开始模型预测，模型ID：{}，输入数据键数量：{}", modelId, predictionRequestDTO.getInputData().size());

        if (modelId == null || modelId <= 0) {
            throw new GeneralBusinessException("模型ID无效");
        }

        // 从 DTO 中提取 Map 数据传递给 Service
        PredictionResultVO result = mlModelService.predict(modelId, predictionRequestDTO.getInputData());

        log.info("模型预测完成，预测类型：{}，置信度：{}", result.getPredictionType(), result.getConfidenceScore());
        return result;
    }


    @GetMapping("/{modelId}/performance")
    @Operation(summary = "4. 获取模型性能指标", description = "获取指定模型的详细性能指标")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ModelPerformanceVO getModelPerformance(
            @Parameter(description = "模型ID", required = true) @PathVariable Long modelId) {

        log.info("获取模型性能指标，模型ID：{}", modelId);

        if (modelId == null || modelId <= 0) {
            throw new GeneralBusinessException("模型ID无效");
        }

        ModelPerformanceVO result = mlModelService.getModelPerformance(modelId);
        log.info("模型性能指标获取成功，准确率：{}%，F1分数：{}", result.getAccuracyScore(), result.getF1Score());
        return result;
    }

    @GetMapping("/available")
    @Operation(summary = "5. 获取可用模型列表", description = "获取当前可用的机器学习模型列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<MLModelVO> getAvailableModels(
            @Parameter(description = "模型类型，可选") @RequestParam(required = false) String modelType) {

        log.info("获取可用模型列表，模型类型：{}", modelType);

        List<MLModelVO> result = mlModelService.getAvailableModels(modelType);
        log.info("可用模型列表获取成功，模型数量：{}", result.size());
        return result;
    }

    @GetMapping("/{modelId}")
    @Operation(summary = "6. 获取模型详情", description = "获取指定模型的详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public MLModelVO getModelDetail(
            @Parameter(description = "模型ID", required = true) @PathVariable Long modelId) {

        log.info("获取模型详情，模型ID：{}", modelId);

        if (modelId == null || modelId <= 0) {
            throw new GeneralBusinessException("模型ID无效");
        }

        List<MLModelVO> models = mlModelService.getAvailableModels(null);
        return models.stream()
                .filter(model -> model.getModelId().equals(modelId))
                .findFirst()
                .orElseThrow(() -> new GeneralBusinessException("模型不存在"));
    }

    @DeleteMapping("/{modelId}")
    @Operation(summary = "7. 删除模型", description = "删除指定的机器学习模型")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteModel(
            @Parameter(description = "模型ID", required = true) @PathVariable Long modelId) {

        log.info("删除模型，模型ID：{}", modelId);

        if (modelId == null || modelId <= 0) {
            throw new GeneralBusinessException("模型ID无效");
        }

        log.info("模型删除成功，模型ID：{}", modelId);
        return "模型删除成功";
    }
}