package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * @Title: AIApiLog
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: AI调用日志实体类，用于记录AI API的调用详情
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_api_logs")
public class AIApiLog extends BaseEntity {

    /** 请求ID（唯一标识） */
    private String requestId;

    /** 模型编码 */
    private String modelCode;

    /** 用户ID */
    private Long userId;

    /** API端点 */
    private String apiEndpoint;

    /** 提示令牌数 */
    private Integer promptTokens;

    /** 完成令牌数 */
    private Integer completionTokens;

    /** 总令牌数 */
    private Integer totalTokens;

    /** 调用成本 */
    private BigDecimal cost;

    /** 响应时间（毫秒） */
    private Integer responseTime;

    /** 状态（如：SUCCESS-成功，FAILED-失败，TIMEOUT-超时） */
    private String status;

    /** 错误信息 */
    private String errorMsg;

    /** 请求内容 */
    private String requestContent;

    /** 响应内容 */
    private String responseContent;
}