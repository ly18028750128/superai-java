package com.lianziyou.bot.model.base;


import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class BaseDeleteEntity implements Serializable {


    @NotEmpty(message = "删除数组不能为空")
    private List<Long> ids;
}
