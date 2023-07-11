package com.lianziyou.bot.model.req.mj;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskReq {

    @NotNull
    private Long id;
}
