package com.lianziyou.bot.model.wx;

import java.io.Serializable;
import lombok.Data;

/**
 * 微信支付配置信息
 *
 * @author Administrator
 */
@Data
public class WeChatPayEntity implements Serializable {

    /**
     * 必填：微信支付商户号
     */
    private String mchId;

    /**
     * 必填：商户绑定的微信公众号、小程序、开放平台等的appid
     */
    private String appId;

    /**
     * 必填：APIv2密钥（调用v2版本的API时，需用APIv2密钥生成签名）
     */
    private String mchKey;

    /**
     * 必填：APIv3密钥（调用APIv3的下载平台证书接口、处理回调通知中报文时，要通过该密钥来解密信息）
     */
    private String apiV3Key;

    /**
     * 必填：apiclient_cert.p12证书文件的绝对路径，或者以classpath:开头的类路径。
     */
    private String keyPath;

    /**
     * 必填：apiclient_key.pem证书文件的绝对路径，或者以classpath:开头的类路径。
     */
    private String privateKeyPath;

    /**
     * 必填：apiclient_cert.pem证书文件的绝对路径，或者以classpath:开头的类路径。
     */
    private String privateCertPath;

    /**
     * 必填：微信支付异步回调通知地址。通知url必须以https开头（SSL协议），外网可访问，不能携带参数。
     */
    private String notifyUrl;
}
