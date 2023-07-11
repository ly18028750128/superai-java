package com.lianziyou.bot.model.res.mj;

import com.lianziyou.bot.enums.mj.Action;
import lombok.Data;

@Data
public class UVDataRes {

    private String id;
    private Action action;
    private int index;
}
