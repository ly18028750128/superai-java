package com.lianziyou.bot.service.prompt.impl;

import static com.lianziyou.bot.constant.AiPromptConst.FLOW_GPT;
import static com.lianziyou.bot.service.prompt.IGetPrompt.GET_PREFIX_NAME;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lianziyou.bot.constant.AiPromptConst.PromptSource;
import com.lianziyou.bot.constant.AiPromptConst.PromptType;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.dao.AiPromptDao;
import com.lianziyou.bot.model.AiPrompt;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.service.prompt.IGetPrompt;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service(GET_PREFIX_NAME + FLOW_GPT)
@Slf4j
public class FlowGptGetPromptImpl implements IGetPrompt {

    private final PromptSource source = PromptSource.FLOWGPT;
    @Resource
    AiPromptDao aiPromptDao;

    @Override
    public void get() throws Exception {
        for (int step = 0; step < 1000; step++) {
            String promptUrl = "https://flowgpt.com/api/trpc/prompt.getPrompts?batch=1&input=";
            String input = "{\"0\":{\"json\":{\"skip\":%d,\"tag\":null,\"sort\":\"most-popular\",\"q\":null,\"language\":\"zh\"},\"meta\":{\"values\":{\"tag\":[\"undefined\"],\"q\":[\"undefined\"]}}}}";
            final String getUrl = promptUrl + URLEncoder.encode(String.format(input, step * 36), "UTF8");
            SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
            String promptStr = HttpUtil.createGet(getUrl).setHttpProxy(sysConfig.getProxyIp(), sysConfig.getProxyPort()).execute().body();
            JSONArray promptArrayObject = JSON.parseArray(promptStr);
            JSONArray promptDatas = promptArrayObject.getObject(0, JSONObject.class).getObject("result", JSONObject.class).getObject("data", JSONObject.class)
                .getObject("json", JSONArray.class);
            if (promptDatas == null || promptDatas.isEmpty()) {
                break;
            }
            log.info("step={}", step);
            for (int idx1 = 0; idx1 < promptDatas.size(); idx1++) {
                JSONObject promptObject = promptDatas.getObject(idx1, JSONObject.class);
                JSONArray tagsObjectArray = promptObject.getObject("Tag", JSONArray.class);
                final String initPrompt = promptObject.getString("initPrompt");

                if (aiPromptDao.selectCount(Wrappers.<AiPrompt>lambdaQuery().eq(AiPrompt::getInitPrompt, initPrompt)) > 0) {
                    continue;
                }

                final String description = promptObject.getString("description");
                if (tagsObjectArray == null || tagsObjectArray.isEmpty()) {
                    AiPrompt aiPrompt = AiPrompt.builder().initPrompt(initPrompt).description(description).source(source).type(PromptType.GPT).tagCn("unknow")
                        .tagEn("unknow").operateTime(LocalDateTime.now()).createTime(LocalDateTime.now()).creator(0L).operator(0L).build();
                    aiPromptDao.insertOrUpdate(aiPrompt);
                } else {
                    for (int idx2 = 0; idx2 < tagsObjectArray.size(); idx2++) {
                        JSONObject tagsObject = tagsObjectArray.getObject(idx2, JSONObject.class);
                        AiPrompt aiPrompt = AiPrompt.builder().initPrompt(initPrompt).description(description).source(source).type(PromptType.GPT)
                            .tagCn(tagsObject.getString("name")).tagEn(tagsObject.getString("name")).operateTime(LocalDateTime.now())
                            .createTime(LocalDateTime.now()).creator(0L).operator(0L).build();
                        aiPromptDao.insertOrUpdate(aiPrompt);
                    }
                }
            }
        }
    }
}
