package com.lianziyou.bot.model.gpt.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;


@Data
public class UseageResponse {

    /**
     * 总使用金额：美元
     */
    @JsonProperty("total_usage")
    private BigDecimal totalUsage;

}
