package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: Reviewer
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: 审核员实体类，用于存储平台审核人员的基本信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reviewers")
public class Reviewer extends BaseEntity {

    /** 关联的用户ID（与用户表对应，标识该审核员对应的系统用户） */
    private Long userId;

    /** 审核员姓名（用于展示和标识） */
    private String reviewerName;

    /** 审核员等级（如：1-初级审核员，2-中级审核员，3-高级审核员） */
    private Integer reviewerLevel;

    /** 是否启用（true：在职/可用，false：禁用/停用） */
    private Boolean isActive;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}
