package com.lianziyou.bot.vo;

import com.lianziyou.bot.vo.HomeQueryVO.HomeQueryParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("首页数据查询参数")
public class HomeQueryVO extends CommonParam<HomeQueryParam> {

    public HomeQueryVO() {
        super.setParam(new HomeQueryParam());
    }

    @Data
    @ApiModel("查询参数")
    public static class HomeQueryParam {

        @ApiModelProperty("唯一主键，chat或者画图的ID")
        private Long id;

        @ApiModelProperty(value = "绘画主题id，关联t_use_ai_draw_topic表")
        private Long topicalId;

        @ApiModelProperty("用户ID")
        private Long userId;
    }

}
