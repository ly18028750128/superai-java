package com.lianziyou.bot.controller.sys;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.lianziyou.bot.annotate.AvoidRepeatRequest;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.dao.MjTaskDao;
import com.lianziyou.bot.enums.sys.SendType;
import com.lianziyou.bot.model.Announcement;
import com.lianziyou.bot.model.MessageLog;
import com.lianziyou.bot.model.PayConfig;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.model.UserAiDrawTopical;
import com.lianziyou.bot.model.req.sys.ClientDeleteLog;
import com.lianziyou.bot.model.req.sys.ClientEmptyLog;
import com.lianziyou.bot.model.req.sys.ClientHomeReq;
import com.lianziyou.bot.model.req.sys.ClientUpdatePasswordReq;
import com.lianziyou.bot.model.req.sys.MessageLogSave;
import com.lianziyou.bot.model.res.sys.ClientConfRes;
import com.lianziyou.bot.model.res.sys.ClientHomeLogRes;
import com.lianziyou.bot.model.res.sys.ClientHomeRes;
import com.lianziyou.bot.model.res.sys.ClientRechargeRes;
import com.lianziyou.bot.model.res.sys.GetFunctionState;
import com.lianziyou.bot.model.res.sys.MjTaskRes;
import com.lianziyou.bot.service.draw.UserAiDrawTopicalService;
import com.lianziyou.bot.service.sys.IAnnouncementService;
import com.lianziyou.bot.service.sys.IMessageLogService;
import com.lianziyou.bot.service.sys.IMjTaskService;
import com.lianziyou.bot.service.sys.IOrderService;
import com.lianziyou.bot.service.sys.IProductService;
import com.lianziyou.bot.service.sys.IUserService;
import com.lianziyou.bot.utils.sys.ImgUtil;
import com.lianziyou.bot.utils.sys.JwtUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import com.lianziyou.bot.vo.CommonParam;
import com.lianziyou.bot.vo.DrawTopicalQueryVO;
import com.lianziyou.bot.vo.HomeQueryVO;
import com.lianziyou.bot.vo.HomeQueryVO.HomeQueryParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/client")
@Log4j2
@Transactional(rollbackFor = BussinessException.class)
@Api(tags = "APP:用户客户端功能", value = "APP:用户客户端功能")
public class ClientController {

    @Resource
    IUserService userService;
    @Resource
    IMessageLogService useLogService;
    @Resource
    IAnnouncementService announcementService;
    @Resource
    IProductService productService;
    @Resource
    IOrderService orderService;
    @Resource
    MjTaskDao mjTaskDao;
    @Resource
    IMjTaskService mjTaskService;

    @Resource
    UserAiDrawTopicalService aiDrawTopicalService;

    @PostMapping(value = "/home/user", name = "用户首页信息(仅含用户信息)")
    @ApiOperation("用户首页信息(仅含用户信息)")
    public ApiResult<ClientHomeRes> homeUser() {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        User user = userService.getById(JwtUtil.getUserId());
        if (StringUtils.hasLength(user.getAvatar()) && !user.getAvatar().contains("http")) {
            user.setAvatar(sysConfig.getImgReturnUrl() + user.getAvatar());
        }
        ClientHomeRes clientHomeRes = BeanUtil.copyProperties(user, ClientHomeRes.class);
        return ApiResult.okBuild(clientHomeRes);
    }

    @PostMapping(value = "/home", name = "用户首页信息")
    @ApiOperation("用户首页信息")
    public ApiResult<ClientHomeRes> home(@Validated @RequestBody ClientHomeReq req, HomeQueryVO pageParam) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        User user = userService.getById(JwtUtil.getUserId());
        if (StringUtils.hasLength(user.getAvatar()) && !user.getAvatar().contains("http")) {
            user.setAvatar(sysConfig.getImgReturnUrl() + user.getAvatar());
        }

