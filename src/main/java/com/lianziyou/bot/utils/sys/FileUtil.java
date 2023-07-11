package com.lianziyou.bot.utils.sys;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.model.SysConfig;
import com.unknow.first.mongo.utils.MongoDBUtil;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

@Log4j2
public class FileUtil {

    public static String base64ToImage(String base64, String fileName) throws IOException {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        // JDK8以上
        // Base64解码
        byte[] bytes = Base64.decode(base64);

        if (bytes == null) {
            log.info("未获取到[{}],请稍等!");
            return null;
        }

        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        String imgUploadUrl = cacheObject.getImgUploadUrl();
        String dayFilePatch = DateUtil.format(new Date(), "yyyyMMdd");
        File destFile = new File(imgUploadUrl + dayFilePatch);
        if (!destFile.exists()) {
            //不存在就创建
            destFile.mkdir();
        }
        if (StringUtils.isEmpty(fileName)) {
            fileName = String.valueOf(IDUtil.getNextId());
        }
        String newFileName = "/" + fileName + ".jpg";
        // 生成jpeg图片
        OutputStream out = null;
        try {
            out = Files.newOutputStream(Paths.get(imgUploadUrl + dayFilePatch + newFileName));
            out.write(bytes);
            out.flush();
        } finally {
            IoUtil.close(out);
        }

        return "/" + dayFilePatch + newFileName;
    }

    public static String base64ToImage(String base64) throws IOException {
        return base64ToImage(base64, null);
    }

    /**
     * 图片URL转Base64编码
     *
     * @param imgUrl 图片URL
     * @return Base64编码
     */
    public static String imageUrlToBase64(String imgUrl) {
        try {
            if (!StringUtils.hasText(imgUrl)) {
                return null;
            }
            log.info("imgUrl = {}", imgUrl);
            if (imgUrl.startsWith("http")) {
                return encode(HttpUtils.getRequestBytes(imgUrl));
            } else if (!imgUrl.startsWith("/")) {
                return encode(IoUtil.readBytes(MongoDBUtil.single().getInputStreamByObjectId(imgUrl)));
            } else {
                Path path = getPath(imgUrl);
                return encode(Files.readAllBytes(path));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static Path getPath(String imgUrl) {
        return Paths.get(getFilePath(imgUrl));
    }

    public static String getFilePath(String imgUrl) {
        if (!new File(imgUrl).exists()) {
            SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
            return sysConfig.getImgUploadUrl() + imgUrl;
        }
        return imgUrl;
    }

    /**
     * 图片转字符串
     *
     * @param image 图片Buffer
     * @return Base64编码
     */
    public static String encode(byte[] image) {
        return replaceEnter(Base64.encode(image));
    }

    /**
     * 字符替换
     *
     * @param str 字符串
     * @return 替换后的字符串
     */
    public static String replaceEnter(String str) {
        String reg = "[\n-\r]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    public static List<String> readFiles(String folderPath) {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) { // 确定文件夹存在且是文件夹
            File[] files = folder.listFiles(); // 列出文件夹下全部文件和文件夹
            assert files != null;
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                } else {
                    // 如果是文件夹，递归读取其中的文件
                    readFiles(file.getAbsolutePath());
                }
            }
        } else {
            log.info("文件夹不存在或不是一个文件夹！");
        }
        return fileNames;
    }

}
