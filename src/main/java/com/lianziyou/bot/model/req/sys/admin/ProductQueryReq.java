package com.lianziyou.bot.model.req.sys.admin;

import com.lianziyou.bot.model.base.BasePageHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class ProductQueryReq extends BasePageHelper {


    /**
     * 商品名
     */
    private String name;


}


