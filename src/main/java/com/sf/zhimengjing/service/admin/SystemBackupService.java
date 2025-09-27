package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.SystemBackupDTO;
import com.sf.zhimengjing.common.model.vo.SystemBackupVO;
import com.sf.zhimengjing.entity.admin.SystemBackup;

import java.util.Map;

/**
 * @Title: SystemBackupService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: 系统备份服务接口
 */
public interface SystemBackupService extends IService<SystemBackup> {

    /** 分页查询备份列表 */
    IPage<SystemBackupVO> getBackupList(SystemBackupDTO dto);

    /** 创建系统备份 */
    SystemBackupVO createBackup(SystemBackupDTO dto, Long userId);

    /** 删除备份 */
    Boolean deleteBackup(Long id);

    /** 下载备份文件 */
    String getDownloadUrl(Long id);

    /** 恢复系统备份 */
    Boolean restoreBackup(Long id, Long userId);

    /** 测试备份完整性 */
    Boolean testBackupIntegrity(Long id);

    /** 获取备份统计信息 */
    Map<String, Long> getBackupStatistics();
}