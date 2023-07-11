package com.lianziyou.bot.model.res.gpt;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class GptBillingRes {

    /**
     * 总额度
     */
    private BigDecimal hardLimitUsd;

    /**
     * 已用额度
     */
    private BigDecimal totalUsage;

}
