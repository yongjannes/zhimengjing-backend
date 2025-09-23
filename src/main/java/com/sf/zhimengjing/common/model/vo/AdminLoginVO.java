package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @Title: AdminLoginVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 管理员登录返回视图对象（VO）
 */
@Data
@Builder
@Schema(description = "管理员登录VO")
public class AdminLoginVO {
    @Schema(description = "JWT令牌")
    private String token;
}