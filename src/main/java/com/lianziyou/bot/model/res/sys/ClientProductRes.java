package com.lianziyou.bot.model.res.sys;


import java.math.BigDecimal;
import lombok.Data;

@Data
public class ClientProductRes {

    /**
     * 商品id
     */
    private Long id;

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
     * 库存数
     */
    private Integer stock;

}
