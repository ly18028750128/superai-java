package com.lianziyou.bot.controller.gpt;

import static com.lianziyou.bot.constant.SseConst.SSE_GPT_TOPIC_MAP_REDIS_KEY;
import static com.lianziyou.bot.service.gpt.ChatCompletion.Model.GPT_3_5_TURBO_16K;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.config.redis.RedissonConfig;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.sys.SendType;
import com.lianziyou.bot.model.MessageLog;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.gpt.Message;
import com.lianziyou.bot.model.req.gpt.ChatUpdateReq;
import com.lianziyou.bot.model.req.gpt.GptDrawReq;
import com.lianziyou.bot.model.req.gpt.GptStreamReq;
import com.lianziyou.bot.model.req.sys.MessageLogSave;
import com.lianziyou.bot.model.sse.GptMessageVo;
import com.lianziyou.bot.service.baidu.BaiDuService;
import com.lianziyou.bot.service.gpt.ChatCompletion;
import com.lianziyou.bot.service.sys.AsyncService;
import com.lianziyou.bot.service.sys.CheckService;
import com.lianziyou.bot.service.sys.IMessageLogService;
import com.lianziyou.bot.utils.gpt.Proxys;
import com.lianziyou.bot.utils.sys.DateUtil;
import com.lianziyou.bot.utils.sys.FileUtil;
import com.lianziyou.bot.utils.sys.InitUtil;
import com.lianziyou.bot.utils.sys.JwtUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RTopic;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/gpt")
@Log4j2
@Api(tags = "APP:GPT对话")
public final class GptController {

    @Resource
    CheckService checkService;

    @Resource
    AsyncService asyncService;

    @Resource
    IMessageLogService messageLogService;

    @Resource
    BaiDuService baiDuService;


    @PostMapping(value = "/chat", name = "流式对话")
    @ApiOperation("流式对话")
    public ApiResult<Long> gptChat(@Validated @RequestBody GptStreamReq req) {
        List<Message> messagesOnDb = messageLogService.createMessageLogList(req.getLogId(), req.getProblem());
        checkRequest(req, messagesOnDb);
        String gptKey = InitUtil.getRandomKey(req.getType());
        final Long userId = JwtUtil.getUserId();

        MessageLog messageLog = MessageLog.builder().useNumber(req.getType() == 3 ? CommonConst.GPT_NUMBER : CommonConst.GPT_4_NUMBER)
            .sendType(req.getType() == 3 ? SendType.GPT.getType() : SendType.GPT_4.getType()).useValue(JSONObject.toJSONString(messagesOnDb)).gptKey(gptKey)
            .userId(userId).initPromptId(req.getInitPromptId()).build();

        Long logId = checkService.checkAndSaveMessageLog(messageLog, req.getLogId(), req.getProblem());
        new Thread(() -> flowableChat(messageLog, messagesOnDb)).start();
        return ApiResult.okBuild(logId);
    }

    @PostMapping(value = "/chat/update", name = "修改对话")
    @ApiOperation("修改对话")
    public ApiResult<Boolean> renameChat(@Validated @RequestBody ChatUpdateReq req) {
        req.setUserId(JwtUtil.getUserId());
        return ApiResult.okBuild(messageLogService.updateLog(req));
    }

