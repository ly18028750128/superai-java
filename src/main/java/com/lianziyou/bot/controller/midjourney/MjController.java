package com.lianziyou.bot.controller.midjourney;

import cn.hutool.core.text.CharSequenceUtil;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.controller.midjourney.support.TaskCondition;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.req.mj.DescribeReq;
import com.lianziyou.bot.model.req.mj.MjCallBack;
import com.lianziyou.bot.model.req.mj.SubmitBlendReq;
import com.lianziyou.bot.model.req.mj.SubmitReq;
import com.lianziyou.bot.model.req.mj.TaskReq;
import com.lianziyou.bot.model.req.mj.UVSubmitReq;
import com.lianziyou.bot.service.baidu.BaiDuService;
import com.lianziyou.bot.service.mj.TaskService;
import com.lianziyou.bot.service.mj.TaskStoreService;
import com.lianziyou.bot.service.sys.CheckService;
import com.lianziyou.bot.utils.OpenAiUtil;
import com.lianziyou.bot.utils.mj.BannedPromptUtils;
import com.lianziyou.bot.utils.mj.MimeTypeUtils;
import com.lianziyou.bot.utils.sys.IDUtil;
import com.lianziyou.bot.utils.sys.JwtUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mj")
@Log4j2
public class MjController {

    @Resource
    BaiDuService baiDuService;
    @Resource
    TaskStoreService taskStoreService;
    @Resource
    CheckService checkService;
    @Resource
    TaskService taskService;

