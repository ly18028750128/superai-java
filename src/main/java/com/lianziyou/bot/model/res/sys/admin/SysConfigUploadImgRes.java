package com.lianziyou.bot.model.res.sys.admin;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SysConfigUploadImgRes implements Serializable {

    private String filePatch;

    private String imageUrl;

}
