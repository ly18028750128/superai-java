package com.lianziyou.bot.model.req.sys.admin;

import com.lianziyou.bot.model.base.BasePageHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class SdLoraQueryReq extends BasePageHelper {


    /**
     * lora名
     */
    private String loraName;


}


