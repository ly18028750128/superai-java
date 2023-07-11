package com.lianziyou.bot.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lianziyou.bot.model.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("gpt_key")
public class GptKey extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;
    /**
     * key
     */
    @TableField("`key`")
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
     * "状态 0 启用 1禁用
     */
    private Integer state;


    /**
     * key类型 3-gpt3.5 4-gpt4
     */
    private Integer type;


}
