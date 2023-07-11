package com.lianziyou.bot.model.res.sys.admin;

import java.io.Serializable;
import lombok.Data;


@Data
public class PayConfigQueryRes implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 易支付pid
     */
    private Integer pid;

    /**
     * 易支付secretKey
     */
    private String secretKey;

    /**
     * 易支付支付请求地址
     */
    private String submitUrl;

    /**
     * 易支付订单查询api
     */
    private String apiUrl;

    /**
     * 支付类型 0 易支付 1卡密
     */
    private Integer payType;

    /**
     * id
     */
    private Long id;


}
