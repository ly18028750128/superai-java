package com.lianziyou.bot.model.req.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;


/**
 * @author Administrator
 */
@Data
public class CreateOrderReq {

    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 产品id
     */
    @NonNull
    private Long productId;

    /**
     * 数量
     */
    @NonNull
    private Integer payNumber;

    /**
     * 支付类型
     */
    @NonNull
    private String payType;

}
