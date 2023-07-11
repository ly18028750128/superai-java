package com.lianziyou.bot.service.prompt.impl;

import static com.lianziyou.bot.constant.AiPromptConst.PLEX_PT;
import static com.lianziyou.bot.constant.AiPromptConst.PromptSource.PLEXPT;
import static com.lianziyou.bot.service.prompt.IGetPrompt.GET_PREFIX_NAME;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lianziyou.bot.constant.AiPromptConst.PromptSource;
import com.lianziyou.bot.constant.AiPromptConst.PromptType;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.dao.AiPromptDao;
import com.lianziyou.bot.model.AiPrompt;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.service.prompt.IGetPrompt;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service(GET_PREFIX_NAME + PLEX_PT)
@Slf4j
public class PlexPtGptGetPromptImpl implements IGetPrompt {

    private final PromptSource source = PLEXPT;
    @Resource
    AiPromptDao aiPromptDao;

    @Override
    public void get() throws Exception {
//        String rawUrl = "https://raw.githubusercontent.com/" + owner + "/" + repo + "/main/" + filePath;

        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String promptUrl = "https://raw.githubusercontent.com/PlexPt/awesome-chatgpt-prompts-zh/main/prompts-zh.json";
        String promptStr = HttpUtil.createGet(promptUrl).setHttpProxy(sysConfig.getProxyIp(), sysConfig.getProxyPort()).execute().body();
        JSONArray promptDatas = JSON.parseArray(promptStr);

        for (int idx1 = 0; idx1 < promptDatas.size(); idx1++) {
            JSONObject promptObject = promptDatas.getObject(idx1, JSONObject.class);
            AiPrompt aiPrompt = AiPrompt.builder().initPrompt(promptObject.getString("prompt")).description(promptObject.getString("act"))
                .source(source).type(PromptType.GPT).tagCn("未分类").tagEn("unknow").operateTime(LocalDateTime.now()).createTime(LocalDateTime.now())
                .creator(0L).operator(0L).build();
            aiPromptDao.insertOrUpdate(aiPrompt);
        }

    }
}
