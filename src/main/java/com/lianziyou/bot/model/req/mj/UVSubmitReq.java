package com.lianziyou.bot.model.req.mj;

import com.lianziyou.bot.enums.mj.TaskAction;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交UV任务参数
 */
@Data
public class UVSubmitReq {

    @NotNull
    private Long id;

    @NotNull
    private TaskAction taskAction;

    @NotNull
    private Integer index;

}
