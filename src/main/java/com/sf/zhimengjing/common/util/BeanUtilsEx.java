package com.sf.zhimengjing.common.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * @Title: BeanUtilsEx
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.util
 * @description: Bean 工具类扩展，用于获取对象中属性值为 null 的属性名
 */
public class BeanUtilsEx {

    /**
     * 获取对象中属性值为 null 的属性名数组
     * @param source 要检查的对象
     * @return 属性名数组
     */
    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        return emptyNames.toArray(new String[0]);
    }
}
