package com.sf.zhimengjing.service.admin.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.model.dto.SystemLogDTO;
import com.sf.zhimengjing.common.model.vo.SystemLogVO;
import com.sf.zhimengjing.entity.admin.SystemLog;
import com.sf.zhimengjing.mapper.admin.SystemLogMapper;
import com.sf.zhimengjing.service.admin.SystemLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements SystemLogService {

    @Override
    public IPage<SystemLogVO> getLogList(SystemLogDTO dto) {
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<SystemLog>()
                .like(StringUtils.hasText(dto.getKeyword()), SystemLog::getOperation, dto.getKeyword())
                .or()
                .like(StringUtils.hasText(dto.getKeyword()), SystemLog::getModule, dto.getKeyword())
                .eq(StringUtils.hasText(dto.getLogLevel()), SystemLog::getLogLevel, dto.getLogLevel())
                .eq(StringUtils.hasText(dto.getModule()), SystemLog::getModule, dto.getModule())
                .eq(dto.getUserId() != null, SystemLog::getUserId, dto.getUserId())
                .ge(dto.getStartTime() != null, SystemLog::getCreateTime, dto.getStartTime())
                .le(dto.getEndTime() != null, SystemLog::getCreateTime, dto.getEndTime())
                .orderByDesc(SystemLog::getCreateTime);

        Page<SystemLog> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<SystemLog> entityPage = this.page(page, wrapper);

        return entityPage.convert(this::convertToVO);
    }

    @Override
    public Boolean recordLog(SystemLog log) {
        return this.save(log);
    }

    @Override
    public Map<String, Long> getLogStatistics(LocalDate startTime, LocalDate  endTime) {
        return baseMapper.getLogStatistics(startTime, endTime);
    }

    @Override
    public Boolean cleanExpiredLogs(Integer daysToKeep) {
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(daysToKeep);
        int deletedCount = baseMapper.cleanExpiredLogs(beforeTime);
        log.info("清理过期日志完成，删除记录数：{}", deletedCount);
        return deletedCount > 0;
    }

    @Override
    public void exportLogs(SystemLogDTO dto, HttpServletResponse response) {
        // 1. 根据查询条件，查询所有需要导出的数据（不分页）
        QueryWrapper<SystemLog> wrapper = buildQueryWrapper(dto);
        List<SystemLog> logList = this.list(wrapper);

        // 2. 将实体列表 List<SystemLog> 转换为视图列表 List<SystemLogVO>
        List<SystemLogVO> exportData = logList.stream().map(log -> {
            SystemLogVO vo = new SystemLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());

        // 3. 设置HTTP响应头，触发浏览器下载 Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = "system-logs-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("文件名编码失败", e);
        }

        // 4. 使用 EasyExcel 写数据到 Excel
        try {
            EasyExcel.write(response.getOutputStream(), SystemLogVO.class)
                    .sheet("系统日志")
                    .doWrite(exportData);
        } catch (IOException e) {
            log.error("导出系统日志到 Excel 时发生异常", e);
        }
    }


    private SystemLogVO convertToVO(SystemLog entity) {
        SystemLogVO vo = new SystemLogVO();
        vo.setId(entity.getId());
        vo.setLogLevel(entity.getLogLevel());
        vo.setModule(entity.getModule());
        vo.setOperation(entity.getOperation());
        vo.setRequestUrl(entity.getRequestUrl());
        vo.setRequestMethod(entity.getRequestMethod());
        vo.setUserId(entity.getUserId());
        vo.setUserIp(entity.getUserIp());
        vo.setExecutionTime(entity.getExecutionTime());
        vo.setErrorMessage(entity.getErrorMessage());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * 辅助方法：根据 SystemLogDTO 构建 QueryWrapper
     * (该方法现在是类的一部分，解决了 findLogsByDTO 不存在的问题)
     */
    private QueryWrapper<SystemLog> buildQueryWrapper(SystemLogDTO dto) {
        QueryWrapper<SystemLog> wrapper = new QueryWrapper<>();

        // 完全匹配 SystemLogDTO 和 SystemLog 的字段
        wrapper.eq(StringUtils.hasText(dto.getLogLevel()), "log_level", dto.getLogLevel());
        wrapper.like(StringUtils.hasText(dto.getModule()), "module", dto.getModule());
        wrapper.like(StringUtils.hasText(dto.getOperation()), "operation", dto.getOperation());
        wrapper.eq(dto.getUserId() != null, "user_id", dto.getUserId());
        wrapper.eq(StringUtils.hasText(dto.getUserIp()), "user_ip", dto.getUserIp());
        wrapper.between(dto.getStartTime() != null && dto.getEndTime() != null, "create_time", dto.getStartTime(), dto.getEndTime());

        // 关键词可以模糊搜索多个字段
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(qw -> qw.like("operation", dto.getKeyword())
                    .or().like("request_url", dto.getKeyword())
                    .or().like("error_message", dto.getKeyword()));
        }

        wrapper.orderByDesc("create_time"); // 按时间降序排序
        return wrapper;
    }

    /**
     * 辅助方法：处理CSV内容中的特殊字符，主要是双引号
     */
    private String escapeCsv(Object obj) {
        if (obj == null) {
            return "";
        }
        String text = obj.toString();
        // 如果内容包含双引号，需要替换为两个双引号
        if (text.contains("\"")) {
            text = text.replace("\"", "\"\"");
        }
        // 如果内容包含逗号、换行符等，需要用双引号括起来
        if (text.contains(",") || text.contains("\n") || text.contains("\r")) {
            return "\"" + text + "\"";
        }
        return text;
    }
}