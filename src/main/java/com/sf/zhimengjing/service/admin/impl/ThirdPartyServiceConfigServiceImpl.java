package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.ThirdPartyServiceDTO;
import com.sf.zhimengjing.common.model.vo.ThirdPartyServiceVO;
import com.sf.zhimengjing.entity.admin.ThirdPartyService;
import com.sf.zhimengjing.mapper.admin.ThirdPartyServiceMapper;
import com.sf.zhimengjing.service.admin.ThirdPartyServiceConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Title: ThirdPartyServiceConfigServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.admin.impl
 * @description: 第三方服务配置管理服务实现类
 * 负责第三方服务配置的增删改查、连接测试以及发送通知功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdPartyServiceConfigServiceImpl extends ServiceImpl<ThirdPartyServiceMapper, ThirdPartyService> implements ThirdPartyServiceConfigService {

    /** JSON 序列化/反序列化工具 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取第三方服务配置列表（分页）
     * @param dto 查询条件 DTO
     * @return 分页后的第三方服务 VO 列表
     */
    @Override
    public IPage<ThirdPartyServiceVO> getServiceList(ThirdPartyServiceDTO dto) {
        LambdaQueryWrapper<ThirdPartyService> wrapper = new LambdaQueryWrapper<ThirdPartyService>()
                .like(StringUtils.hasText(dto.getKeyword()), ThirdPartyService::getServiceName, dto.getKeyword())
                .eq(StringUtils.hasText(dto.getServiceType()), ThirdPartyService::getServiceType, dto.getServiceType())
                .eq(dto.getIsActive() != null, ThirdPartyService::getIsActive, dto.getIsActive())
                .orderByAsc(ThirdPartyService::getPriority);

        Page<ThirdPartyService> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<ThirdPartyService> entityPage = this.page(page, wrapper);

        return entityPage.convert(this::convertToVO);
    }

    /**
     * 创建新的第三方服务配置
     * @param dto 服务配置 DTO
     * @return 创建后的服务 VO
     */
    @Override
    @Transactional
    public ThirdPartyServiceVO createServiceConfig(ThirdPartyServiceDTO dto) {
        ThirdPartyService service = new ThirdPartyService();
        service.setServiceName(dto.getServiceName());
        service.setServiceType(dto.getServiceType());
        service.setConfigData(convertMapToJson(dto.getConfigData()));
        service.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : false);
        service.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
        service.setDescription(dto.getDescription());

        boolean saved = this.save(service);
        if (!saved) {
            throw new GeneralBusinessException("创建服务配置失败");
        }
        return convertToVO(service);
    }

    /**
     * 更新已有的第三方服务配置
     * @param id 服务配置 ID
     * @param dto 服务配置 DTO
     * @return 更新后的服务 VO
     */
    @Override
    @Transactional
    public ThirdPartyServiceVO updateServiceConfig(Long id, ThirdPartyServiceDTO dto) {
        ThirdPartyService service = this.getById(id);
        if (service == null) {
            throw new GeneralBusinessException("服务配置不存在");
        }

        service.setServiceName(dto.getServiceName());
        service.setServiceType(dto.getServiceType());
        service.setConfigData(convertMapToJson(dto.getConfigData()));
        service.setIsActive(dto.getIsActive());
        service.setPriority(dto.getPriority());
        service.setDescription(dto.getDescription());

        boolean updated = this.updateById(service);
        if (!updated) {
            throw new GeneralBusinessException("更新服务配置失败");
        }
        return convertToVO(service);
    }

    /**
     * 删除指定的第三方服务配置
     * @param id 服务配置 ID
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteServiceConfig(Long id) {
        if (this.getById(id) == null) {
            throw new GeneralBusinessException("服务配置不存在");
        }
        return this.removeById(id);
    }

    /**
     * 测试第三方服务连接，并更新测试结果
     * @param id 服务配置 ID
     * @return 测试是否成功
     */
    @Override
    @Transactional
    public Boolean testServiceConnection(Long id) {
        ThirdPartyService service = this.getById(id);
        if (service == null) {
            throw new GeneralBusinessException("服务配置不存在");
        }

        service.setLastTestTime(LocalDateTime.now());
        try {
            // 模拟连接测试，可以根据 service.getServiceType() 调用不同的测试逻辑
            log.info("开始测试服务: {} ({})", service.getServiceName(), service.getServiceType());
            Thread.sleep(1000); // 模拟网络延迟

            // 模拟随机成功或失败
            if (Math.random() > 0.3) { // 70% 成功率
                service.setTestResult("SUCCESS");
                service.setTestError(null);
                this.updateById(service);
                log.info("服务 {} 连接测试成功", service.getServiceName());
                return true;
            } else {
                throw new IOException("模拟连接超时或认证失败");
            }
        } catch (Exception e) {
            service.setTestResult("FAILED");
            service.setTestError(e.getMessage());
            this.updateById(service);
            log.error("服务 {} 连接测试失败: {}", service.getServiceName(), e.getMessage());
            return false;
        }
    }


    /**
     * 获取指定类型的启用服务列表
     * @param serviceType 服务类型，可为空
     * @return 启用服务列表
     */
    @Override
    public List<ThirdPartyServiceVO> getActiveServices(String serviceType) {
        LambdaQueryWrapper<ThirdPartyService> wrapper = new LambdaQueryWrapper<ThirdPartyService>()
                .eq(ThirdPartyService::getIsActive, true)
                .eq(StringUtils.hasText(serviceType), ThirdPartyService::getServiceType, serviceType)
                .orderByAsc(ThirdPartyService::getPriority);
        List<ThirdPartyService> services = this.list(wrapper);

        return services.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 发送邮件
     * @param to 收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    @Override
    public Boolean sendEmail(String to, String subject, String content) {
        List<ThirdPartyService> emailServices = this.list(
                new LambdaQueryWrapper<ThirdPartyService>()
                        .eq(ThirdPartyService::getServiceType, "EMAIL")
                        .eq(ThirdPartyService::getIsActive, true)
                        .orderByAsc(ThirdPartyService::getPriority)
        );

        if (emailServices.isEmpty()) {
            throw new GeneralBusinessException("没有可用的邮件服务配置");
        }

        // 尝试使用配置的服务发送邮件，直到成功或全部失败
        for (ThirdPartyService service : emailServices) {
            try {
                // 模拟邮件发送
                log.info("尝试使用服务 {} 发送邮件到 {}", service.getServiceName(), to);
                // 这里可以集成真实的邮件发送SDK
                if (Math.random() > 0.2) { // 80% 成功率
                    log.info("邮件发送成功: {} -> {}", subject, to);
                    return true;
                }
                throw new RuntimeException("模拟邮件服务器错误");
            } catch (Exception e) {
                log.error("使用服务 {} 发送邮件失败: {}", service.getServiceName(), e.getMessage());
            }
        }
        return false;
    }


    /**
     * 发送短信
     * @param phone 手机号
     * @param content 短信内容
     * @return 是否发送成功
     */
    @Override
    public Boolean sendSms(String phone, String content) {
        List<ThirdPartyService> smsServices = this.list(
                new LambdaQueryWrapper<ThirdPartyService>()
                        .eq(ThirdPartyService::getServiceType, "SMS")
                        .eq(ThirdPartyService::getIsActive, true)
                        .orderByAsc(ThirdPartyService::getPriority)
        );

        if (smsServices.isEmpty()) {
            throw new GeneralBusinessException("没有可用的短信服务配置");
        }

        for (ThirdPartyService service : smsServices) {
            try {
                // 模拟短信发送
                log.info("尝试使用服务 {} 发送短信到 {}", service.getServiceName(), phone);
                // 这里可以集成真实的短信发送SDK
                if (Math.random() > 0.2) { // 80% 成功率
                    log.info("短信发送成功: {} -> {}", content, phone);
                    return true;
                }
                throw new RuntimeException("模拟短信网关错误");
            } catch (Exception e) {
                log.error("使用服务 {} 发送短信失败: {}", service.getServiceName(), e.getMessage());
            }
        }

        return false;
    }


    /**
     * 将 Map 转换为 JSON 字符串
     */
    private String convertMapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map != null ? map : new HashMap<>());
        } catch (Exception e) {
            log.error("转换Map为JSON失败", e);
            throw new GeneralBusinessException("配置数据格式错误");
        }
    }

    /**
     * 将 JSON 字符串转换为 Map
     */
    private Map<String, Object> convertJsonToMap(String json) {
        try {
            if (!StringUtils.hasText(json)) {
                return new HashMap<>();
            }
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.error("转换JSON为Map失败", e);
            return new HashMap<>(); // 返回空Map，避免空指针
        }
    }


    /**
     * 将 ThirdPartyService 实体转换为 VO
     */
    private ThirdPartyServiceVO convertToVO(ThirdPartyService entity) {
        ThirdPartyServiceVO vo = new ThirdPartyServiceVO();
        vo.setId(entity.getId());
        vo.setServiceName(entity.getServiceName());
        vo.setServiceType(entity.getServiceType());
        vo.setConfigData(convertJsonToMap(entity.getConfigData()));
        vo.setIsActive(entity.getIsActive());
        vo.setPriority(entity.getPriority());
        vo.setDescription(entity.getDescription());
        vo.setLastTestTime(entity.getLastTestTime());
        vo.setTestResult(entity.getTestResult());
        vo.setTestError(entity.getTestError());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}