        List<Announcement> list = announcementService.lambdaQuery().select(Announcement::getContent).orderByDesc(Announcement::getSort).last("limit 1").list();
        ClientHomeRes clientHomeRes = BeanUtil.copyProperties(user, ClientHomeRes.class);
        clientHomeRes.setAnnouncement((null != list && list.size() > 0) ? list.get(0).getContent() : "暂无通知公告");
        if (Objects.equals(req.getSendType(), SendType.MJ.getType())) {
            List<MjTaskRes> mjTaskList = getMjTaskRes(JwtUtil.getUserId(), pageParam, sysConfig);
            clientHomeRes.setMjTaskList(mjTaskList);
            List<ClientHomeLogRes> homeLogResList = getClientHomeLogRes(req.getSendType(), pageParam, sysConfig);
            clientHomeRes.setLogList(homeLogResList);
        } else {
            List<ClientHomeLogRes> homeLogResList = getClientHomeLogRes(req.getSendType(), pageParam, sysConfig);
            clientHomeRes.setLogList(homeLogResList);
        }
        return ApiResult.okBuild(clientHomeRes);
    }

    @GetMapping(value = "/home/draw/topical", name = "查询绘画主题列表")
    @ApiOperation("查询绘画主题列表")
    public ApiResult<List<UserAiDrawTopical>> queryUserDrawTopical(DrawTopicalQueryVO queryParam) {
        queryParam.getParam().setUserId(JwtUtil.getUserId());
        List<UserAiDrawTopical> resultList = aiDrawTopicalService.queryUserDrawTopical(queryParam);
        return ApiResult.okBuild(resultList);
    }

    @DeleteMapping(value = "/home/draw/topical/{id}", name = "删除绘画主题列表")
    @ApiOperation("删除绘画主题列表")
    public ApiResult<Boolean> delete(@ApiParam("主题ID") @PathVariable("id") Long id) throws Exception {
        if (aiDrawTopicalService.lambdaQuery().eq(UserAiDrawTopical::getUserId, JwtUtil.getUserId()).eq(UserAiDrawTopical::getId, id).count() < 1) {
            throw new BussinessException("主题不存在");
        }
        return ApiResult.okBuild(aiDrawTopicalService.removeUserAiDrawTopical(id));
    }

    @GetMapping(value = "/home/mj", name = "查询mj任务")
    @ApiOperation("查询mj任务")
    public ApiResult<List<MjTaskRes>> queryMj(HomeQueryVO pageParam) {
        return ApiResult.okBuild(getMjTaskRes(JwtUtil.getUserId(), pageParam, RedisUtil.getCacheObject(CommonConst.SYS_CONFIG)));
    }

    @GetMapping(value = "/home/chart/{chatType}", name = "查询对话任务")
    @ApiOperation("查询对话任务")
    public ApiResult<List<ClientHomeLogRes>> queryChat(@ApiParam("对话类型，1：GPT3.5 2：GPT3和4") @PathVariable("chatType") Integer chatType,
        HomeQueryVO pageParam) {
        return ApiResult.okBuild(getClientHomeLogRes(chatType, pageParam, RedisUtil.getCacheObject(CommonConst.SYS_CONFIG)));
    }

    @NotNull
    private List<ClientHomeLogRes> getClientHomeLogRes(Integer sendType, CommonParam<HomeQueryParam> pageParam, SysConfig sysConfig) {
        List<MessageLog> logList;
        final String lastStr = String.format("limit %d,%d", pageParam.getStart(), pageParam.getLimit());

        LambdaQueryChainWrapper<MessageLog> logListQuery = useLogService.lambdaQuery()
            .select(MessageLog::getUseValue, MessageLog::getId, MessageLog::getSendType, MessageLog::getName, MessageLog::getTagName)
            .eq(MessageLog::getUserId, JwtUtil.getUserId()).orderByDesc(MessageLog::getId);

        if (ObjectUtil.isNotNull(pageParam.getParam())) {
            if (ObjectUtil.isNotNull(pageParam.getParam().getId())) {
                logListQuery.eq(MessageLog::getId, pageParam.getParam().getId());
            }
        }

        if (!Objects.equals(sendType, SendType.GPT.getType())) {
            logList = logListQuery.eq(MessageLog::getSendType, sendType).last(lastStr).list();
        } else {
            logList = logListQuery.in(MessageLog::getSendType, SendType.GPT.getType(), SendType.GPT_4.getType()).last(lastStr).list();
        }
        List<ClientHomeLogRes> homeLogResList = new ArrayList<>();
        logList.forEach(e -> {
            ClientHomeLogRes res = new ClientHomeLogRes();
            res.setId(e.getId());
            res.setSendType(e.getSendType());
            if (e.getSendType().equals(SendType.GPT.getType()) || e.getSendType().equals(SendType.GPT_4.getType()) || e.getSendType()
                .equals(SendType.BING.getType())) {

                if (StringUtils.hasLength(e.getTagName())) {
                    res.setTitle(String.format("[%s]%s", e.getTagName(), e.getName()));
                } else {
                    res.setTitle(String.format("%s", e.getName()));
                }

                res.setContent(e.getUseValue());
            } else {
                MessageLogSave messageLogSave = JSONObject.parseObject(e.getUseValue(), MessageLogSave.class);
                if (CollectionUtil.isNotEmpty(messageLogSave.getImgList())) {
                    messageLogSave.setImgList(
                        messageLogSave.getImgList().stream().map(item -> (sysConfig.getImgReturnUrl() + item)).collect(Collectors.toList()));
                } else {
                    messageLogSave.setImgList(new ArrayList<>());
                }
                res.setTitle(messageLogSave.getPrompt());
                res.setContent(JSONObject.toJSONString(messageLogSave));
            }

            homeLogResList.add(res);
        });
        return homeLogResList;
    }

    @NotNull
    private List<MjTaskRes> getMjTaskRes(Long userId, CommonParam<HomeQueryParam> pageParam, SysConfig sysConfig) {
        List<MjTaskRes> mjTaskList = mjTaskDao.selectUserMjTask(userId, pageParam);
        for (MjTaskRes mjTaskRes : mjTaskList) {
            if (StringUtils.hasLength(mjTaskRes.getImageUrl())) {
                mjTaskRes.setImageUrl(sysConfig.getImgReturnUrl() + "?fileId=" + URLUtil.encode(mjTaskRes.getImageUrl()));
            }
            if (ObjectUtil.isNull(mjTaskRes.getIndex())) {
                mjTaskRes.setTaskTransformList(mjTaskDao.selectTransform(mjTaskRes.getId()));
            }
        }
        return mjTaskList;
    }

    @PostMapping(value = "/updateAvatar", name = "修改头像")
    @AvoidRepeatRequest(intervalTime = 2626560, msg = "头像每个月可更换一次")
    @ApiOperation("修改头像")
    public ApiResult<Void> updateAvatar(MultipartFile file) throws IOException {
        User user = new User();
        String oldFileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf("."));
        String fileName = ImgUtil.uploadMultipartFile(file, IdUtil.fastSimpleUUID());
        user.setAvatar(fileName);
        user.setId(JwtUtil.getUserId());
        userService.updateById(user);
        return ApiResult.okBuild();
    }

    @PostMapping(value = "/updatePassword", name = "修改密码")
    @AvoidRepeatRequest(intervalTime = 2626560, msg = "密码每个月可更换一次")
    @ApiOperation("修改密码")
    public ApiResult<String> updatePassword(@Validated @RequestBody ClientUpdatePasswordReq req) {
        User user = new User();
        user.setId(JwtUtil.getUserId());
        user.setPassword(SecureUtil.md5(req.getPassword()));
        userService.updateById(user);
        RedisUtil.deleteObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + user.getId());
        return ApiResult.okBuild("修改成功请重新登陆");
    }

    @PostMapping(value = "/recharge", name = "充值")
    @ApiOperation("充值")
    public ApiResult<ClientRechargeRes> recharge() {
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        return ApiResult.okBuild(
            ClientRechargeRes.builder().productList(productService.getProductList()).orderList(orderService.userOrderList(JwtUtil.getUserId()))
                .payType(payConfig.getPayType()).build());
    }

    @RequestMapping(value = "/register/method", name = "查询注册方式", method = RequestMethod.POST)
    @ApiOperation("查询注册方式")
    public ApiResult<Integer> getRegisterMethod() {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        return ApiResult.okBuild(sysConfig.getRegistrationMethod());
    }

    @RequestMapping(value = "/getFunctionState", name = "获取配置开启状态", method = RequestMethod.POST)
    @ApiOperation("获取配置开启状态")
    public ApiResult<GetFunctionState> getOpenSdState() {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);

        GetFunctionState getFunctionState = GetFunctionState.builder().isOpenSd(sysConfig.getIsOpenSd()).isOpenFlagStudio(sysConfig.getIsOpenFlagStudio())
            .isOpenBing(sysConfig.getIsOpenBing()).isOpenMj(sysConfig.getIsOpenMj()).build();

        return ApiResult.okBuild(getFunctionState);
    }

    @PostMapping(value = "/delete/log", name = "删除对话")
    @ApiOperation("删除对话")
    public ApiResult<Void> deleteLog(@Validated @RequestBody ClientDeleteLog req) {
        useLogService.removeById(req.getId());
        return ApiResult.okBuild();
    }

    @PostMapping(value = "/empty/log", name = "清空对话")
    @ApiOperation("清空对话")
    public ApiResult<Void> emptyLog(@Validated @RequestBody ClientEmptyLog req) {
        useLogService.lambdaUpdate().set(MessageLog::getDeleted, 1).eq(MessageLog::getUserId, JwtUtil.getUserId())
            .eq(MessageLog::getSendType, req.getSendType()).remove();
        return ApiResult.okBuild();
    }

    @PostMapping(value = "/delete/mj/task", name = "删除mj任务")
    @ApiOperation("删除mj任务")
    public ApiResult<Void> deleteMjTask(@Validated @RequestBody ClientDeleteLog req) {
        mjTaskService.deleteMjTask(req.getId());
        return ApiResult.okBuild();
    }

    @PostMapping(value = "/empty/mj/task", name = "清空mj任务")
    @ApiOperation("删除mj任务")
    public ApiResult<Void> emptyMjTask() {
        mjTaskService.emptyMjTask(JwtUtil.getUserId());
        return ApiResult.okBuild();
    }

    @PostMapping(value = "/upload/img", name = "上传图片")
    @AvoidRepeatRequest(intervalTime = 10, msg = "请勿频繁上传图片")
    public ApiResult<String> uploadImg(MultipartFile file) throws IOException {
        String oldFileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf("."));
        String fileName = ImgUtil.uploadMultipartFile(file, IdUtil.fastSimpleUUID());
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        return ApiResult.okBuild(cacheObject.getImgReturnUrl() + "?fileId=" + URLEncoder.encode(fileName));
    }

    @PostMapping(value = "/client/conf", name = "获取客户端配置，logo，名称")
    public ApiResult<ClientConfRes> clientConf() {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        ClientConfRes clientConfRes = BeanUtil.copyProperties(cacheObject, ClientConfRes.class);
        clientConfRes.setClientLogo(cacheObject.getImgReturnUrl() + clientConfRes.getClientLogo());
        return ApiResult.okBuild(clientConfRes);
    }
}
