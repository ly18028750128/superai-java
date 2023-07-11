package com.lianziyou.bot.service.wx.impl;

import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.constant.WXMessageEnum;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.WxLog;
import com.lianziyou.bot.service.sys.IWxLogService;
import com.lianziyou.bot.service.wx.WXMpUtilService;
import com.lianziyou.bot.service.wx.WxService;
import com.lianziyou.bot.utils.MyWxMpCryptUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("WxService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class WxServiceImpl implements WxService {

    @Resource
    WXMpUtilService wxMpUtilService;

    @Resource
    IWxLogService wxLogService;
    @Resource
    private WxMpService wxMpService;

    @Override
    public String callbackEvent(HttpServletRequest request) throws Exception {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if (sysConfig == null) {
            return "";
        }
        String url = sysConfig.getClientUrl();
        // 默认返回的文本消息内容
        String respContent = WXMessageEnum.WELCOME.getContent() + "\n" + WXMessageEnum.MENU.getContent();
        WxMpXmlMessage message = getWxMpXmlMessage(request);
        if (message == null) {
            return "消息不合法";
        }
        // 发送方帐号
        String fromUser = message.getFromUser();
        // 开发者微信号
        String toUser = message.getToUser();
        // 消息类型
        String msgType = message.getMsgType();
        // 回复文本消息
        if (msgType.equals(CommonConst.REQ_MESSAGE_TYPE_EVENT)) {
            // 事件类型
            log.info("事件名称【{}】", message.getEvent());
            if ("unsubscribe".equals(message.getEvent())) {
                wxMpUtilService.unsubscribe(message);
                return "";
            } else if ("subscribe".equals(message.getEvent())) {
                wxMpUtilService.subscribe(message);
            } else if ("view".equals(message.getEvent())) {
                log.info("访问链接为：{}", message.getEventKey());
            } else if ("SCAN".equalsIgnoreCase(message.getEvent())) {
                log.info("扫码：{}", message.getTicket());
            }
            if (StringUtils.hasLength(message.getTicket())) {
                respContent = wxMpUtilService.ticket(message);
            }
        } else if (!msgType.equals(CommonConst.REQ_MESSAGE_TYPE_TEXT)) {
            respContent = "暂不支持该消息类型";
            log.info("其他消息");
        } else {
            log.info("文本消息");
            String content = message.getContent();
            saveWxLog(fromUser, content);
            if (content.contains("绑定-")) {
                respContent = wxMpUtilService.bind(message);
            } else if (content.equals("查询")) {
                respContent = wxMpUtilService.query(message);
            } else if (content.equals("公司介绍")) {
                respContent = "链自由科技是一家位于广东省深圳市的科技公司，成立于2020年6月31日。我们提供区块链DAPP、行业软件定制、多租户商城、电子报价系统、自研开发平台、ERP系统等多种产品和服务，并且可以根据客户的具体需求开发各种定制化的软件和系统，以满足客户的特殊业务需求。我们的核心价值观是专业、专心、专注，不断努力为客户提供更好的体验和服务。我们承诺，将提供优质的服务，并提供最高性价比的解决方案。此外，我们非常重视客户反馈，始终保证24小时响应客户的问题与需求。自成立以来，我们取得了多项重要里程碑和成就。在2020年7月，我们成功研发了ERP系统并申请获得软件著作权。在2021年6月，我们成功上线了多租户商城。在2022年4月，我们成功上线了电子产品多方询价报价系统，进一步丰富了我们的产品线。此外，我们还有序地开发和交付各种区块链DAPP。我们的团队由来自不同领域的专业人才组成，拥有丰富的经验和知识，在区块链、软件开发、UI/UX设计等领域具备深入的了解和实战经验。我们重视客户的反馈和需求，并会根据客户的反馈持续改进我们的产品和服务，以更好地满足客户的需求。感谢您对链自由科技的关注，我们期待与您合作，为您提供最优质的服务和定制化的工具，共同推动科技进步和商业发展。";
            } else if (content.equals("菜单")) {
                respContent = WXMessageEnum.MENU.getContent();
            } else if (content.equals("联系作者")) {
                WxMpXmlOutMessage texts = WxMpXmlOutTextMessage.IMAGE().toUser(fromUser).fromUser(toUser).mediaId(contractMeMediaId()).build();
                return texts.toXml();
            } else if (content.contains("修改密码")) {
                respContent = wxMpUtilService.updatePass(message);
            } else if (content.equals("重置密码")) {
                respContent = wxMpUtilService.restPass(message);
            } else if (content.contains("用户名-")) {
                respContent = wxMpUtilService.modifyUserName(message);
            }
        }
        WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage.TEXT().toUser(fromUser).fromUser(toUser).content(respContent).build();
        return texts.toXml();
    }

    private void saveWxLog(String fromUser, String content) {
        WxLog wxLog = new WxLog();
        wxLog.setContent(content);
        wxLog.setFromUserName(fromUser);
        wxLog.setCreateTime(LocalDateTime.now());
        wxLogService.save(wxLog);
    }

    private WxMpXmlMessage getWxMpXmlMessage(HttpServletRequest request) throws IOException {
        try {

            log.info("encrypt_type = {}", request.getParameter("encrypt_type"));

            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String signature = request.getParameter("signature");
            // 调用parseXml方法解析请求消息
            String message = new MyWxMpCryptUtil(wxMpService.getWxMpConfigStorage()).decryptXml(signature, timestamp, nonce,
                IOUtils.toString(request.getInputStream()));
            return WxMpXmlMessage.fromXml(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }

    }

    private String contractMeMediaId() {

        String contractMeMediaId = RedisUtil.getCacheObject("ContractMeMediaId");
        if (StringUtils.hasLength(contractMeMediaId)) {
            return contractMeMediaId;
        }

        File file = new File("/gpt/file/640.png");
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        wxMpMaterial.setFile(file);
        wxMpMaterial.setName("ContractMe");
        WxMpMaterialUploadResult wxMpMaterialUploadResult = null;
        try {
            wxMpMaterialUploadResult = wxMpService.getMaterialService().materialFileUpload(WxConsts.MediaFileType.IMAGE, wxMpMaterial);
        } catch (WxErrorException e) {
            log.error("上传永久素材失败[{}]", e.getMessage());
            return null;
        }
        contractMeMediaId = wxMpMaterialUploadResult.getMediaId();
        RedisUtil.setCacheObject("ContractMeMediaId", contractMeMediaId);
        return contractMeMediaId;


    }

}
