package com.lianziyou.bot.model.wx;

import com.lianziyou.bot.enums.order.BusinessType;
import com.lianziyou.bot.enums.wx.WeixinTradeType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeixinPayParamDTO {

    private WeixinTradeType tradeType;  // 交易类型
    private BusinessType businessType;  // 业务类型
    private String description;  //  描述
    private String outTradeNo; // 外部订单号
    private BigDecimal total;  // 金额，需要乘以100
    private String openId;  // 用户openid
}
