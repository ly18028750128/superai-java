package com.lianziyou.bot.model.res.sys;


import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientRechargeRes {

    /**
     * 商品列表
     */
    List<ClientProductRes> productList;

    /**
     * 订单列表
     */
    List<ClientOrderRes> orderList;

    /**
     * 支付类型 0 易支付 1卡密
     */
    private Integer payType;

}
