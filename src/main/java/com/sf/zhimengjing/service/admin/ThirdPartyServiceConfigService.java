package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.ThirdPartyServiceDTO;
import com.sf.zhimengjing.common.model.vo.ThirdPartyServiceVO;
import com.sf.zhimengjing.entity.admin.ThirdPartyService;

import java.util.List;

/**
 * @Title: ThirdPartyServiceConfigService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: 第三方服务配置服务接口
 */
public interface ThirdPartyServiceConfigService extends IService<ThirdPartyService> {

    /** 分页查询第三方服务列表 */
    IPage<ThirdPartyServiceVO> getServiceList(ThirdPartyServiceDTO dto);

    /** 创建第三方服务配置 */
    ThirdPartyServiceVO createServiceConfig(ThirdPartyServiceDTO dto);

    /** 更新第三方服务配置 */
    ThirdPartyServiceVO updateServiceConfig(Long id, ThirdPartyServiceDTO dto);

    /** 删除第三方服务配置 */
    Boolean deleteServiceConfig(Long id);

    /** 测试服务连接 */
    Boolean testServiceConnection(Long id);

    /** 获取活跃的服务配置 */
    List<ThirdPartyServiceVO> getActiveServices(String serviceType);

    /** 发送邮件 */
    Boolean sendEmail(String to, String subject, String content);

    /** 发送短信 */
    Boolean sendSms(String phone, String content);
}