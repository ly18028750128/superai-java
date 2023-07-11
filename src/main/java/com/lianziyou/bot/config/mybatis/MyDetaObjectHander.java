package com.lianziyou.bot.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lianziyou.bot.utils.sys.JwtUtil;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyDetaObjectHander implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
//        log.info("come to insert fill .........");
        //setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject)
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("operateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("creator", JwtUtil.getUserId(), metaObject);
        this.setFieldValByName("operator", JwtUtil.getUserId(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
//        log.info("come to update fill .......");
        this.setFieldValByName("operateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("operator", JwtUtil.getUserId(), metaObject);

    }
}
