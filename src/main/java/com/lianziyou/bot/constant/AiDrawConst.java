package com.lianziyou.bot.constant;

import lombok.AllArgsConstructor;

public interface AiDrawConst {


    //1(Mid) 2:SD 3:Flagstudio 4:其它
    @AllArgsConstructor
    enum TopicalType {
        MID(1, "Mid journey"),
        SD(2, "stabled diffusion"),
        FLAG_STUDIO(3, "flag studio"),
        ;

        public final Integer value; // 标签获取的URL

        public final String description; // 标签获取的URL
    }


}
