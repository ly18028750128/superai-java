package com.lianziyou.bot.model.res.sys.admin;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class GptKeyQueryRes {


    /**
     * id
     */
    private Long id;

    /**
     * key
     */
    private String key;


    /**
     * 使用次数
     */
    private Integer useNumber;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0 启用 1禁用
     */
    private Integer state;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    /**
     * key类型 3-gpt3.5 4-gpt4
     */
    private Integer type;

}
