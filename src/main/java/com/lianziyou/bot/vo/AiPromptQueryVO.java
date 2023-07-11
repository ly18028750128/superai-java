package com.lianziyou.bot.vo;

import com.lianziyou.bot.annotate.Query;
import com.lianziyou.bot.annotate.Query.Type;
import com.lianziyou.bot.constant.AiPromptConst.PromptType;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiPromptQueryVO {

    @Query(ignore = true)
    @ApiModelProperty(value = "标签,可多选,用逗号分隔")
    private List<String> tags;


    @ApiModelProperty(value = "类型：GPT/MJ/SD")
    private PromptType type;

    @ApiModelProperty(value = "描述")
    @Query(type = Type.INNER_LIKE)
    private String description;
}
