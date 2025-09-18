package com.sf.zhimengjing.service;

import com.sf.zhimengjing.common.model.vo.CaptchaVO;

/**
 * @Title: CaptchaService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service
 * @description: 验证码服务
 */
public interface CaptchaService {
    /**
     *  生成图形验证码
     * @param captchaId 验证码id
     * @return 验证码视图对象
     */
    CaptchaVO getCaptcha(String captchaId);
}
