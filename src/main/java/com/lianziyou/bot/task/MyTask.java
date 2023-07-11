package com.lianziyou.bot.task;

import static com.lianziyou.bot.constant.SseConst.SSE_SD_TOPIC_MAP_REDIS_KEY;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.lianziyou.bot.config.redis.RedissonConfig;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.sys.SendType;
import com.lianziyou.bot.model.MessageLog;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.req.sd.SdCreateReq;
import com.lianziyou.bot.model.req.sys.MessageLogSave;
import com.lianziyou.bot.model.req.sys.MessageLogSave.MessageLogSaveBuilder;
import com.lianziyou.bot.model.sse.SdMessageVo;
import com.lianziyou.bot.service.baidu.BaiDuService;
import com.lianziyou.bot.service.sys.AsyncService;
import com.lianziyou.bot.service.sys.CheckService;
import com.lianziyou.bot.utils.sys.DateUtil;
import com.lianziyou.bot.utils.sys.FileUtil;
import com.lianziyou.bot.utils.sys.QueueUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import com.lianziyou.bot.utils.sys.StringUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class MyTask {

    @Resource
    CheckService checkService;
    @Resource
    QueueUtil queueUtil;

    @Resource
    AsyncService asyncService;

    @Resource
    BaiDuService baiDuService;


    @Scheduled(fixedRate = 2000) // 每2秒执行一次
    public void run() {
        if (queueUtil.getCurrentQueueLength() < 1) {
            return;
        }
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        List<String> promptList = queueUtil.getCurrentQueue();
        List<String> imgUrlList = new ArrayList<String>();
        List<String> returnImgUrlList = new ArrayList<String>();
        String startTime = DateUtil.getLocalDateTimeNow();
        SdCreateReq req = JSONObject.parseObject(promptList.get(0), SdCreateReq.class);
        Long logId = checkService.checkAndSaveMessageLog(MessageLog.builder().useNumber(CommonConst.SD_NUMBER).sendType(SendType.SD.getType()).useValue(
                JSONObject.toJSONString(
                    MessageLogSave.builder().prompt(req.getPrompt()).type(SendType.SD.getRemark()).startTime(startTime).imgList(imgUrlList).build()))
            .userId(req.getUserId()).build(), null, req.getPrompt());
        String postUrl = cacheObject.getSdUrl();
        postUrl = postUrl + (null != req.getInitImages() && req.getInitImages().size() > 0 ? CommonConst.SD_IMG_2_IMG : CommonConst.SD_TXT_2_IMG);
        log.info("sd请求地址：{}", postUrl);
        SdCreateReq param = BeanUtil.copyProperties(req, SdCreateReq.class);
        String lora = null != req.getLoraList() ? String.join(" ", req.getLoraList()) : "";
        param.setPrompt(baiDuService.translateToEnglish(req.getPrompt()).trim() + lora);
        param.setLoraList(null);
        String body = HttpUtil.createPost(postUrl).header(Header.CONTENT_TYPE, ContentType.JSON.getValue()).body(StringUtil.toUnderlineCase(param)).execute()
            .body();
        MessageLogSaveBuilder messageLogSaveBuilder = MessageLogSave.builder().prompt(req.getPrompt()).type(SendType.SD.getRemark()).startTime(startTime)
            .success(true);
        try {
            JSONObject result = JSONObject.parseObject(body);
            if (result.containsKey("error")) {
                messageLogSaveBuilder.endTime(DateUtil.getLocalDateTimeNow()).success(false).message(result.get("error").toString());
            } else if (result.containsKey("images")) {
                List<String> images = JSONObject.parseArray(result.getJSONArray("images").toJSONString(), String.class);
                for (String item : images) {
                    String localImgUrl = FileUtil.base64ToImage(item);
                    imgUrlList.add(localImgUrl);
                    returnImgUrlList.add(cacheObject.getImgReturnUrl() + localImgUrl);
                    messageLogSaveBuilder.imgList(imgUrlList);
                }
                messageLogSaveBuilder.endTime(DateUtil.getLocalDateTimeNow());
            }
            MessageLogSave messageLogSave = messageLogSaveBuilder.build();
            asyncService.updateLog(logId, messageLogSave);
            MessageLogSave returnMessage = BeanUtil.copyProperties(messageLogSave, MessageLogSave.class);
            returnMessage.setImgList(returnImgUrlList);
            sendMessage(req.getUserId(), returnMessage);
            log.info("发送sd画图信息：userId：{}，sd画图内容：{}", req.getUserId(), returnMessage);
            queueUtil.remove(promptList.get(0));
        } catch (Exception e) {
            log.info("队列异常，错误信息：{}", e.getMessage(), e);
            MessageLogSave returnMessage = messageLogSaveBuilder.endTime(DateUtil.getLocalDateTimeNow()).success(false).message(e.getMessage()).build();
            sendMessage(req.getUserId(), returnMessage);
            queueUtil.remove(promptList.get(0));
            asyncService.updateRemainingTimes(req.getUserId(), CommonConst.SD_NUMBER);
        }

    }

    private void sendMessage(Long userId, MessageLogSave message) {
        String topicName = RedisUtil.getCacheMapValue(SSE_SD_TOPIC_MAP_REDIS_KEY, userId);
        RedissonConfig.getClient().getTopic(topicName).publish(SdMessageVo.builder().userId(userId).message(message).build());
    }
}
