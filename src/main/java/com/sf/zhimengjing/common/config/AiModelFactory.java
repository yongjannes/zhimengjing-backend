package com.sf.zhimengjing.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sf.zhimengjing.entity.admin.AIModel;
import com.sf.zhimengjing.service.admin.AIModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: AiModelFactory
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.config
 * @Description: AI模型工厂类，负责从数据库动态创建和管理不同的AI模型实例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiModelFactory {

    private final AIModelService aiModelService;
    private final Map<String, ChatModel> modelCache = new ConcurrentHashMap<>();

    /**
     * 根据模型编码获取聊天模型实例
     * 优先从缓存获取，如果不存在则动态构建
     */
    public ChatModel getChatModel(String modelCode) {
        log.info("获取AI模型实例，modelCode={}", modelCode);
        return modelCache.computeIfAbsent(modelCode, code -> {
            log.info("缓存未命中，构建模型：{}", code);
            return buildChatModel(code);
        });
    }

    /**
     * 根据模型编码构建聊天模型
     */
    private ChatModel buildChatModel(String modelCode) {
        // 1. 从数据库获取模型配置
        AIModel model = aiModelService.getOne(
                new LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, modelCode)
        );

        if (model == null) {
            log.error("未在数据库中找到模型配置：{}", modelCode);
            throw new IllegalArgumentException("未在数据库中找到模型配置：" + modelCode);
        }

        if (Boolean.FALSE.equals(model.getIsAvailable())) {
            log.warn("模型当前不可用：{}", modelCode);
            throw new IllegalStateException("模型当前不可用：" + modelCode);
        }

        log.info("正在构建AI模型，modelCode={}, provider={}", modelCode, model.getProvider());

        // 2. 根据提供商类型动态构建模型
        return switch (model.getProvider()) {
            case "DeepSeek" -> buildDeepSeekChatModel(model);
            case "ZhipuAI" -> buildZhipuChatModel(model);
            case "AliBaiLian" -> buildAliBaiLianChatModel(model);
            case "Kimi" -> buildKimiChatModel(model);
            default -> {
                log.error("不支持的AI提供商：{}", model.getProvider());
                throw new IllegalArgumentException("不支持的AI提供商：" + model.getProvider());
            }
        };
    }

    private ChatModel buildDeepSeekChatModel(AIModel model) {
        log.info("构建DeepSeek聊天模型，modelName={}", model.getModelName());
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .apiKey(Objects.requireNonNull(model.getApiKey(), "DeepSeek API Key 不能为空"))
                .build();

        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model(model.getModelName())
                .temperature(model.getTemperature().doubleValue())
                .maxTokens(model.getMaxTokens())
                .build();

        return DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(options)
                .build();
    }

    private ChatModel buildZhipuChatModel(AIModel model) {
        log.info("构建ZhipuAI聊天模型，modelName={}", model.getModelName());
        ZhiPuAiApi zhipu = new ZhiPuAiApi(Objects.requireNonNull(model.getApiKey(), "ZhiPu API Key 不能为空"));



        ZhiPuAiChatOptions options = ZhiPuAiChatOptions.builder()
                .model(model.getModelName())
                .temperature(model.getTemperature().doubleValue())
                .maxTokens(model.getMaxTokens())
                .build();

        return new ZhiPuAiChatModel(zhipu, options);

    }

    private ChatModel buildAliBaiLianChatModel(AIModel model) {
        log.info("构建AliBaiLian聊天模型，modelName={}", model.getModelName());
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(Objects.requireNonNull(model.getApiKey(), "AliBaiLian API Key 不能为空"))
                .baseUrl(Objects.requireNonNull(model.getApiEndpoint(), "AliBaiLian Base URL 不能为空"))
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model.getModelName())
                .temperature(model.getTemperature().doubleValue())
                .maxTokens(model.getMaxTokens())
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

    private ChatModel buildKimiChatModel(AIModel model) {
        log.info("构建Kimi聊天模型，modelName={}", model.getModelName());
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(Objects.requireNonNull(model.getApiKey(), "Kimi API Key 不能为空"))
                .baseUrl(Objects.requireNonNull(model.getApiEndpoint(), "Kimi Base URL 不能为空"))
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model.getModelName())
                .temperature(model.getTemperature().doubleValue())
                .maxTokens(model.getMaxTokens())
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

    public void clearCache() {
        log.info("清除所有模型缓存");
        modelCache.clear();
    }

    public void clearModelCache(String modelCode) {
        log.info("清除指定模型缓存，modelCode={}", modelCode);
        modelCache.remove(modelCode);
    }
}
