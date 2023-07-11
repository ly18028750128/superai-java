package com.lianziyou.bot.utils.sys;


import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.model.SysConfig;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

@Log4j2
public class HttpUtils {

    private static final int TIMEOUT = 5000; // 设置超时时间为5秒

    public static JSONObject readData(HttpServletRequest request) {
        JSONObject result = null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader();) {
            char[] buff = new char[1024];
            int len;
            while ((len = reader.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
            result = JSONUtil.parseObj(sb.toString());
        } catch (IOException e) {
        }
        return result;
    }

    public static InputStream sendGetRequest(String url) throws IOException {
        URL obj = new URL(url);

        HttpURLConnection con = null;

        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if (cacheObject.getIsOpenProxy() == 1) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(cacheObject.getProxyIp(), cacheObject.getProxyPort())); // 设置代理
            con = (HttpURLConnection) obj.openConnection(proxy);
        } else {
            con = (HttpURLConnection) obj.openConnection();
        }
        con.setRequestMethod("GET");
        con.setConnectTimeout(TIMEOUT);
        con.setReadTimeout(TIMEOUT);
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return con.getInputStream();
        } else {
            throw new IOException(con.getResponseCode() + "::" + con.getResponseMessage());
        }


    }

    public static byte[] getRequestBytes(String url) throws IOException {
        InputStream in = sendGetRequest(url);
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用输入流从buffer里把数据读取出来
            Assert.notNull(in, "输入流为空！");
            while ((len = in.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
        } finally {
            IoUtil.close(in);
            IoUtil.close(outStream);
        }

        return outStream.toByteArray();
    }

    public static void flushFile(String filePath, ServletResponse response, Boolean isDownLoad) throws Exception {

//        if (mjTaskService.lambdaQuery().eq(MjTask::getImageUrl, filePath).eq(MjTask::getUserId, JwtUtil.getUserId()).count() < 1) {
//            log.error("请不要盗用别人的图片！");
//            return;
//        }
        FileUtil.getPath(filePath);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Path path = FileUtil.getPath(filePath);
            inputStream = Files.newInputStream(path);
            outputStream = response.getOutputStream();
            response.setContentLength(inputStream.available());
            response.setContentType(Files.probeContentType(path));
            if (response instanceof HttpServletResponse) {
                HttpServletResponse ssResponse = ((HttpServletResponse) response);
                if (isDownLoad) {
                    ssResponse.setHeader("Content-disposition", "attachment;filename=" + path.getFileName().toString());
                }
                setCacheTime(ssResponse);
            }
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(outputStream);
        }
    }

    public static void setCacheTime(HttpServletResponse response) {
        // 设置缓存过期时间为1天（单位为秒）
        long cacheExpiration = TimeUnit.DAYS.toSeconds(1) * 365;
        response.setHeader("Cache-Control", "public, max-age=" + cacheExpiration);
        // 设置最后修改时间为当前时间
        long currentTime = System.currentTimeMillis();
        response.setDateHeader("Last-Modified", currentTime);
        // 设置响应的过期时间为1天后
        long expirationTime = currentTime + cacheExpiration * 1000;
        response.setDateHeader("Expires", expirationTime);
    }
}
