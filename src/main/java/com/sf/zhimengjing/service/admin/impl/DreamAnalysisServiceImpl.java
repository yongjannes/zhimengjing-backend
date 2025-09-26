package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sf.zhimengjing.common.config.AiModelFactory;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.ai.AIDreamConfigDTO;
import com.sf.zhimengjing.common.util.SecurityUtils;
import com.sf.zhimengjing.entity.admin.AIApiLog;
import com.sf.zhimengjing.entity.admin.AIModel;
import com.sf.zhimengjing.mapper.admin.AIApiLogMapper;
import com.sf.zhimengjing.service.admin.AIDreamConfigService;
import com.sf.zhimengjing.service.admin.AIModelService;
import com.sf.zhimengjing.service.admin.DreamAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Title: DreamAnalysisServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: 梦境解析服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DreamAnalysisServiceImpl implements DreamAnalysisService {

    private final AiModelFactory aiModelFactory;
    private final AIDreamConfigService dreamConfigService;
    private final AIApiLogMapper aiApiLogMapper;
    private final AIModelService aiModelService;

    private static final BigDecimal CHAR_TO_TOKEN_RATIO = new BigDecimal("1.8");

    @Override
    public String analyzeDream(String modelCode, String dreamContent) {
        if (!StringUtils.hasText(modelCode)) {
            modelCode = getDefaultModelCode();
        }
        log.info("=== 开始解析梦境 ===");
        log.info("请求参数 - modelCode: {}, dreamContent: {}", modelCode, dreamContent);

        AIApiLog logEntity = new AIApiLog();
        logEntity.setRequestId(UUID.randomUUID().toString());
        logEntity.setModelCode(modelCode);
        logEntity.setUserId(SecurityUtils.getUserId());
        logEntity.setRequestContent(dreamContent);
        logEntity.setCreateTime(LocalDateTime.now());

        long startTime = System.currentTimeMillis();

        try {
            // 获取模型和配置
            ChatModel chatModel = aiModelFactory.getChatModel(modelCode);
            log.debug("获取到 ChatModel: {}", chatModel);

            AIDreamConfigDTO config = dreamConfigService.getDreamConfigByModel(modelCode);
            log.debug("获取到模型配置: {}", config);
            logEntity.setApiEndpoint("DREAM_ANALYSIS");

            // 构建聊天客户端
            ChatClient chatClient = buildChatClient(chatModel, config);
            log.debug("构建 ChatClient 成功");

            // 执行解析
//            String result = chatClient.prompt()
//                    .user(dreamContent)
//                    .call()
//                    .content();

            ChatResponse response = chatClient.prompt()
                    .user(dreamContent)
                    .call()
                    .chatResponse();

            String result = response.getResult().getOutput().getText();
            log.info("梦境解析成功，结果: {}", result);
            logEntity.setResponseContent(result);

            Usage usage = response.getMetadata().getUsage();
            if (usage != null) {
                logEntity.setPromptTokens(usage.getPromptTokens().intValue());
                logEntity.setCompletionTokens(usage.getCompletionTokens().intValue());
                logEntity.setTotalTokens(usage.getTotalTokens().intValue());
                Integer totalTokens = usage.getTotalTokens().intValue();
                logEntity.setTotalTokens(totalTokens);
                AIModel model = aiModelService.getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, modelCode));
                if (model != null && model.getCostPer1kTokens() != null) {
                    BigDecimal cost = model.getCostPer1kTokens()
                            .multiply(new BigDecimal(totalTokens))
                            .divide(new BigDecimal(1000), 6, RoundingMode.HALF_UP);
                    logEntity.setCost(cost);
                }
            }
            logEntity.setStatus("SUCCESS");
            return result;

        } catch (Exception e) {
            log.error("梦境解析失败 - modelCode: {}, dreamContent: {}", modelCode, dreamContent, e);
            logEntity.setStatus("FAILED");
            String errorMessage = "AI服务调用失败：" + e.getMessage();
            logEntity.setErrorMsg(e.getMessage());
            throw new GeneralBusinessException(errorMessage);
        } finally {
            logEntity.setResponseTime((int) (System.currentTimeMillis() - startTime));
            aiApiLogMapper.insert(logEntity);
            log.info("=== 解析梦境结束 ===");
        }
    }

    @Override
    public Flux<String> analyzeDreamStream(String modelCode, String dreamContent) {
        String finalModelCode;
        if (!StringUtils.hasText(modelCode)) {
            finalModelCode  = getDefaultModelCode();
        }else {
            finalModelCode = modelCode;
        }
        log.info("=== 开始流式解析梦境 ===");
        log.info("请求参数 - modelCode: {}, dreamContent: {}", modelCode, dreamContent);

        AIApiLog logEntity = new AIApiLog();
        logEntity.setRequestId(UUID.randomUUID().toString());
        logEntity.setModelCode(modelCode);
        logEntity.setUserId(SecurityUtils.getUserId());
        logEntity.setRequestContent(dreamContent);
        logEntity.setCreateTime(LocalDateTime.now());
        long startTime = System.currentTimeMillis();
        StringBuilder responseContent = new StringBuilder();

        try {
            ChatModel chatModel = aiModelFactory.getChatModel(modelCode);
            AIDreamConfigDTO config = dreamConfigService.getDreamConfigByModel(modelCode);
            logEntity.setApiEndpoint("DREAM_ANALYSIS_STREAM");
            ChatClient chatClient = buildChatClient(chatModel, config);

            return chatClient.prompt()
                    .user(dreamContent)
                    .stream()
                    .content() // 我们仍然从content()开始，因为它最简单
                    .doOnNext(responseContent::append) // 拼接所有返回的文本
                    .doOnError(err -> {
                        log.error("流式解析出现异常", err);
                        logEntity.setStatus("FAILED");
                        logEntity.setErrorMsg(err.getMessage());
                        logEntity.setResponseContent(responseContent.toString());
                        logEntity.setResponseTime((int) (System.currentTimeMillis() - startTime));
                        aiApiLogMapper.insert(logEntity);
                    })
                    .doOnComplete(() -> {
                        log.info("流式解析完成");
                        logEntity.setStatus("SUCCESS");
                        logEntity.setResponseContent(responseContent.toString());
                        logEntity.setResponseTime((int) (System.currentTimeMillis() - startTime));

                        // --- 关键修改：在这里估算Token和成本 ---
                        try {
                            // 估算输入Token
                            int promptTokens = new BigDecimal(dreamContent.length()).multiply(CHAR_TO_TOKEN_RATIO).intValue();
                            logEntity.setPromptTokens(promptTokens);

                            // 估算输出Token
                            int completionTokens = new BigDecimal(responseContent.length()).multiply(CHAR_TO_TOKEN_RATIO).intValue();
                            logEntity.setCompletionTokens(completionTokens);

                            // 计算总Token
                            int totalTokens = promptTokens + completionTokens;
                            logEntity.setTotalTokens(totalTokens);

                            // 估算成本
                            AIModel model = aiModelService.getOne(new LambdaQueryWrapper<AIModel>().eq(AIModel::getModelCode, finalModelCode));
                            if (model != null && model.getCostPer1kTokens() != null) {
                                BigDecimal cost = model.getCostPer1kTokens()
                                        .multiply(new BigDecimal(totalTokens))
                                        .divide(new BigDecimal(1000), 6, RoundingMode.HALF_UP);
                                logEntity.setCost(cost);
                                log.info("流式请求日志估算完成: totalTokens={}, cost={}", totalTokens, cost);
                            }
                        } catch (Exception e) {
                            log.error("流式请求日志估算Token和成本时发生异常", e);
                        }

                        aiApiLogMapper.insert(logEntity);
                    });

        } catch (Exception e) {
            log.error("流式梦境解析失败 - modelCode: {}, dreamContent: {}", modelCode, dreamContent, e);
            logEntity.setStatus("FAILED");
            logEntity.setErrorMsg(e.getMessage());
            logEntity.setResponseTime((int) (System.currentTimeMillis() - startTime));
            aiApiLogMapper.insert(logEntity);
            throw e;
        }
    }

    /**
     * 构建聊天客户端
     */
    private ChatClient buildChatClient(ChatModel chatModel, AIDreamConfigDTO config) {
        log.debug("构建 ChatClient，使用自定义系统提示: {}", config.getCustomPrompt());
        try {
            ChatClient client = ChatClient.builder(chatModel)
                    .defaultSystem(config.getCustomPrompt())
                    .defaultAdvisors(new SimpleLoggerAdvisor())
                    .build();
            log.debug("ChatClient 构建完成");
            return client;
        } catch (Exception e) {
            log.error("构建 ChatClient 失败", e);
            throw e;
        }
    }

    /**
     * 获取默认模型的编码
     */
    private String getDefaultModelCode() {
        AIModel defaultModel = aiModelService.getOne(new LambdaQueryWrapper<AIModel>().eq(AIModel::getIsDefault, true));
        if (defaultModel == null) {
            log.error("系统中没有配置默认的AI模型！");
            throw new GeneralBusinessException("系统中没有配置默认的AI模型");
        }
        log.info("前端未指定模型，自动使用默认模型: {}", defaultModel.getModelCode());
        return defaultModel.getModelCode();
    }
}