    @PostMapping(value = "/submit", name = "提交Imagine或UV任务")
    public ApiResult<MjTask> submit(@RequestBody SubmitReq req) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if (null == sysConfig.getIsOpenMj() || sysConfig.getIsOpenMj() == 0) {
            throw new BussinessException("暂未开启Mj");
        }
        String prompt = req.getPrompt();
        if (CharSequenceUtil.isBlank(prompt)) {
            throw new BussinessException("prompt不能为空");
        }
        prompt = prompt.trim();
        MjTask mjTask = newTask();
        mjTask.setAction(TaskAction.IMAGINE);
        mjTask.setPrompt(prompt);
        mjTask.setTopicalId(req.getTopicalId());
        mjTask.setImgList(req.getImgList());
        String promptEn;
        int paramStart = prompt.indexOf(" --");
//		if (paramStart > 0) {
//			promptEn = this.baiDuService.translateToEnglish(prompt.substring(0, paramStart)).trim() + prompt.substring(paramStart);
//		} else {
//			promptEn = this.baiDuService.translateToEnglish(prompt).trim();
//		}
        if (paramStart > 0) {
            promptEn = OpenAiUtil.single().getMjPrompt(prompt.substring(0, paramStart)).trim() + prompt.substring(paramStart);
        } else {
            promptEn = OpenAiUtil.single().getMjPrompt(prompt).trim();
        }
        if (CharSequenceUtil.isBlank(promptEn)) {
            promptEn = prompt;
        }
        if (BannedPromptUtils.isBanned(promptEn)) {
            throw new BussinessException("可能包含敏感词");
        }
        checkService.checkAndSaveMessageLog(JwtUtil.getUserId(), CommonConst.MJ_NUMBER);
        mjTask.setPromptEn(promptEn
            + (StringUtils.hasLength(req.getNo()) ? " --no " + (this.baiDuService.translateToEnglish(req.getNo())) : "")
            + (StringUtils.hasLength(req.getVersion()) ? " " + req.getVersion() : "")
            + (StringUtils.hasLength(req.getStyle()) ? " " + req.getStyle() : "")
            + (StringUtils.hasLength(req.getAr()) ? " " + req.getAr() : "")
            + (StringUtils.hasLength(req.getQ()) ? " " + req.getQ() : "")
            + (StringUtils.hasLength(req.getStylize()) ? " " + req.getStylize() : "")
            + (StringUtils.hasLength(req.getChaos()) ? " " + req.getChaos() : ""));
        this.taskService.submitImagine(mjTask);
        return ApiResult.okBuild(mjTask);
    }

    @PostMapping(value = "/submit/uv", name = "提交选中放大或变换任务")
    public ApiResult<MjTask> submitUV(@RequestBody UVSubmitReq req) {
        if (null == req.getId()) {
            throw new BussinessException("id 不能为空");
        }
        if (!Arrays.asList(TaskAction.UPSCALE, TaskAction.VARIATION, TaskAction.REROLL).contains(req.getTaskAction())) {
            throw new BussinessException("action参数错误");
        }
        MjTask parentTask = taskStoreService.get(req.getId());
        if (null == parentTask) {
            throw new BussinessException(" 任务不存在");
        }
        if (parentTask.getStatus() != TaskStatus.SUCCESS) {
            throw new BussinessException("关联任务状态错误");
        }
        if (!Arrays.asList(TaskAction.IMAGINE, TaskAction.VARIATION).contains(parentTask.getAction())) {
            throw new BussinessException("关联任务不允许执行变化");
        }
        String description = "/up " + parentTask.getId();
        if (TaskAction.REROLL.equals(req.getTaskAction())) {
            description += " R";
        } else {
            description += " " + req.getTaskAction().name().charAt(0) + req.getIndex();
        }
        TaskCondition condition = new TaskCondition().setDescription(description);
        MjTask existMjTask = this.taskStoreService.findNotFailOne(condition);
        if (null != existMjTask) {
            throw new BussinessException(" 任务已存在 ");
        }
        MjTask mjTask = newTask();
        mjTask.setAction(req.getTaskAction());
        mjTask.setPrompt(parentTask.getPrompt());
        mjTask.setPromptEn(parentTask.getPromptEn());
        mjTask.setRelatedTaskId(parentTask.getId());
        mjTask.setDescription(description);
        mjTask.setIndex(req.getIndex());
        mjTask.setFinalPrompt(parentTask.getFinalPrompt());
        mjTask.setTopicalId(parentTask.getTopicalId());
        if (TaskAction.UPSCALE.equals(req.getTaskAction())) {
            this.taskService.submitUpscale(mjTask, parentTask.getMessageId(), parentTask.getMessageHash(), req.getIndex(), parentTask.getFlags());
        } else if (TaskAction.VARIATION.equals(req.getTaskAction())) {
            this.taskService.submitVariation(mjTask, parentTask.getMessageId(), parentTask.getMessageHash(), req.getIndex(), parentTask.getFlags());
        } else {
            throw new BussinessException("不支持的操作");
        }
        return ApiResult.okBuild(mjTask);
    }

    @PostMapping(value = "/describe", name = "提交Describe图生文任务")
    public ApiResult<MjTask> describe(@RequestBody DescribeReq req) {
        if (CharSequenceUtil.isBlank(req.getBase64())) {
            throw new BussinessException("校验错误");
        }
        IDataUrlSerializer serializer = new DataUrlSerializer();
        DataUrl dataUrl;
        try {
            dataUrl = serializer.unserialize(req.getBase64());
        } catch (MalformedURLException e) {
            throw new BussinessException("base64格式错误");
        }
        MjTask mjTask = newTask();
        mjTask.setAction(TaskAction.DESCRIBE);
        String taskFileName = mjTask.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
        mjTask.setDescription("/describe " + taskFileName);
        this.taskService.submitDescribe(mjTask, dataUrl);
        return ApiResult.okBuild(mjTask);
    }

    @PostMapping(value = "/blend", name = "提交Blend任务")
    public ApiResult<MjTask> blend(@RequestBody SubmitBlendReq req) {
        List<String> base64Array = req.getBase64Array();
        if (base64Array == null || base64Array.size() < 2 || base64Array.size() > 5) {
            throw new BussinessException("base64List参数错误");
        }
        IDataUrlSerializer serializer = new DataUrlSerializer();
        List<DataUrl> dataUrlList = new ArrayList<>();
        try {
            for (String base64 : base64Array) {
                DataUrl dataUrl = serializer.unserialize(base64);
                dataUrlList.add(dataUrl);
            }
        } catch (MalformedURLException e) {
            throw new BussinessException("base64格式错误");
        }
        MjTask mjTask = newTask();
        mjTask.setAction(TaskAction.BLEND);
        mjTask.setDescription("/blend " + mjTask.getId() + " " + dataUrlList.size());
        this.taskService.submitBlend(mjTask, dataUrlList, req.getDimensions());
        return ApiResult.okBuild(mjTask);
    }


    @PostMapping("getTask")
    public ApiResult<MjTask> getTask(@RequestBody TaskReq req) {
        return ApiResult.okBuild(taskStoreService.get(req.getId()));
    }

    //	@PostMapping("callBack")
    public void callBack(@RequestBody MjCallBack mjTask) throws Exception {
//		log.info("mj开始回调,回调内容：{}", mjTask);
//		if(mjTask.getStatus() == TaskStatus.SUCCESS){
//			MjTask targetTask = new MjTask();
//			String localImgUrl = FileUtil.base64ToImage((mjTask.getImageUrl()));
//			targetTask.setImageUrl(localImgUrl);
//			targetTask.setId(mjTask.getId());
//			targetTask.setStatus(TaskStatus.SUCCESS);
//			mjTask.setImageUrl(targetTask.getImageUrl());
//			asyncService.updateMjTask(targetTask);
//		}
//		SseEmitterServer.sendMessage(mjTask.getUserId(),mjTask);
    }

    private MjTask newTask() {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        MjTask mjTask = new MjTask();
        mjTask.setId(IDUtil.getNextId());
        mjTask.setSubmitTime(System.currentTimeMillis());
        mjTask.setNotifyHook(sysConfig.getApiUrl() + CommonConst.MJ_CALL_BACK_URL);
        mjTask.setUserId(JwtUtil.getUserId());
        mjTask.setSubType(1);
        return mjTask;
    }

}
