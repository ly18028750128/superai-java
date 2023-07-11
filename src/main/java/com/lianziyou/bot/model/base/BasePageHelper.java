package com.lianziyou.bot.model.base;


import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;


@Data
public class BasePageHelper implements Serializable {


    @NotNull(message = "当前页数不能为空")
    private Integer pageNumber;

    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

}
