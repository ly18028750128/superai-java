package com.lianziyou.bot.model.res.sys;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonInclude(Include.NON_EMPTY)
public class ClientOrderRes {

    /**
     * 订单id
     */
    private Long id;

    /**
     * 订单金额
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer payNumber;

    @ApiModelProperty(value = "冲值次数")
    private Integer chargeCount;

    /**
     * 状态 0待支付 1支付完成 2支付失败
     */
    private Integer state;

    /**
     * 支付方式 wxpay、alipay、qqpay
     */
    private String payType;

    /**
     * 平台订单号、卡密
     */
    private String tradeNo;

    /**
     * 商品名
     */
    private String productName;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;

}
