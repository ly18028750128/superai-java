package com.lianziyou.bot.model.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeixinPayResultDTO {

    private Boolean isSuccess = true;
    private String qrcode;
    private String message;

    private String payUrl;
}
