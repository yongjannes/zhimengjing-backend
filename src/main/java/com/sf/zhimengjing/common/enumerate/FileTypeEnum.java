package com.sf.zhimengjing.common.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title: FileTypeEnum
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 文件类型枚举，定义支持的文件类型及对应扩展名
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum {

    /**
     * 图片类型
     */
    IMAGE("image", "图片", "jpg,jpeg,png,gif,webp,bmp"),

    /**
     * 文档类型
     */
    DOCUMENT("document", "文档", "pdf,doc,docx,xls,xlsx,txt"),

    /**
     * 压缩包类型
     */
    ARCHIVE("archive", "压缩包", "zip,rar,7z"),

    /**
     * 视频类型
     */
    VIDEO("video", "视频", "mp4,avi,mov,wmv"),

    /**
     * 音频类型
     */
    AUDIO("audio", "音频", "mp3,wav,flac"),
    /**
     * 其他/通用类型
     * 用于通用上传接口，其扩展名校验将依赖于 application.yml 中的总列表
     */
    OTHER("other", "其他", "");

    /**
     * 类型标识
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 支持的扩展名（逗号分隔）
     */
    private final String extensions;
}