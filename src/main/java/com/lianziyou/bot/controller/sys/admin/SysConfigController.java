package com.lianziyou.bot.controller.sys.admin;

import cn.hutool.core.util.IdUtil;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.req.sys.admin.SysConfigUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.SysConfigQueryRes;
import com.lianziyou.bot.model.res.sys.admin.SysConfigUploadImgRes;
import com.lianziyou.bot.service.sys.ISysConfigService;
import com.lianziyou.bot.utils.sys.ImgUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/sys/config")
@RequiredArgsConstructor
public class SysConfigController {


    @Resource
    ISysConfigService sysConfigService;


    @RequestMapping(value = "/queryPage", name = "查询系统配置", method = RequestMethod.POST)
    public ApiResult<SysConfigQueryRes> querySysConfig() {
        return sysConfigService.queryPage();
    }


    @RequestMapping(value = "/update", name = "更新系统配置", method = RequestMethod.POST)
    public ApiResult<Void> update(@Validated @RequestBody SysConfigUpdateReq req) {
        return sysConfigService.savaOrUpdate(req);
    }

    @RequestMapping(value = "/upload/img", name = "上传图片")
    public ApiResult<SysConfigUploadImgRes> uploadImg(MultipartFile file) throws IOException {
        String oldFileName = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf("."));
        String fileName = ImgUtil.uploadMultipartFile(file, IdUtil.fastSimpleUUID());
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        SysConfigUploadImgRes res = SysConfigUploadImgRes.builder().imageUrl(cacheObject.getImgReturnUrl() + fileName).filePatch(fileName).build();
        return ApiResult.okBuild(res);
    }
}
