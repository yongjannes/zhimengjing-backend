package com.sf.zhimengjing.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
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
public class AdminLoginVO {
    @ApiModelProperty("JWT令牌")
    private String token;
}