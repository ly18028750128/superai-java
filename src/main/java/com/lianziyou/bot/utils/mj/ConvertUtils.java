package com.lianziyou.bot.utils.mj;

import cn.hutool.core.text.CharSequenceUtil;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.model.mj.data.MessageData;
import com.lianziyou.bot.model.mj.data.UVData;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConvertUtils {

    private static final String MJ_I_CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - <@(\\d+)> \\((.*?)\\)";
    private static final String MJ_UV_CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - (.*?) by <@(\\d+)> \\((.*?)\\)";
    private static final String MJ_U_CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - Image #(\\d) <@(\\d+)>";

    public static String findTaskIdByFinalPrompt(String finalPrompt) {
        return CharSequenceUtil.subBetween(finalPrompt, "[", "]");
    }

    public static MessageData matchImagineContent(String content) {
        Pattern pattern = Pattern.compile(MJ_I_CONTENT_REGEX);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            return null;
        }
        MessageData data = new MessageData();
        data.setTaskAction(TaskAction.IMAGINE);
        data.setPrompt(matcher.group(1));
        data.setStatus(matcher.group(3));
        return data;
    }

    public static MessageData matchUVContent(String content) {
        Pattern pattern = Pattern.compile(MJ_UV_CONTENT_REGEX);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            return matchUContent(content);
        }
        MessageData data = new MessageData();
        data.setPrompt(matcher.group(1));
        String matchAction = matcher.group(2);
        data.setTaskAction(matchAction.startsWith("Variation") ? TaskAction.VARIATION : TaskAction.UPSCALE);
        data.setStatus(matcher.group(4));
        return data;
    }

    private static MessageData matchUContent(String content) {
        Pattern pattern = Pattern.compile(MJ_U_CONTENT_REGEX);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            return null;
        }
        MessageData data = new MessageData();
        data.setTaskAction(TaskAction.UPSCALE);
        data.setPrompt(matcher.group(1));
        data.setStatus("complete");
        data.setIndex(Integer.parseInt(matcher.group(2)));
        return data;
    }

    public static UVData convertUVData(String content) {
        List<String> split = CharSequenceUtil.split(content, " ");
        if (split.size() != 2) {
            return null;
        }
        String action = split.get(1).toLowerCase();
        if (action.length() != 2) {
            return null;
        }
        UVData upData = new UVData();
        if (action.charAt(0) == 'u') {
            upData.setTaskAction(TaskAction.UPSCALE);
        } else if (action.charAt(0) == 'v') {
            upData.setTaskAction(TaskAction.VARIATION);
        } else {
            return null;
        }
        try {
            int index = Integer.parseInt(action.substring(1, 2));
            if (index < 1 || index > 4) {
                return null;
            }
            upData.setIndex(index);
        } catch (NumberFormatException e) {
            return null;
        }
        upData.setId(split.get(0));
        return upData;
    }

}
