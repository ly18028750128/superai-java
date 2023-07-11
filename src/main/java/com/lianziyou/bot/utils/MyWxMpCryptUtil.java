/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 *
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 * <p>
 * 针对org.apache.commons.codec.binary.Base64， 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 * <p>
 * 针对org.apache.commons.codec.binary.Base64， 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 */

// ------------------------------------------------------------------------

/**
 * 针对org.apache.commons.codec.binary.Base64，
 * 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 */
package com.lianziyou.bot.utils;

import java.util.Base64;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import org.apache.commons.lang3.StringUtils;

public class MyWxMpCryptUtil extends me.chanjar.weixin.common.util.crypto.WxCryptUtil {

    /**
     * 构造函数
     *
     * @param wxMpConfigStorage
     */
    public MyWxMpCryptUtil(WxMpConfigStorage wxMpConfigStorage) {
        /*
         * @param token          公众平台上，开发者设置的token
         * @param encodingAesKey 公众平台上，开发者设置的EncodingAESKey
         * @param appId          公众平台appid
         */
        String encodingAesKey = wxMpConfigStorage.getAesKey();
        String token = wxMpConfigStorage.getToken();
        String appId = wxMpConfigStorage.getAppId();

        this.token = token;
        this.appidOrCorpid = appId;
        this.aesKey = Base64.getDecoder().decode(StringUtils.remove(encodingAesKey, " "));
    }

    @Override
    public String decryptContent(String msgSignature, String timeStamp, String nonce, String encryptedContent) {
        // 验证安全签名
        String signature = SHA1.gen(this.token, timeStamp, nonce);
        if (!signature.equals(msgSignature)) {
            throw new WxRuntimeException("加密消息签名校验失败");
        }
        // 解密
        return decrypt(encryptedContent);
    }

}
