package com.lianziyou.bot.service.wx.impl;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result.JsapiResult;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.lianziyou.bot.config.WeixinPayConfig;
import com.lianziyou.bot.enums.wx.WeixinTradeType;
import com.lianziyou.bot.model.wx.WeixinPayParamDTO;
import com.lianziyou.bot.model.wx.WeixinPayResultDTO;
import com.lianziyou.bot.utils.sys.IPUtils;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * @author Administrator
 */
@Service
public class MyWxPayService {

    @Resource
    WxPayServiceImpl wxPayService;

    @Resource
    WxMpService wxMpService;

    public WeixinPayResultDTO pay(final String mchId, final WeixinPayParamDTO payParamDTO) {

        if (StringUtils.hasLength(mchId)) {
            wxPayService.switchover(mchId);
        }

        // 构建统一下单请求参数对象
        WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request();
        // 对象中写入数据
        wxPayUnifiedOrderV3Request
            // 【1】必填信息
            // 商品描述：必填
            .setDescription(payParamDTO.getDescription())
            // 商户订单号：必填，同一个商户号下唯一
            .setOutTradeNo(payParamDTO.getOutTradeNo())
            // 通知地址：必填，公网域名必须为https，外网可访问。可不填，通过配置信息读取（但这个组件没写...）
            .setNotifyUrl(wxPayService.getConfig().getNotifyUrl() + "/" + payParamDTO.getOutTradeNo())
            // 订单金额：单位（分）
            .setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(payParamDTO.getTotal().multiply(new BigDecimal(100)).intValue()))
            // 【2】选填信息
            // 附加信息
            .setAttach(payParamDTO.getDescription());

        try {
            // 根据请求类型，返回指定类型，其中包含：【3】条件必填信息
            switch (payParamDTO.getTradeType()) {
                // Native支付
                case NATIVE:

                    String codeUrl = wxPayService.unifiedOrderV3(TradeTypeEnum.NATIVE, wxPayUnifiedOrderV3Request).getCodeUrl();

                    wxMpService.getQrcodeService().qrCodePictureUrl(codeUrl);

                    return WeixinPayResultDTO.builder().qrcode(wxPayService.unifiedOrderV3(TradeTypeEnum.NATIVE, wxPayUnifiedOrderV3Request).getCodeUrl())
                        .build();
                // JSAPI支付
                case JSAPI:
                    // 用户在直连商户appid下的唯一标识。 下单前需获取到用户的Openid
                    wxPayUnifiedOrderV3Request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(payParamDTO.getOpenId()));
                    WxPayUnifiedOrderV3Result wxPayUnifiedOrderV3Result = wxPayService.unifiedOrderV3(TradeTypeEnum.JSAPI, wxPayUnifiedOrderV3Request);
                    WxPayConfig payConfig = wxPayService.getConfig();
                    JsapiResult jsapiResult = wxPayUnifiedOrderV3Result.getPayInfo(TradeTypeEnum.JSAPI, payConfig.getAppId(), payConfig.getMchId(),
                        payConfig.getPrivateKey());
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("jsapiResult", jsapiResult);
                    result.put("prepayId", wxPayUnifiedOrderV3Result.getPrepayId());
                    result.put("total", payParamDTO.getTotal());
                    return WeixinPayResultDTO.builder().qrcode(JSON.toJSONString(result)).payUrl(WeixinPayConfig.getPayPage()).build();
                // H5支付
                case H5:
                    // 用户终端IP
                    wxPayUnifiedOrderV3Request.setSceneInfo(new WxPayUnifiedOrderV3Request.SceneInfo().setPayerClientIp(IPUtils.getClientIpStr())
                        .setH5Info(new WxPayUnifiedOrderV3Request.H5Info().setType("wechat")));// 场景类型
                    return WeixinPayResultDTO.builder().qrcode(wxPayService.unifiedOrderV3(TradeTypeEnum.H5, wxPayUnifiedOrderV3Request).getH5Url()).build();
                // APP支付
                case APP:
                    return WeixinPayResultDTO.builder().qrcode(wxPayService.unifiedOrderV3(TradeTypeEnum.APP, wxPayUnifiedOrderV3Request).getPrepayId())
                        .build();
                default:
                    throw new RuntimeException("输入的[" + payParamDTO.getTradeType() + "]不合法，只能为native、jsapi、h5、app其一，请核实！");
            }
        } catch (WxPayException e) {
            throw new RuntimeException(e.getMessage());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }
}