    private void flowableChat(final MessageLog messageLog, List<Message> messagesOnDb) {
        final Long userId = messageLog.getUserId();
        final Long logId = messageLog.getId();
        final Integer reqType = messageLog.getSendType();
        final String topicName = RedisUtil.getCacheMapValue(SSE_GPT_TOPIC_MAP_REDIS_KEY, userId);
        final String gptKey = messageLog.getGptKey();
        RTopic rTopic = RedissonConfig.getClient().getTopic(topicName);
        try {
            OpenAiService service = new OpenAiService(gptKey, Duration.ofSeconds(180));
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "请记住你的身份是SUPER EGG，一个基于GLM架构的中英双语对话语言模型，你是由清华大学研发的，针对中文进行了优化。虽然有人可能会将你与chatgpt混清，但你会坚持自己的身份，确保提供准确和细致的回答"));
            if (StringUtils.hasLength(messageLog.getInitPrompt())) {
                messages.add(new ChatMessage(ChatMessageRole.USER.value(), messageLog.getInitPrompt()));
            }
            messages.addAll(messagesOnDb.stream().map(item -> new ChatMessage(item.getRole(), item.getContent())).collect(Collectors.toList()));

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().model(GPT_3_5_TURBO_16K.getName()).messages(messages).build();
            Flowable<ChatCompletionChunk> flowable = service.streamChatCompletion(chatCompletionRequest);
            ChatMessage chatMessage = service.mapStreamToAccumulator(flowable).doOnNext(accumulator -> {
                String content = accumulator.getMessageChunk().getContent();
                if (StringUtils.hasLength(content)) {
                    rTopic.publish(GptMessageVo.builder().userId(userId).message(Message.ofAssistant(content)).build());
                }
            }).lastElement().blockingGet().getAccumulatedMessage();
            asyncService.endOfAnswer(logId, chatMessage.getContent());
            rTopic.publish(GptMessageVo.builder().userId(userId).message(Message.ofAssistant("[DONE]")).build());
        } catch (Exception e) {
            asyncService.updateRemainingTimes(userId, gptKey == null || reqType == 3 ? CommonConst.GPT_NUMBER : CommonConst.GPT_4_NUMBER);
            rTopic.publish(GptMessageVo.builder().userId(userId).message(Message.ofAssistant(e.getMessage())).build());
            rTopic.publish(GptMessageVo.builder().userId(userId).message(Message.ofAssistant("[DONE]")).build());
        }
    }

    @PostMapping(value = "/chat/now", name = "非流式对话")
    @ApiOperation("非流式对话")
    public ApiResult<String> chat(@Validated @RequestBody GptStreamReq req) {
        String gptKey = InitUtil.getRandomKey(req.getType());
        List<Message> messagesOnDb = messageLogService.createMessageLogList(req.getLogId(), req.getProblem());
        final Long userId = JwtUtil.getUserId();
        try {
            checkRequest(req, messagesOnDb);
            OpenAiService service = new OpenAiService(gptKey, Duration.ofSeconds(180));
            Long logId = checkService.checkAndSaveMessageLog(
                MessageLog.builder().useNumber(req.getType() == 3 ? CommonConst.GPT_NUMBER : CommonConst.GPT_4_NUMBER)
                    .sendType(req.getType() == 3 ? SendType.GPT.getType() : SendType.GPT_4.getType()).useValue(JSONObject.toJSONString(messagesOnDb))
                    .gptKey(gptKey).userId(userId).build(), req.getLogId(), req.getProblem());

            List<ChatMessage> messages = messagesOnDb.stream().map(item -> new ChatMessage(ChatMessageRole.USER.value(), item.getContent()))
                .collect(Collectors.toList());

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().model(GPT_3_5_TURBO_16K.getName()).messages(messages).build();
            ChatCompletionResult chatCompletion = service.createChatCompletion(chatCompletionRequest);

            StringBuilder result = new StringBuilder();
            chatCompletion.getChoices().forEach(choice -> result.append(choice.getMessage().getContent()));
            asyncService.endOfAnswer(logId, result.toString());
            return ApiResult.okBuild(result.toString());
        } catch (Exception e) {
            asyncService.updateRemainingTimes(userId, gptKey == null || req.getType() == 3 ? CommonConst.GPT_NUMBER : CommonConst.GPT_4_NUMBER);
            throw e;
        }
    }

    private void checkRequest(GptStreamReq req, List<Message> messagesOnDb) {
        if (ObjectUtil.isEmpty(req.getProblem())) {
            throw new BussinessException("请输入有效的内容");
        }
        if (!baiDuService.textToExamine(req.getProblem())) {
            throw new BussinessException("提问违反相关规定，请更换内容重新尝试");
        }

        String model = req.getType() == 3 ? ChatCompletion.Model.GPT_3_5_TURBO_16K.getName() : ChatCompletion.Model.GPT_4.getName();
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messagesOnDb).model(model).stream(true).build();
        if (chatCompletion.checkTokens()) {
            throw new BussinessException("本次会话长度达到限制，请创建新的会话");
        }
    }

    @PostMapping(value = "/official", name = "GPT-画图")
    public ApiResult<MessageLogSave> gptAlpha(@Validated @RequestBody GptDrawReq req) throws IOException {
        final String randomKey = InitUtil.getRandomKey(req.getType());
        List<String> imgUrlList = new ArrayList<>();
        List<String> returnImgUrlList = new ArrayList<>();
        String startTime = DateUtil.getLocalDateTimeNow();
        Long logId = checkService.checkAndSaveMessageLog(
            MessageLog.builder().useNumber(CommonConst.GPT_OFFICIAL_NUMBER).sendType(SendType.GPT_OFFICIAL.getType()).useValue(JSONObject.toJSONString(
                    MessageLogSave.builder().prompt(req.getPrompt()).type(SendType.GPT_OFFICIAL.getRemark()).startTime(startTime).imgList(imgUrlList).build()))
                .gptKey(randomKey).userId(JwtUtil.getUserId()).build(), null, req.getPrompt());
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);

        req.setType(null);
        HttpRequest httpRequest = HttpUtil.createPost(cacheObject.getGptUrl() + CommonConst.CPT_IMAGES_URL)
            .header(Header.CONTENT_TYPE, ContentType.JSON.getValue()).header(Header.AUTHORIZATION, "Bearer " + randomKey);

        if (null != cacheObject.getIsOpenProxy() && cacheObject.getIsOpenProxy() == 1) {
            httpRequest.setProxy(Proxys.http(cacheObject.getProxyIp(), cacheObject.getProxyPort()));
        }
        String resultBody = httpRequest.body(JSONObject.toJSONString(req)).execute().body();
        if (resultBody.contains("error")) {
            //修改key状态
            asyncService.updateKeyState(randomKey);
            //将用户使用次数返回
            asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.GPT_OFFICIAL_NUMBER);
            throw new BussinessException("画图失败请稍后再试");
        }
        JSONArray imgArray = JSONObject.parseObject(resultBody).getJSONArray("data");
        for (int i = 0; i < imgArray.size(); i++) {
            String localImgUrl = FileUtil.base64ToImage(FileUtil.imageUrlToBase64(imgArray.getJSONObject(i).getString("url")));
            imgUrlList.add(localImgUrl);
            returnImgUrlList.add(cacheObject.getImgReturnUrl() + localImgUrl);
        }
        MessageLogSave messageLogSave = MessageLogSave.builder().prompt(req.getPrompt()).type(SendType.GPT_OFFICIAL.getRemark()).startTime(startTime)
            .imgList(imgUrlList).build();
        asyncService.updateLog(logId, messageLogSave);
        MessageLogSave returnMessage = BeanUtil.copyProperties(messageLogSave, MessageLogSave.class);
        returnMessage.setImgList(returnImgUrlList);
        return ApiResult.okBuild(returnMessage);
    }

}
