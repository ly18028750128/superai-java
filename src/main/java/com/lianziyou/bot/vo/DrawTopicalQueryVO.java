package com.lianziyou.bot.vo;

import com.lianziyou.bot.constant.AiDrawConst.TopicalType;
import com.lianziyou.bot.vo.DrawTopicalQueryVO.DrawTopicalQueryParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel("首页查询参数")
public class DrawTopicalQueryVO extends CommonParam<DrawTopicalQueryParam> {

    public DrawTopicalQueryVO() {
        super.setParam(new DrawTopicalQueryParam());
    }

    @Data
    @ApiModel("查询参数")
    public static class DrawTopicalQueryParam {

        @ApiModelProperty("唯一主键")
        private Long id;

        @ApiModelProperty("主题名称")
        private String name;

        @ApiModelProperty("主题类型")
        @NotNull
        private TopicalType type;

        @ApiModelProperty("用户ID")
        private Long userId;
    }

}
