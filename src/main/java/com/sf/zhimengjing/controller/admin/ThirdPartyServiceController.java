package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.ThirdPartyServiceDTO;
import com.sf.zhimengjing.common.model.vo.ThirdPartyServiceVO;
import com.sf.zhimengjing.service.admin.ThirdPartyServiceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @Title: ThirdPartyServiceController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 第三方服务配置控制器
 */
@RestController
@RequestMapping("/admin/system/third-party")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('system:sys:config')")
@Tag(name = "第三方服务配置", description = "第三方服务配置相关操作接口")
public class ThirdPartyServiceController {

    private final ThirdPartyServiceConfigService thirdPartyServiceConfigService;

    /**
     * 1. 获取第三方服务列表（分页）
     */
    @GetMapping("/list")
    @Operation(summary = "1. 获取第三方服务列表")
    @PreAuthorize("hasRole('ADMIN')")
    public IPage<ThirdPartyServiceVO> getServiceList(@Valid ThirdPartyServiceDTO dto) {
        return thirdPartyServiceConfigService.getServiceList(dto);
    }

    /**
     * 2. 创建第三方服务配置
     */
    @PostMapping
    @Operation(summary = "2. 创建第三方服务配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "第三方服务", operation = "创建配置")
    public ThirdPartyServiceVO createServiceConfig(@Valid @RequestBody ThirdPartyServiceDTO dto) {
        return thirdPartyServiceConfigService.createServiceConfig(dto);
    }

    /**
     * 3. 更新第三方服务配置
     */
    @PutMapping("/{id}")
    @Operation(summary = "3. 更新第三方服务配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "第三方服务", operation = "更新配置")
    public ThirdPartyServiceVO updateServiceConfig(
            @Parameter(description = "服务ID") @PathVariable Long id,
            @Valid @RequestBody ThirdPartyServiceDTO dto) {
        return thirdPartyServiceConfigService.updateServiceConfig(id, dto);
    }

    /**
     * 4. 删除第三方服务配置
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "4. 删除第三方服务配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "第三方服务", operation = "删除配置")
    public Boolean deleteServiceConfig(
            @Parameter(description = "服务ID") @PathVariable Long id) {
        return thirdPartyServiceConfigService.deleteServiceConfig(id);
    }

    /**
     * 5. 测试服务连接
     */
    @PostMapping("/{id}/test")
    @Operation(summary = "5. 测试服务连接")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "第三方服务", operation = "测试连接")
    public Boolean testServiceConnection(
            @Parameter(description = "服务ID") @PathVariable Long id) {
        return thirdPartyServiceConfigService.testServiceConnection(id);
    }

    /**
     * 6. 获取活跃服务配置
     */
    @GetMapping("/active")
    @Operation(summary = "6. 获取活跃服务配置")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ThirdPartyServiceVO> getActiveServices(
            @Parameter(description = "服务类型") @RequestParam(required = false) String serviceType) {
        return thirdPartyServiceConfigService.getActiveServices(serviceType);
    }

    /**
     * 7. 发送测试邮件
     */
    @PostMapping("/email/send")
    @Operation(summary = "7. 发送测试邮件")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "第三方服务", operation = "发送测试邮件")
    public Boolean sendTestEmail(
            @Parameter(description = "收件人邮箱") @RequestParam String to,
            @Parameter(description = "邮件主题") @RequestParam String subject,
            @Parameter(description = "邮件内容") @RequestParam String content) {
        return thirdPartyServiceConfigService.sendEmail(to, subject, content);
    }

    /**
     * 8. 发送测试短信
     */
    @PostMapping("/sms/send")
    @Operation(summary = "8. 发送测试短信")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "第三方服务", operation = "发送测试短信")
    public Boolean sendTestSms(
            @Parameter(description = "手机号码") @RequestParam String phone,
            @Parameter(description = "短信内容") @RequestParam String content) {
        return thirdPartyServiceConfigService.sendSms(phone, content);
    }
}