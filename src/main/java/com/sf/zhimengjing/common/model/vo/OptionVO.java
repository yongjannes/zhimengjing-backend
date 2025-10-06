package com.sf.zhimengjing.common.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: OptionVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 通用下拉选项视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionVO {

    /**
     * 选项的值
     */
    private String value;

    /**
     * 选项的标签
     */
    private String label;
}