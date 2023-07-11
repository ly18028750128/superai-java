package com.lianziyou.bot.enums.order;

import lombok.AllArgsConstructor;

/**
 * @author Administrator
 */

@AllArgsConstructor
public enum BusinessType {
    GPT("openAi聊天"),
    MID("midjourney画图");

    public final String description;

}
