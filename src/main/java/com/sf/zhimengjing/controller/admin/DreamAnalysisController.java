package com.sf.zhimengjing.controller.admin;

import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.service.admin.DreamAnalysisService; // 确认注入的是接口
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @Title: DreamAnalysisController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller
 * @Description: 梦境解析控制器，提供AI梦境解析接口
 */
@RestController
@RequestMapping("/api/dream-analysis")
@RequiredArgsConstructor
@Tag(name = "梦境解析接口")
public class DreamAnalysisController {

    private final DreamAnalysisService dreamAnalysisService; // 注入接口

    @PostMapping("/analyze")
    @Operation(summary = "1. 梦境解析", description = "使用AI对梦境进行解析")
    public Result<String> analyzeDream(
            @Parameter(description = "模型编码") @RequestParam(required = false) String modelCode,
            @Parameter(description = "梦境内容") @RequestParam  String dreamContent) {

        return Result.success(dreamAnalysisService.analyzeDream(modelCode, dreamContent));
    }

    @PostMapping(value = "/analyze/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "2. 流式梦境解析", description = "使用AI对梦境进行流式解析")
    public Flux<String> analyzeDreamStream(
            @Parameter(description = "模型编码") @RequestParam String modelCode,
            @Parameter(description = "梦境内容") @RequestParam String dreamContent) {

        return dreamAnalysisService.analyzeDreamStream(modelCode, dreamContent);
    }
}