package com.lianziyou.bot.utils;

import static com.lianziyou.bot.service.gpt.ChatCompletion.Model.GPT_3_5_TURBO;
import static com.lianziyou.bot.service.gpt.ChatCompletion.Model.GPT_3_5_TURBO_16K;

import com.lianziyou.bot.utils.sys.InitUtil;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class OpenAiUtil {

    private final static OpenAiUtil INSTANCE = new OpenAiUtil();


    private OpenAiUtil() {

    }

    public static OpenAiUtil single() {
        return INSTANCE;
    }

    public OpenAiService getOpenAiServiceV3() {
        return new OpenAiService(InitUtil.getRandomKey(3));
    }

    public OpenAiService getOpenAiServiceV4() {
        return new OpenAiService(InitUtil.getRandomKey(4));
    }

    public String getMjPrompt(String prompt) {
        OpenAiService service = OpenAiUtil.single().getOpenAiServiceV3();
        List<ChatMessage> list = new ArrayList<>();
        // 定义一个用户身份，content是用户写的内容
        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole(ChatMessageRole.SYSTEM.value());
        userMessage.setContent("你是一个prompt工程师，我输入一个prompt，你就给我输出一个新的prompt,发挥下想象力,输出的 prompt 可以比较好地运行在图片生成模型上。\n"
            + "举个例子：\n\n"
            + "输入: 一个三只耳朵的猫\n"
            + "输出: a cat with three ears.\n\n"
            + "输入: " + prompt + "\n"
            + "输出:");
        return getGptMessage(service, list, userMessage);
    }

    public String getPromptDescription(String prompt) {
        OpenAiService service = OpenAiUtil.single().getOpenAiServiceV3();
        List<ChatMessage> list = new ArrayList<>();
        // 定义一个用户身份，content是用户写的内容
        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole(ChatMessageRole.SYSTEM.value());
        userMessage.setContent("你是一个文章关键字的提取工程师,您要很好的总结文章中的关键字生成一个大约20字以内的描述\n"
            + "举个例子：\n\n"
            + "输入: 一个三只耳朵的猫在一条大街上跳舞\n"
            + "输出: 猫在大街上跳舞\n\n"
            + "输入: " + prompt + "\n"
            + "输出:");
        return getGptMessage(service, list, userMessage);
    }

    public String getPromptTag(String prompt) {
        OpenAiService service = OpenAiUtil.single().getOpenAiServiceV3();
        List<ChatMessage> list = new ArrayList<>();
        // 定义一个用户身份，content是用户写的内容
        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole(ChatMessageRole.SYSTEM.value());
        userMessage.setContent("你是一个文章Tag提取工程师,您要很好的总结文章中的关键字生成2个以内的Tag,用逗号分开\n"
            + "举个例子：\n\n"
            + "输入: 一个三只耳朵的猫在一条大街上跳舞\n"
            + "输出: 宠物,运动\n\n"
            + "输入: " + prompt + "\n"
            + "输出:");
        return getGptMessage(service, list, userMessage);
    }

    @NotNull
    private static String getGptMessage(OpenAiService service, List<ChatMessage> list, ChatMessage userMessage) {
        list.add(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(list)
            .model(GPT_3_5_TURBO_16K.getName())
            .build();

        // chatCompletion 对象就是chatGPT响应的数据了
        ChatCompletionResult chatCompletion = service.createChatCompletion(request);

        StringBuilder result = new StringBuilder();
        chatCompletion.getChoices().forEach(choice -> result.append(choice.getMessage().getContent()));
        return result.toString();
    }


}
