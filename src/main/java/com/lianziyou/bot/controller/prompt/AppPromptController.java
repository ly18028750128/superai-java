package com.lianziyou.bot.controller.prompt;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.AiPromptConst.PromptType;
import com.lianziyou.bot.model.AiPrompt;
import com.lianziyou.bot.service.sys.AiPromptService;
import com.lianziyou.bot.utils.MyBatisPlusUtil;
import com.lianziyou.bot.vo.AiPromptQueryVO;
import com.lianziyou.bot.vo.CommonParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Administrator
 */
@RestController
@RequestMapping("/app/prompt")
@Api(tags = "APP:Ai提示词相关API", value = "APP:Ai提示词相关API")
public class AppPromptController {


    @Resource
    AiPromptService aiPromptService;

    @GetMapping(value = "/tags")
    @ApiOperation("查询提示词标签")
    public ApiResult<PageInfo<AiPrompt>> listTags(@RequestParam("promptType") PromptType promptType, CommonParam pageParam) {
        LambdaQueryWrapper<AiPrompt> wrapper = Wrappers.<AiPrompt>query().select("DISTINCT tag_en,tag_cn").lambda().eq(AiPrompt::getType, promptType)
            .eq(AiPrompt::getIsPublish, 1);

        PageHelper.startPage(pageParam.getPage(), pageParam.getLimit(), pageParam.getSorts());

        return ApiResult.okBuild(PageInfo.of(aiPromptService.list(wrapper)));
    }


    @GetMapping("")
    @ApiOperation("查询提示词")
    public ApiResult<PageInfo<AiPrompt>> listConfig(AiPromptQueryVO promptQueryVO, CommonParam pageParam) {
        QueryWrapper<AiPrompt> queryWrapper = MyBatisPlusUtil.single().getPredicate(promptQueryVO);
        if (CollectionUtil.isEmpty(promptQueryVO.getTags())) {
            queryWrapper.select("distinct description, init_prompt");
        } else {
            queryWrapper.select("id","tag_cn, tag_en, description, init_prompt").and(wrapper -> {
                wrapper.lambda().or().in(AiPrompt::getTagCn, promptQueryVO.getTags()).or().in(AiPrompt::getTagEn, promptQueryVO.getTags());
            });
        }
        queryWrapper.eq("is_publish", 1);
        PageHelper.startPage(pageParam.getPage(), pageParam.getLimit(), pageParam.getSorts());
        List<AiPrompt> aiPrompts = aiPromptService.list(queryWrapper);
        return ApiResult.okBuild(PageInfo.of(aiPrompts));
    }


}
