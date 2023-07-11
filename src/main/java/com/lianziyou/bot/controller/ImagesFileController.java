package com.lianziyou.bot.controller;

import com.lianziyou.bot.service.sys.impl.MjTaskImpl;
import com.lianziyou.bot.utils.sys.HttpUtils;
import com.unknow.first.mongo.utils.MongoDBUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@RestController
@RequestMapping(value = "/files")
@Api(description = "文件管理", tags = "文件管理")
@Slf4j
public class ImagesFileController {

    public final static long FILE_CACHE_TIME = 31_536_000;

    @Autowired
    MjTaskImpl mjTaskService;

    /**
     * 直接展示文件
     *
     * @param fileId
     * @param response
     * @throws Exception
     */
    @GetMapping(value = "/")
    @ApiOperation(value = "显示个人文件")
    public void showFile(@ApiParam("文件ID") @RequestParam("fileId") String fileId, HttpServletResponse response) throws Exception {
        if (fileId.startsWith("/")) {
            HttpUtils.flushFile(fileId, response, false);
        } else {
            MongoDBUtil.single().downloadOrShowFile(MongoDBUtil.single().getGridFSFileByObjectId(fileId), response, false);
        }
    }
}
