package com.sf.zhimengjing.common.model.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Title: UserLoginVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 用户登录返回信息视图对象
 */
@Data
@Builder
public class UserLoginVO implements Serializable {
    private String token;//令牌
    private String userName;//用户名
    private String avatar;//头像
}
