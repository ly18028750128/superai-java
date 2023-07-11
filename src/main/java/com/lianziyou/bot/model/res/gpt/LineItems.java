package com.lianziyou.bot.model.res.gpt;


import java.math.BigDecimal;
import lombok.Data;

@Data
public class LineItems {

    /**
     * 使用时间
     */
    private String name;

    /**
     * 使用额度
     */
    private BigDecimal cost;
}
