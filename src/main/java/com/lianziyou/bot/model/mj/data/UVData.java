package com.lianziyou.bot.model.mj.data;

import com.lianziyou.bot.enums.mj.TaskAction;
import lombok.Data;

@Data
public class UVData {

    private String id;
    private TaskAction taskAction;
    private int index;
}
