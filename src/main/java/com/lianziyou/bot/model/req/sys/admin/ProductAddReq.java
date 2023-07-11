package com.lianziyou.bot.model.req.sys.admin;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;


@Data
public class ProductAddReq implements Serializable {

    /**
     * 商品名
     */
    private String name;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 次数
     */
    private Integer numberTimes;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 库存数
     */
    private Integer stock;

}


