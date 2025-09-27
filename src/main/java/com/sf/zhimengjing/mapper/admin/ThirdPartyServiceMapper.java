package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.admin.ThirdPartyService;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: ThirdPartyServiceMapper
 * @Author 殇枫
 * @Package com.sf.zhimengjing.mapper.admin
 * @description: 第三方服务配置数据访问层
 */
public interface ThirdPartyServiceMapper extends BaseMapper<ThirdPartyService> {

    /**
     * 根据服务类型获取激活的服务列表（按优先级排序）
     * @param serviceType 服务类型
     * @return 激活的服务列表
     */
    List<ThirdPartyService> getActiveServicesByType(@Param("serviceType") String serviceType);

    /**
     * 更新测试结果
     * @param id 服务ID
     * @param testResult 测试结果
     * @param testError 测试错误信息
     * @param testTime 测试时间
     * @return 更新记录数
     */
    int updateTestResult(@Param("id") Long id,
                         @Param("testResult") String testResult,
                         @Param("testError") String testError,
                         @Param("testTime") LocalDateTime testTime);
}
