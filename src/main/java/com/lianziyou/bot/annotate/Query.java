/*
 * Copyright (C) 2019-2022 All rights reserved, Designed By www.lianziyou.com
 */
package com.lianziyou.bot.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zheng Jie
 * @date 2019-6-4 13:52:30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    // Dong ZhaoYang 2017/8/7 基本对象的属性名
    String propName() default "";

    // Dong ZhaoYang 2017/8/7 查询方式
    Type type() default Type.EQUAL;

    /**
     * 多字段模糊搜索，仅支持String类型字段，多个用逗号隔开, 如@Query(blurry = "email,username")
     */
    String blurry() default "";

    boolean ignore() default false;

    enum Type {
        // jie 2019/6/4 相等
        EQUAL
        // Dong ZhaoYang 2017/8/7 大于等于
        ,
        GREATER_THAN
        //大于
        ,
        GREATER_THAN_NQ
        // Dong ZhaoYang 2017/8/7 小于等于
        ,
        LESS_THAN
        // Dong ZhaoYang 2017/8/7 中模糊查询
        ,
        INNER_LIKE
        // Dong ZhaoYang 2017/8/7 左模糊查询
        ,
        LEFT_LIKE
        // Dong ZhaoYang 2017/8/7 右模糊查询
        ,
        RIGHT_LIKE
        // Dong ZhaoYang 2017/8/7 小于
        ,
        LESS_THAN_NQ
        // jie 2019/6/4 包含
        ,
        IN
        // 不等于
        ,
        NOT_EQUAL
        // between
        ,
        BETWEEN
        // 不为空
        ,
        NOT_NULL
        // 查询时间
        ,
        UNIX_TIMESTAMP,
        LONG_TIMESTAMP;


    }

}

