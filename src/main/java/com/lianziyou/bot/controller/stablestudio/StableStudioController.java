package com.lianziyou.bot.controller.stablestudio;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.sys.SendType;
import com.lianziyou.bot.model.MessageLog;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.req.stablestudio.TextToImageReq;
import com.lianziyou.bot.model.req.sys.MessageLogSave;
import com.lianziyou.bot.model.res.stablestudio.EngineRes;
import com.lianziyou.bot.model.res.stablestudio.TextToImageRes;
import com.lianziyou.bot.model.res.stablestudio.UserAccountRes;
import com.lianziyou.bot.service.sys.AsyncService;
import com.lianziyou.bot.service.sys.CheckService;
import com.lianziyou.bot.utils.gpt.Proxys;
import com.lianziyou.bot.utils.sys.DateUtil;
import com.lianziyou.bot.utils.sys.FileUtil;
import com.lianziyou.bot.utils.sys.JwtUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import com.lianziyou.bot.utils.sys.StringUtil;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stable/studio")
@Log4j2
public class StableStudioController {

    @Resource
    CheckService checkService;
    @Resource
    AsyncService asyncService;

    @PostMapping(value = "/user/account", name = "查询账户信息")
    public ApiResult<UserAccountRes> getUserAccount() {
        SysConfig sysConfig = getStableStudioConfig();
        String body = HttpUtil.createGet(sysConfig.getStableStudioApi() + CommonConst.STABLE_STUDIO_USER_ACCOUNT)
            .header(Header.AUTHORIZATION, "Bearer " + sysConfig.getStableStudioKey())
            .execute()
            .body();
        checkError(body);
        return ApiResult.okBuild(JSONObject.parseObject(body, UserAccountRes.class));
    }

    @PostMapping(value = "/user/balance", name = "查询账户余额")
    public ApiResult<String> getUserBalance() {
        SysConfig sysConfig = getStableStudioConfig();
        String body = HttpUtil.createGet(sysConfig.getStableStudioApi() + CommonConst.STABLE_STUDIO_USER_BALANCE)
            .header(Header.AUTHORIZATION, "Bearer " + sysConfig.getStableStudioKey())
            .execute()
            .body();
        checkError(body);
        return ApiResult.okBuild(JSONObject.parseObject(body).getString("credits"));
    }

    @PostMapping(value = "/engines/List", name = "获取引擎列表")
    public ApiResult<List<EngineRes>> getEnginesList() {
        SysConfig sysConfig = getStableStudioConfig();
        String body = HttpUtil.createGet(sysConfig.getStableStudioApi() + CommonConst.STABLE_STUDIO_ENGINES_LIST)
            .header(Header.AUTHORIZATION, "Bearer " + sysConfig.getStableStudioKey())
            .execute()
            .body();
        if (!body.contains("description") || !body.contains("type")) {
            checkError(body);
        }
        List<EngineRes> engineRes = JSONObject.parseArray(body, EngineRes.class);
        engineRes.removeIf((EngineRes e) -> !e.getType().equals("PICTURE"));
        return ApiResult.okBuild(engineRes);
    }

    @PostMapping(value = "/text/to/image", name = "文生图")
    public ApiResult<MessageLogSave> textToImage(@Validated @RequestBody TextToImageReq req) {
        SysConfig sysConfig = getStableStudioConfig();
        Long logId = checkService.checkAndSaveMessageLog(MessageLog.builder()
            .useNumber(CommonConst.STABLE_STUDIO_NUMBER)
            .sendType(SendType.STABLE_STUDIO.getType())
            .useValue(JSONObject.toJSONString(req.getTextPrompts()))
            .userId(JwtUtil.getUserId()).build(), null,req.getTextPrompts().get(0).getText());
        List<String> imgUrlList = new ArrayList<String>();
        List<String> returnImgUrlList = new ArrayList<String>();
        String startTime = DateUtil.getLocalDateTimeNow();
        Proxy proxy = null;
        if (null != sysConfig.getIsOpenProxy() && sysConfig.getIsOpenProxy() == 1) {
            proxy = Proxys.http(sysConfig.getProxyIp(), sysConfig.getProxyPort());
        }
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        String body = HttpUtil.createPost(sysConfig.getStableStudioApi() + String.format(CommonConst.STABLE_STUDIO_TEXT_TO_IMAGE, req.getEngineId()))
            .header(Header.AUTHORIZATION, "Bearer " + sysConfig.getStableStudioKey())
            .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
            .header(Header.ACCEPT, ContentType.JSON.getValue())
            .setProxy(proxy)
            .body(StringUtil.toUnderlineCase(req))
            .execute()
            .body();
        if (!body.contains("base64") || !body.contains("finishReason")) {
            checkError(body);
            //将用户使用次数返回
            asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.SD_NUMBER);
            throw new BussinessException("生成失败");
        }
        List<TextToImageRes> textToImageList = JSONObject.parseArray(JSONObject.parseObject(body).getJSONArray("artifacts").toJSONString(),
            TextToImageRes.class);
        textToImageList.forEach(t -> {
            if (!t.getFinishReason().equals("ERROR")) {
                try {
                    String localImgUrl = FileUtil.base64ToImage(t.getBase64());
                    imgUrlList.add(localImgUrl);
                    returnImgUrlList.add(sysConfig.getImgReturnUrl() + localImgUrl);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        MessageLogSave messageLogSave = MessageLogSave.builder()
            .prompt(JSONObject.toJSONString(req.getTextPrompts()))
            .type(SendType.STABLE_STUDIO.getRemark())
            .startTime(startTime)
            .imgList(imgUrlList).build();
        asyncService.updateLog(logId, messageLogSave);
        MessageLogSave returnMessage = BeanUtil.copyProperties(messageLogSave, MessageLogSave.class);
        returnMessage.setImgList(returnImgUrlList);
        return ApiResult.okBuild(returnMessage);
    }


    public SysConfig getStableStudioConfig() {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if (null == cacheObject.getIsOpenStableStudio() || cacheObject.getIsOpenStableStudio() == 0) {
            throw new BussinessException("暂未开启StableStudio");
        }
        return cacheObject;
    }

    public void checkError(String body) {
        JSONObject bodyJson = JSONObject.parseObject(body);
        if (body.contains("message")) {
            String message = bodyJson.getString("message");
            if (message.equals("missing authorization header")) {
                throw new BussinessException("api秘钥不正确");
            } else {
                log.error("stableStudio错误：{}", message);
                throw new BussinessException("发生了一些意外的服务器错误");
            }
        }
    }
}
