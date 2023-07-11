package com.lianziyou.bot.model.req.sys.admin;

import com.lianziyou.bot.model.base.BasePageHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryPageReq extends BasePageHelper {


    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String mobile;


}
