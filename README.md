<div align="center">
    <p style="font-size:40px;font-weight: 800;color: coral">SuperAI 智能Ai机器人 </p>
</div>


## 👨‍🚀 Major Function

<h2>📌 客户端</h2>

* **登录**
* **临时用户**
* **注册（公众号注册，邮箱注册，账号密码注册）**
* **基于SSE GPT 3.5/4.0 流式对话+上下文**
* **GPT 画图**
* **Midjourney画图**
* **AI场景对话**
* **个人信息展示（剩余次数，身份，昵称）**
* **个人信息修改（头像，密码）**
* **产品查询购买**
* **订单查询**
* **支付方式 微信支付、卡密兑换**

<h2>📌 管理端</h2>

* **首页（数据统计）**
* **支付配置**
* **对KEY配置**
* **用户管理**
* **订单管理**
* **公告管理**
* **产品管理**
* **系统配置**

## 💻 INSTALL AND START

    一、系统依赖jdk1.11 其中redis、mysql 8.0、mongodb 需自行安装（建议修改mysql与redis端口，redis可能会被挖矿）
    二、shell运行安装步骤
        1.安装mysql，redis
        2.创建数据库名字为：intelligent_bot
        3.导入src/resources/下的intelligent_bot.sql 文件
        4.使用centos7系统（其他系统需自己修改shell脚本），将application-prod.yml配置改为自己实际配置 复制到/usr/local/SuperAI下
        5.修改yml中的数据库配置与redis配置
        6.在根目录下创建临时上传路径/www/temp/data 或自己根据自己实际的路径来配置，注意修改yml中第17行
        7.增加上传图片目录/gpt/file/，不可以重新配置
        8.进入/usr/local将脚本复制到根目录下
        9.使用sh start.sh运行安装脚本
        10.脚本将自动安装git,拉取代码，安装maven，jdk1.8，并配置环境变量
        11.自动maven打包，放到/usr/local/SuperAI下
        12.在/etc/systemd/system/下创建bot.service 并开机启动
        13.打包成功之后会运行systemctl restart bot 运行jar包
        14.使用journalctl -fu bot 命令可查看当前服务状态日志
        15.管理员账号admin密码123456，根据自己需求合理增加或修改表内数据，初始化sql只为正常启动代码
        16.相关配置请往下滑！

## 🕹 Precautions For Using Nginx

<p align="center">🪧  若使用nginx反向代理到后端需要增加SEE支持，与SEE长连接时间 </p>

```powershell
 server {
        listen 443 ssl http2;  # 1.1版本后这样写
        server_name baidu.com; #填写绑定证书的域名
        ssl_certificate /www/server/nginx/ssl/baidu.pem;# 指定证书的位置，绝对路径
        ssl_certificate_key /www/server/nginx/ssl/baidu.key; # 绝对路径，同上
        ssl_session_timeout 5m;
        ssl_protocols TLSv1 TLSv1.1 TLSv1.2; #按照这个协议配置
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:HIGH:!aNULL:!MD5:!RC4:!DHE;#按照这个套件配置
        ssl_prefer_server_ciphers on;
        location / {
          proxy_pass http://127.0.0.1:8080/;   #转发到tomcat
          proxy_set_header Host $http_host;  ##proxy_set_header用来重定义发往后端服务器的请求头
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto $scheme;
          proxy_buffering off;
          proxy_http_version  1.1;
          proxy_read_timeout 600s; ##设置SSE长链接保持时间为 600s
          }
    }
```

## ⌨️ And coding style tests

**<h3>🧧 专属H5 UI(开发中)</h3>**



## 💬 USE GPT

