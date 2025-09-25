package com.sf.zhimengjing.service.admin;

import reactor.core.publisher.Flux;

/**
 * @Title: DreamAnalysisService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: 梦境解析服务接口，提供AI梦境解析功能
 */
public interface DreamAnalysisService {

    /**
     * 执行梦境解析
     *
     * @param modelCode    模型编码
     * @param dreamContent 梦境内容
     * @return 解析后的字符串结果
     */
    String analyzeDream(String modelCode, String dreamContent);

    /**
     * 流式梦境解析
     *
     * @param modelCode    模型编码
     * @param dreamContent 梦境内容
     * @return 包含解析结果的响应流
     */
    Flux<String> analyzeDreamStream(String modelCode, String dreamContent);
}