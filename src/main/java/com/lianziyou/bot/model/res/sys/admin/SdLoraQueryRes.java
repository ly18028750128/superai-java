package com.lianziyou.bot.model.res.sys.admin;

import java.time.LocalDateTime;
import lombok.Data;


@Data
public class SdLoraQueryRes {


    /**
     * lora名
     */
    private String loraName;

    /**
     * lora图片地址
     */
    private String imgUrl;

    /**
     * 图片返回前缀地址
     */
    private String imgReturnUrl;

    /**
     * id
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}


