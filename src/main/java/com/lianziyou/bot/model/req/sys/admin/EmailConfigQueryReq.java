package com.lianziyou.bot.model.req.sys.admin;

import com.lianziyou.bot.model.base.BasePageHelper;
import lombok.Data;

@Data
public class EmailConfigQueryReq extends BasePageHelper {


    /**
     * 邮件账号
     */
    private String username;

}
