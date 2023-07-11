package com.lianziyou.bot.model.req.sys;

import javax.validation.constraints.NotNull;
import lombok.Data;


@Data
public class OrderYiReturnReq {


    /**
     * 订单id
     */
    @NotNull
    private Long orderId;

}
