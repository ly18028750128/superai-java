package com.lianziyou.bot.service.mj.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lianziyou.bot.config.redis.RedissonConfig;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.model.req.mj.MjCallBack;
import com.lianziyou.bot.model.sse.MidMessageVo;
import com.lianziyou.bot.service.mj.NotifyService;
import com.lianziyou.bot.service.sys.IUserService;
import com.lianziyou.bot.utils.sys.FileUtil;
import com.lianziyou.bot.utils.sys.PicUtils;
import com.lianziyou.bot.utils.sys.RedisUtil;
import com.lianziyou.bot.constant.SseConst;
import java.io.File;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Resource
    IUserService userService;
    @Resource
    WxMpService wxMpService;

    @Override
    public void notifyTaskChange(MjTask mjTask) {
        String notifyHook = mjTask.getNotifyHook();
        if (CharSequenceUtil.isBlank(notifyHook)) {
            return;
        }
        try {
            String paramsStr = OBJECT_MAPPER.writeValueAsString(mjTask);
//			log.info("任务变更, 触发推送, mjTask: {}", paramsStr);
            postJson(notifyHook, paramsStr);
        } catch (Exception e) {
            log.warn("回调通知接口失败: {}", e.getMessage());
        }
    }

    private void postJson(String notifyHook, String paramsJson) throws WxErrorException {
        MjCallBack mjTask = JSONObject.parseObject(paramsJson, MjCallBack.class);
        log.info("mj开始回调,回调内容：{}", mjTask);
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String fileLocalPath = null;
        if (null != mjTask.getImageUrl()) {
            fileLocalPath = mjTask.getImageUrl();
            mjTask.setImageUrl(FileUtil.imageUrlToBase64(mjTask.getImageUrl()));
        }

        if (mjTask.getSubType() != 2) {
            String topicName = RedisUtil.getCacheMapValue(SseConst.SSE_MID_TOPIC_MAP_REDIS_KEY, mjTask.getUserId());
            RedissonConfig.getClient().getTopic(topicName).publish(MidMessageVo.builder().userId(mjTask.getUserId()).message(mjTask).build());
            return;
        }

        if (mjTask.getStatus().equals(TaskStatus.SUCCESS)) {
            User user = userService.getById(mjTask.getUserId());
            String content;
            if (mjTask.getAction().equals(TaskAction.UPSCALE)) {
                content = "绘图完成\n\n" + "任务id：" + mjTask.getId();
            } else {
                content = "绘图完成\n\n" + "任务id：" + mjTask.getId() + "\n\n" + "放大命令：/U +空格+ 图片位置1.2.3.4 + 空格 +任务id\n\n" + "例如放大图片1：/U 1 "
                    + mjTask.getId() + "\n\n" + "变换命令：/V +空格+ 图片位置1.2.3.4 + 空格 +任务id\n\n" + "例如变换图片1：/V 1 " + mjTask.getId();
            }
            WxMpKefuMessage message = WxMpKefuMessage.TEXT().toUser(user.getFromUserName()).content(content).build();
            wxMpService.getKefuService().sendKefuMessage(message);
            if (null != mjTask.getImageUrl()) {
                String fileUrl = cacheObject.getImgUploadUrl() + fileLocalPath;
                PicUtils.commpressPicForScale(fileUrl, fileUrl);
                File file = new File(fileUrl);
                WxMediaUploadResult wxMediaUploadResult = wxMpService.getMaterialService().mediaUpload(WxConsts.MediaFileType.IMAGE, file);
                message = WxMpKefuMessage.IMAGE().toUser(user.getFromUserName()).mediaId(wxMediaUploadResult.getMediaId()).build();
                wxMpService.getKefuService().sendKefuMessage(message);
            }
        }

    }
}
