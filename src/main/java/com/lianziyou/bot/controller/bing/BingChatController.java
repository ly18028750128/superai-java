package com.lianziyou.bot.controller.bing;


import com.alibaba.fastjson.JSONObject;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.sys.SendType;
import com.lianziyou.bot.model.MessageLog;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.gpt.Message;
import com.lianziyou.bot.model.req.bing.BingChatReq;
import com.lianziyou.bot.server.SseEmitterServer;
import com.lianziyou.bot.service.baidu.BaiDuService;
import com.lianziyou.bot.service.bing.BingChatService;
import com.lianziyou.bot.service.sys.AsyncService;
import com.lianziyou.bot.service.sys.CheckService;
import com.lianziyou.bot.service.sys.IMessageLogService;
import com.lianziyou.bot.utils.sys.JwtUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bing")
@Log4j2
@Transactional(rollbackFor = BussinessException.class)
public class BingChatController {

    @Resource
    BingChatService bingChatService;
    @Resource
    CheckService checkService;
    @Resource
    AsyncService asyncService;
    @Resource
    IMessageLogService messageLogService;
    @Resource
    BaiDuService baiDuService;


    @RequestMapping(value = "/chat", name = "bing对话")
    public ApiResult<String> bingChat(@Validated @RequestBody BingChatReq req) throws ExecutionException, InterruptedException {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if (null == sysConfig.getIsOpenBing() || sysConfig.getIsOpenBing() == 0) {
            return ApiResult.finalBuild("暂未开启newBing");
        }
        if (!baiDuService.textToExamine(req.getPrompt())) {
            throw new BussinessException("提问违反相关规定，请更换内容重新尝试");
        }
        List<Message> messages = messageLogService.createMessageLogList(req.getLogId(), req.getPrompt());
        Long logId = checkService.checkAndSaveMessageLog(MessageLog.builder()
            .useNumber(CommonConst.BING_NUMBER)
            .sendType(SendType.BING.getType())
            .useValue(JSONObject.toJSONString(messages))
            .userId(JwtUtil.getUserId()).build(), req.getLogId(),req.getPrompt());
        req.setUserId(JwtUtil.getUserId());
        String bingMessage = "";
        if (req.getIsOk() == 0) {
            List<MessageLog> list = messageLogService.lambdaQuery()
                .like(MessageLog::getUseValue, req.getPrompt())
                .eq(MessageLog::getSendType, SendType.BING.getType())
                .list();
            for (MessageLog m : list) {
                List<Message> messageList = JSONObject.parseArray(m.getUseValue(), Message.class);
                for (int i = 0; i < messageList.size(); i++) {
                    Message user = messageList.get(i);
                    if (user.getContent().replace("\n", "").equals(req.getPrompt()) && (i + 1) < messageList.size()) {
                        Message assistant = messageList.get(i + 1);
                        if (assistant.getRole().equals("assistant") && !assistant.getContent().contains("会话异常，请稍后重试")) {
                            bingMessage = assistant.getContent();
                            break;
                        }
                    }
                }
                if (!StringUtils.isEmpty(bingMessage)) {
                    break;
                }
            }
        }
        if (!StringUtils.isEmpty(bingMessage)) {
            Stream.of(bingMessage.split("")).forEach(m -> {
                SseEmitterServer.sendMessage(JwtUtil.getUserId(), m);
            });
            asyncService.endOfAnswer(logId, bingMessage);
        } else {
            bingChatService.ask(req, logId, asyncService);
        }
        return ApiResult.okBuild("请求成功，请稍后");
    }
}
