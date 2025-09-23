package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: VipLevelDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: VIP等级相关的数据传输对象集合
 */
@Data
@Schema(description="VIP等级信息DTO")
public class VipLevelDTO implements Serializable {

    @Schema(description = "等级ID（主键ID）")
    private Long id;

    @Schema(description = "等级编码")
    private String levelCode;

    @Schema(description = "等级名称")
    private String levelName;

    @Schema(description = "等级排序")
    private Integer levelOrder;

    @Schema(description = "月费价格")
    private BigDecimal monthlyPrice;

    @Schema(description = "季费价格")
    private BigDecimal quarterlyPrice;

    @Schema(description = "年费价格")
    private BigDecimal yearlyPrice;

    @Schema(description = "折扣率")
    private BigDecimal discountRate;

    @Schema(description = "每日解析次数限制")
    private Integer dailyAnalysisLimit;

    @Schema(description = "存储空间(MB)")
    private Integer storageSpaceMb;

    @Schema(description = "可访问的AI模型列表")
    private List<String> aiModelAccess;

    @Schema(description = "优先客服支持")
    private Boolean prioritySupport;

    @Schema(description = "无广告体验")
    private Boolean adFree;

    @Schema(description = "高级功能列表")
    private List<String> advancedFeatures;

    @Schema(description = "等级描述")
    private String description;

    @Schema(description = "是否启用")
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 等级创建请求 DTO —— 接收前端提交的等级创建数据
     */
    @Data
    @Schema(description = "等级创建请求DTO")
    public static class LevelRequestDTO implements Serializable {

        @Schema(description = "等级编码")
        @NotBlank(message = "等级编码不能为空")
        private String levelCode;

        @Schema(description = "等级名称")
        @NotBlank(message = "等级名称不能为空")
        private String levelName;

        @Schema(description = "等级排序")
        private Integer levelOrder;

        @Schema(description = "月费价格")
        private BigDecimal monthlyPrice;

        @Schema(description = "季费价格")
        private BigDecimal quarterlyPrice;

        @Schema(description = "年费价格")
        private BigDecimal yearlyPrice;

        @Schema(description = "折扣率")
        private BigDecimal discountRate;

        @Schema(description = "每日解析次数限制")
        private Integer dailyAnalysisLimit;

        @Schema(description = "存储空间(MB)")
        private Integer storageSpaceMb;

        @Schema(description = "可访问的AI模型列表")
        private List<String> aiModelAccess;

        @Schema(description = "优先客服支持")
        private Boolean prioritySupport;

        @Schema(description = "无广告体验")
        private Boolean adFree;

        @Schema(description = "高级功能列表")
        private List<String> advancedFeatures;

        @Schema(description = "等级描述")
        private String description;

        @Schema(description = "是否启用")
        private Boolean isActive;
    }

    /**
     * 等级统计 VO —— 用于返回等级统计数据
     */
    @Data
    @Schema(description = "等级统计VO")
    public static class LevelStatsVO implements Serializable {

        @Schema(description = "总等级数")
        private Long totalLevels;

        @Schema(description = "启用等级数")
        private Long activeLevels;

        @Schema(description = "禁用等级数")
        private Long inactiveLevels;

        @Schema(description = "会员总数")
        private Long totalMembers;

        @Schema(description = "各等级会员分布")
        private List<LevelMemberCount> levelMemberCounts;
    }

    @Data
    @Schema(description = "等级会员数量统计")
    public static class LevelMemberCount implements Serializable {
        @Schema(description = "等级ID")
        private Long levelId;

        @Schema(description = "等级名称")
        private String levelName;

        @Schema(description = "会员数量")
        private Long memberCount;
    }
}