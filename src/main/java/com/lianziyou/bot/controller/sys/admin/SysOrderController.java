package com.lianziyou.bot.controller.sys.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.req.sys.OrderYiReturnReq;
import com.lianziyou.bot.model.req.sys.admin.OrderQueryReq;
import com.lianziyou.bot.model.res.sys.admin.OrderQueryRes;
import com.lianziyou.bot.service.sys.IOrderService;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/order")
public class SysOrderController {

    @Resource
    IOrderService orderService;

    public static void main(String[] args) {
        String channelId = "1111803813121232928";
        String botToken = "MTExMTgxOTM4MzM2NzU0MDc2Ng.GYIegJ.TlNb5Y2DJrcIJeetVKTsEy8Vf_7hGVTSPFTvPw";

        try {
            String imageUrl = getMidjourneyImageUrl(channelId, botToken);
            System.out.println("Midjourney Image URL: " + imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getMidjourneyImageUrl(String channelId, String botToken) throws IOException {
        // Construct the API URL
        String apiUrl = "https://discord.com/api/v10/channels/" + channelId + "/messages?limit=10";
        URL url = new URL(apiUrl);

        // Open connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bot " + botToken);
        conn.setRequestProperty("User-Agent", "DiscordBot (https://yourwebsite.com, 1.0)");

        // Read response
        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }

        // Parse JSON response to extract image URL
        String json = response.toString();
        // Parse the JSON and extract the image URL using a JSON parsing library like Jackson or Gson
        // For simplicity, let's assume the image URL is stored in the "url" field of the first message in the response JSON
        String imageUrl = "YOUR_IMAGE_URL";

        return imageUrl;
    }

    @RequestMapping(value = "/query", name = "查询订单列表", method = RequestMethod.POST)
    public ApiResult<Page<OrderQueryRes>> query(@Validated @RequestBody OrderQueryReq req) {
        return orderService.query(req);
    }

    @RequestMapping(value = "/yi/return/url", name = "易支付订单查询", method = RequestMethod.POST)
    public ApiResult<Void> yiReturnUrl(@Validated @RequestBody OrderYiReturnReq req) {
        return orderService.yiReturnUrl(req);
    }
}
