package com.sf.zhimengjing.common.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Title: CaptchaVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 验证码返回视图对象
 */
@Data
@Builder
public class CaptchaVO {
    //验证码id
    private  String captchaId;
    //验证码图片base64编码
    private  String captchaImage;
}
