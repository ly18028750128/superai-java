package com.lianziyou.bot.controller.wx;

import cn.hutool.core.lang.UUID;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.service.wx.WxService;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/wx", produces = "application/json; charset=UTF-8")
@Log4j2
public class WxApi {

    @Resource
    WxService wxService;

    @Resource
    private WxMpService wxMpService;


    @RequestMapping(value = "/callBack", method = RequestMethod.GET)
    public String token(String signature, String timestamp, String nonce, String echostr) {
        log.error("wxtoken校验");
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (signature != null && wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return null;
    }

    @RequestMapping(value = "/callBack", method = RequestMethod.POST)
    public String message(HttpServletRequest request) throws Exception {
        return wxService.callbackEvent(request);
    }

    /**
     * 上传永久素材
     *
     * @return
     * @throws WxErrorException
     */
    @RequestMapping(value = "uploadPermanent", method = RequestMethod.POST)
    public String uploadPermanent() throws WxErrorException {
        File file = new File("/Users/sim/Desktop/111.jpg");
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        wxMpMaterial.setFile(file);
        wxMpMaterial.setName("logo");
        WxMpMaterialUploadResult wxMpMaterialUploadResult = wxMpService.getMaterialService().materialFileUpload(WxConsts.MediaFileType.IMAGE, wxMpMaterial);
        RedisUtil.setCacheObject("MediaId", wxMpMaterialUploadResult.getMediaId());
        return "上传永久素材成功：mediaId:" + wxMpMaterialUploadResult.getMediaId();
    }

    /**
     * 上传临时素材
     *
     * @return
     * @throws WxErrorException
     */
    @RequestMapping(value = "uploadTemp", method = RequestMethod.POST)
    public String uploadTemp() throws WxErrorException {
        File file = new File("/Users/sim/Desktop/erweima.0df50348.jpg");
        WxMediaUploadResult wxMediaUploadResult = wxMpService.getMaterialService().mediaUpload(WxConsts.MediaFileType.IMAGE, file);
        return "上传临时素材成功：mediaId:" + wxMediaUploadResult.getMediaId();
    }

    @RequestMapping(value = "getTicket", method = RequestMethod.GET)
    public ApiResult<Map<String, String>> getTicket() throws WxErrorException {
        String qrUuid = UUID.randomUUID().toString();
        WxMpQrCodeTicket wxMpQrCodeTicket =
            this.wxMpService.getQrcodeService().qrCodeCreateTmpTicket(qrUuid, 10 * 60);
        String qrCodeUrl =
            this.wxMpService.getQrcodeService().qrCodePictureUrl(wxMpQrCodeTicket.getTicket());
        Map<String, String> result = new LinkedHashMap<>();
        result.put("ticket", wxMpQrCodeTicket.getTicket());
        result.put("qrCodeUrl", qrCodeUrl);
        return ApiResult.okBuild(result);
    }

}
