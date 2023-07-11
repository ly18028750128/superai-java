package com.lianziyou.bot.model.req.sys.admin;

import javax.validation.constraints.NotNull;
import lombok.Data;


@Data
public class GptKeyUpdateStateReq {

    /**
     * id
     */
    @NotNull
    private Long id;

    /**
     * 状态 0 启用 1禁用
     */
    @NotNull
    private Integer state;
}