- 1.在gpt_key中配置对应的gpt key，注意区分3.5与4.0
-
2.若国内环境使用请使用代理访问，或使用cloudflare搭理，[教程地址](https://github.com/x-dr/chatgptProxyAPI)
- 3.gpt使用sse方式进行消息推送与前端交互，若使用nginx请查看上方nginx配置

## 🧩 USE Image Upload(图片上传)

- 1.创建指定的文件夹如：/usr/local/upload
- 2.创建成功后在"sys_config"表中"img_upload_url"配置第一步创建的目录记得最后边加上"/"
  如：/usr/local/upload/
- 3.使用nginx进行文件夹代理
- 4.nginx代理的域名或ip配置到sys_config中img_return_url如："https://www.baidu.com"
- 5.上传的图片会以每天的年月日来进行创建文件夹
- 6.图片名称分为两种，Midjourney的名字为任务id，其余的图片为当前时间戳
- 7.图片最终的地址为："img_return_url"+"img_upload_url"
  +文件名，如："https://www.baidu.com/20230618/123.jpg"

## 🎨 USE Stable-Diffusion

- 1.在"sd_model"表中配置模型（名字（全部内容包括后缀），图片）
- 2.若有lora在"sd_lora"表中配置lora（名字，图片）
- 3.配置"sys_config"表中"is_open_sd"为1，开启状态
- 4.配置"sys_config"表中"sd_url"的地址，本地默认地址为http://127.0.0.1:7860(记得打开api开关)

## 🎨 USE Midjourney

- 1.注册 MidJourney创建自己的频道、[参考地址](https://docs.midjourney.com/docs/quick-start)
- 2.添加成功之后查看浏览器中的地址如：<SMALL>https://discord.com/channels/123/456 </SMALL>
  其中123为mj_guild_id,456为mj_channel_id
- 3.获取mj_user_token，浏览器打开F12随便发送一个信息查看Network,Authorization为用户token
- 4.~~
  添加自己的机器人~~、[参考地址](https://github.com/a616567126/GPT-WEB-JAVA/wiki/MJ%E6%9C%BA%E5%99%A8%E4%BA%BA%E6%B7%BB%E5%8A%A0%E8%AF%B4%E6%98%8E)
- 5.如果使用机器人监听可参考步骤4

## 🪜 USE Proxy
<p align="center">GPT、Midjourney 国内网络环境下使用代理访问</p>
代理使用，配置流程、[参考地址](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E4%BD%BF%E7%94%A8%E4%BB%A3%E7%90%86%E8%AF%B7%E6%B1%82GPT%E3%80%81Midjourney)

## 📄 USE Baidu

<p align="center">GPT、Midjourney、Stable-Diffusion 使用文本审核，Midjourney、Stable-Diffusion，使用百度翻译</p>    

-
1.百度翻译申请，配置流程、[参考地址](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E7%94%B3%E8%AF%B7%E7%99%BE%E5%BA%A6%E7%BF%BB%E8%AF%91)
-
1.百度文本审核申请，配置流程、[参考地址](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E7%94%B3%E8%AF%B7%E7%99%BE%E5%BA%A6%E5%86%85%E5%AE%B9%E5%AE%A1%E6%A0%B8%E5%B9%B3%E5%8F%B0-%E6%96%87%E6%9C%AC)

## 🍾 Put It Last

- 默认启动时需配置如下三个表的数据（根据自己实际情况）
    - gpt_key
    - pay_config
    - sys_config
    - 项目启动时会加载对应参数到redis中，如果手动修改数据库，需要在redis中修改对应参数，防止不生效
- FlagStudio地址：http://flagstudio.baai.ac.cn/

**支付配置(pay_config)(暂时不用)**
字段|描述|注意
-|:-:|-:
使用微信公众号支付

**系统配置(sys_config)**
字段|描述|注意
-|:-:|-:
registration_method|注册模式 1 账号密码 2 邮箱注册 3 公众号 |开启邮件注册后需要在emil_config中配置邮件相关参数
default_times|默认注册次数|用户注册时默认赠送请求次数
gpt_url|gpt请求地址|可使用官方或替换第三方
img_upload_url|图片上传地址|例如：/usr/local 配置图片上传路径
img_return_url|图片域名前缀|上传图片后与图片名组合成可访问的url 例如：https://baidu.com 图片上传成功后
则返回 https://baidu.com /2023/04/26/2222.jpg
api_url|后台接口地址|用于mj、支付、微信等回调使用
client_url|客户端页面地址|用于支付跳转等
is_open_sd|是否开启sd 0未开启 1开启|无
sd_url|Sd接口地址|开启sd时需配置这个地址
is_open_flag_studio|是否开启FlagStudio 0-未开启 1开启|无
flag_studio_key|FlagStudio key|登录之后api获得每天500次请求
flag_studio_url|FlagStudio 接口地址|暂时写死https://flagopen.baai.ac.cn/flagStudio
baidu_appid|百度appid|用于百度翻译
baidu_secret|百度Secret|用于百度翻译
baidu_key|百度应用key|用于敏感词检查
baidu_secret_key|百度应用Secret|用于敏感词检查
is_open_mj|是否开启mj 0-未开启 1开启|无
mj_guild_id|Mj服务器id|url地址中获得
mj_channel_id|Mj频道id|url地址中获得
mj_user_token|mj用户token|F12查看network中的Authorization参数
is_open_proxy|是否开启代理 0关闭 1开启|无
proxy_ip|代理ip|无
proxy_port|代理端口|无
bing_cookie|微软bing cookie|无
is_open_bing|是否开启bing 0-未开启 1开启|无
is_open_stable_studio|是否开启StableStudio 0未开启 1 开启|无
stable_studio_api|StableStudioapi地址前缀|写死：https://api.stability.ai
stable_studio_key|StableStudio key|无


## 🥤 Reward（有能力的可以请作者喝一杯冰可落）

- 支付宝

<img src="https://raw.githubusercontent.com/ly18028750128/pic/main/%E6%94%AF%E4%BB%98%E5%AE%9D%E6%94%B6%E6%AC%BE%E7%A0%81.jpg" width="200" height="320">  

- 微信

<img src="https://raw.githubusercontent.com/ly18028750128/pic/main/%E5%BE%AE%E4%BF%A1%E6%94%B6%E6%AC%BE%E7%A0%81.jpg" width="200" height="320"> 

## ⭐ 记得点一个Star哦!!!!

## ✉ Scan code to add friends（扫码添加微信好友）
<img src="https://raw.githubusercontent.com/ly18028750128/pic/main/%E6%88%91%E7%9A%84%E5%BE%AE%E4%BF%A1%E4%BA%8C%E7%A0%81.jpg" alt="扫码添加微信好友" width="200" height="200">

## 🎯 Pay attention to the official account（关注公众号）

![关注公众号](https://raw.githubusercontent.com/ly18028750128/pic/main/%E6%89%AB%E7%A0%81_%E6%90%9C%E7%B4%A2%E8%81%94%E5%90%88%E4%BC%A0%E6%92%AD%E6%A0%B7%E5%BC%8F-%E6%A0%87%E5%87%86%E8%89%B2%E7%89%88.png)


Apache License 2.0
