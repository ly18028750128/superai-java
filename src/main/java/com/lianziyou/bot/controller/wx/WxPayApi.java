package com.lianziyou.bot.controller.wx;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.WeixinPayConst;
import com.lianziyou.bot.model.wx.WeixinPayParamDTO;
import com.lianziyou.bot.model.wx.WeixinPayResultDTO;
import com.lianziyou.bot.service.sys.IOrderService;
import com.lianziyou.bot.service.wx.impl.MyWxPayService;
import com.lianziyou.bot.utils.sys.IDUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Administrator
 */
@RestController
@Controller
@RequestMapping(value = "/wx/pay", produces = "application/json; charset=UTF-8")
@Log4j2
@Api(value = "微信支付", tags = "微信支付")
public class WxPayApi {

    @Resource
    WxPayServiceImpl wxPayService;

    @Resource
    MyWxPayService myWxPayService;

    @Resource
    IOrderService orderService;

    @GetMapping(value = "/page", produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation("微信支付页")
    public String page(@RequestParam("payData") String payData) {
        return WeixinPayConst.PAY_HTML.replace("$$params$$", payData);
    }

    @PostMapping(value = "/callBack/{mchId}/{orderId}")
    @ApiOperation("微信支付回调")
    public Object callBack(@PathVariable("mchId") String mchId, @PathVariable("orderId") String orderId, @RequestBody String notifyData,
        HttpServletRequest request) throws Exception {
        SignatureHeader header = new SignatureHeader();
        header.setNonce(request.getHeader("Wechatpay-Nonce"));
        header.setTimeStamp(request.getHeader("Wechatpay-Timestamp"));
        header.setSerial(request.getHeader("Wechatpay-Serial"));
        header.setSignature(request.getHeader("Wechatpay-Signature"));
        WxPayOrderNotifyV3Result orderNotifyV3Result = wxPayService.parseOrderNotifyV3Result(notifyData, header);
        return orderService.wxCallback(orderNotifyV3Result.getResult());
    }

    @PostMapping(value = "/pay")
    @ApiOperation("微信支付订单")
    public ApiResult<WeixinPayResultDTO> pay(@RequestParam(value = "mchId", required = false) String mchId, @RequestBody WeixinPayParamDTO payParamDTO)
        throws Exception {
        return ApiResult.okBuild(myWxPayService.pay(mchId, payParamDTO));
    }

    @GetMapping(value = "/getOrderId")
    @ApiOperation("生成支付订单ID")
    public ApiResult<Long> getOrderId() throws Exception {
        return ApiResult.okBuild(IDUtil.getNextId());
    }


}
