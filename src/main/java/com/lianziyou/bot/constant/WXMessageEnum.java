package com.lianziyou.bot.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum WXMessageEnum {
    WELCOME("***--欢迎关注链自由科技--***\n"
        + "客户端功能：1-GPT3.5/4.0流式上下文对话，2-GPT画图，3-SD画图（联系作者开启功能），4-newBing流式对话，5-MJ画图\n------------------\n"
        + "支付能力：1-微信支付，2-卡密支付\n------------------\n"
        + "注册能力：1-账号密码，2-邮箱，3-公众号\n------------------\n"
        + "管理端功能：1-用户管理，2-商品管理，3-订单管理，4-GPTKEY管理，5-卡密管理，6-邮箱管理，7-系统配置，8-操作日志\n------------------\n"
        + "作者承接App，公众号、小程序、网站、物联网、定制软件、DApp、会员系统，<a href=\"weixin://bizmsgmenu?msgmenucontent=联系作者&msgmenuid=1\">点击联系作者</a>\n------------------\n"
        + "<a href=\"weixin://bizmsgmenu?msgmenucontent=菜单&msgmenuid=1\">点击查询菜单</a>"),
    MENU("****************菜单*************\n------------------\n"
        + "<a href=\"weixin://bizmsgmenu?msgmenucontent=菜单&msgmenuid=1\">点击查询菜单</a>\n------------------\n"
        + "<a href=\"weixin://bizmsgmenu?msgmenucontent=查询&msgmenuid=1\">点击查询次数</a>\n------------------\n"
        + "<a href=\"weixin://bizmsgmenu?msgmenucontent=重置密码&msgmenuid=1\">点击重置密码</a>\n------------------\n"
        + "<a href=\"weixin://bizmsgmenu?msgmenucontent=联系作者&msgmenuid=1\">点击联系作者</a>\n------------------\n"
        + "输 入 '绑定-手机号' 即可与当前微信用户绑定例如(绑定-13344445556)\n------------------\n"
        + "输 入 '用户名-用户名' 可以修改用户名，例如（用户名-测试用户1）\n------------------\n"
        + "输 入 '修改密码-新密码' 即可修改当前用户密码例如(修改密码-123456),长度不能超过16位\n------------------\n"),
    BIND_SUCCESS("手机绑定成功!" + "\n手机号:%s\n访问地址：%s"),
    HAD_BIND("用户已经绑定手机!" + "\n手机号:%s\n访问地址：%s"),

    MOBILE_HAD_BIND("手机号已经被其它用户绑定，请联系作者!"),

    REST("密码重置成功!" + "\n新密码:%s\n访问地址：%s"),
    CHANG("密码修改成功!" + "\n新密码:%s\n访问地址：%s"),

    NEW_PASSWORD("新密码为：%s\n访问地址：%s"),
    USER_NAME_MODIFY_SUCCESS("用户名更改成功，新的用户名为：%s\n访问地址：%s"),
    MESSAGE_ERROR("输入内容格式不正确，请检查，或输入'菜单'查看格式'"),
    TICKET_VALIDATE_SUCCESS("登录验证成功[如果您是PC端扫码请忽略]，<a href=\"%s\">请点击返回易脑</a>"),

    ;
    public final String content;

    public String getContent(String... params) {
        if (params == null || params.length == 0) {
            return content;
        }
        return String.format(content, params);
    }
}
