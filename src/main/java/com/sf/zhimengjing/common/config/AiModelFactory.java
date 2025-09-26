package com.sf.zhimengjing.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.entity.admin.AIModel;
import com.sf.zhimengjing.service.admin.AIModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
@Configuration
@RequiredArgsConstructor
public class AiModelFactory {

    private final AIModelService aiModelService;
    private final StringEncryptor stringEncryptor;

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
            throw new GeneralBusinessException("未在数据库中找到模型配置：" + modelCode);
        }

        if (Boolean.FALSE.equals(model.getIsAvailable())) {
            log.warn("模型当前不可用：{}", modelCode);
            throw new GeneralBusinessException("模型当前不可用：" + modelCode);
        }

        // --- 新增校验 --- 确保apiKey不为空
        if (!StringUtils.hasText(model.getApiKey())) {
            log.error("模型 [{}] 的 API Key 未配置", modelCode);
            throw new GeneralBusinessException("模型API Key未配置：" + modelCode);
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
                throw new GeneralBusinessException("不支持的AI提供商：" + model.getProvider());
            }
        };
    }

    private ChatModel buildDeepSeekChatModel(AIModel model) {
        log.info("构建DeepSeek聊天模型，modelName={}", model.getModelName());
        String decryptedApiKey = stringEncryptor.decrypt(model.getApiKey());
        log.info("成功解密模型 [{}] 的 API Key", model.getModelCode());

        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .apiKey(Objects.requireNonNull(decryptedApiKey, "DeepSeek API Key 不能为空"))
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
        String decryptedApiKey = stringEncryptor.decrypt(model.getApiKey());
        log.info("API:{}",decryptedApiKey);
        log.info("成功解密模型 [{}] 的 API Key", model.getModelCode());
        ZhiPuAiApi zhipu = new ZhiPuAiApi(Objects.requireNonNull(decryptedApiKey, "ZhiPu API Key 不能为空"));



        ZhiPuAiChatOptions options = ZhiPuAiChatOptions.builder()
                .model(model.getModelName())
                .temperature(model.getTemperature().doubleValue())
                .maxTokens(model.getMaxTokens())
                .build();

        return new ZhiPuAiChatModel(zhipu, options);

    }

    private ChatModel buildAliBaiLianChatModel(AIModel model) {
        log.info("构建AliBaiLian聊天模型，modelName={}", model.getModelName());
        String decryptedApiKey = stringEncryptor.decrypt(model.getApiKey());
        log.info("成功解密模型 [{}] 的 API Key", model.getModelCode());
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(Objects.requireNonNull(decryptedApiKey, "AliBaiLian API Key 不能为空"))
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
        String decryptedApiKey = stringEncryptor.decrypt(model.getApiKey());
        log.info("成功解密模型 [{}] 的 API Key", model.getModelCode());
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(Objects.requireNonNull(decryptedApiKey, "Kimi API Key 不能为空"))
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

    /**
     * 清除所有缓存（包括ChatClient缓存）
     * 当模型配置发生变化时调用此方法
     */
    public void clearAllCaches() {
        log.info("清除所有相关缓存");
        clearCache(); // 清除ChatModel缓存

        // 通过ApplicationContext获取ChatClientFactory并清除其缓存
        try {
            // 注意：这里需要注入ApplicationContext或者通过其他方式获取ChatClientFactory
            log.info("建议手动调用ChatClientFactory.clearAllCache()来清除ChatClient缓存");
        } catch (Exception e) {
            log.warn("清除ChatClient缓存时发生异常", e);
        }
    }
}
