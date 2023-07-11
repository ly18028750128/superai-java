package com.lianziyou.bot.model.req.stablestudio;

import lombok.Data;

@Data
public class TextPrompt {

    /**
     * 描述
     */
    private String text;

    /**
     * 权重
     */
    private Double weight;
}
