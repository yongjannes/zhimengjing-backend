package com.sf.zhimengjing.handler;

/**
 * @Title: MyMetaObjectHandler
 * @Author 殇枫
 * @Package com.sf.zhimengjing.handler
 * @description:
 */

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Title: MyMetaObjectHandler
 * @Author 殇枫
 * @Package com.sf.zhimengjing.handler
 * @description: 元对象处理器，用于自动填充实体类的创建和更新时间
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 在插入时填充创建时间和更新时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 在更新时填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}