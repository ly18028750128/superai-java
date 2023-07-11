package com.lianziyou.bot.vo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class CommonParam<T> implements Serializable {


    @ApiModelProperty(value = "页数", required = true, example = "1")
    private Integer page = 1;
    @ApiModelProperty(value = "条数", required = true, example = "10")
    private Integer limit = 20;
    @ApiModelProperty(value = "排序", example = "id desc")
    private String sorts;
//    @ApiModelProperty(Api)
    T param ;

    public void setPage(Integer page) {
        this.page = page;
        if (this.page < 1) {
            throw new IllegalArgumentException("Page number is too small");
        }
    }


    /**
     * 获取起始行数
     *
     * @return
     */
    public Integer getStart() {
        return (page - 1) * limit;
    }


}
