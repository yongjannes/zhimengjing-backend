package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Title: DreamTagRelation
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @description: 梦境标签关联实体类，对应表 dream_tag_relations，
 * 用于存储梦境与标签的关联关系。
 */
@Data
@TableName("dream_tag_relations")
public class DreamTagRelation extends BaseEntity implements Serializable {

    /**
     * 梦境ID
     */
    @TableField("dream_id")
    private Long dreamId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 相关度分数（默认1.00）
     */
    @TableField("relevance_score")
    private BigDecimal relevanceScore;

    /**
     * 删除标志（0-正常，1-删除）
     */
    @TableField("delete_flag")
    @TableLogic
    private Integer deleteFlag;
}