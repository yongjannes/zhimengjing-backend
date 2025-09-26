package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.ai.AIDreamConfigDTO;
import com.sf.zhimengjing.entity.admin.AIDreamConfig;

import java.util.List;

/**
 * @Title: AIDreamConfigService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: AI梦境解析配置服务接口，提供梦境解析配置相关业务操作
 */
public interface AIDreamConfigService extends IService<AIDreamConfig> {

    /** 获取所有梦境解析配置 */
    List<AIDreamConfigDTO> getAllDreamConfigs();

    /** 根据模型编码获取梦境解析配置 */
    AIDreamConfigDTO getDreamConfigByModel(String modelCode);

    /** 保存梦境解析配置 */
    boolean saveDreamConfig(AIDreamConfigDTO.DreamConfigRequestDTO requestDTO, Long operatorId);

    /** 删除梦境解析配置 */
    boolean deleteDreamConfig(String modelCode, Long operatorId);

    /** 切换配置状态 */
    boolean toggleConfigStatus(String modelCode, Boolean isActive, Long operatorId);

    /** 测试梦境解析 */
    String testDreamAnalysis(String modelCode, String dreamContent);

    /** 获取默认配置 */
    AIDreamConfigDTO getDefaultConfig();

}