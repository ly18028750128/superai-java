package com.lianziyou.bot.utils.mj;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MimeTypeUtils {

    private final Map<String, List<String>> MIME_TYPE_MAP;

    static {
        MIME_TYPE_MAP = new HashMap<>();
        URL resource = MimeTypeUtils.class.getResource("/mime.types");
        List<String> lines = FileUtil.readLines(resource, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (CharSequenceUtil.isBlank(line)) {
                continue;
            }
            String[] arr = line.split(":");
            MIME_TYPE_MAP.put(arr[0], CharSequenceUtil.split(arr[1], ' '));
        }
    }

    public static String guessFileSuffix(String mimeType) {
        if (CharSequenceUtil.isBlank(mimeType)) {
            return null;
        }
        String key = mimeType;
        if (!MIME_TYPE_MAP.containsKey(key)) {
            key = MIME_TYPE_MAP.keySet().stream().filter(k -> CharSequenceUtil.startWithIgnoreCase(mimeType, k))
                .findFirst().orElse(null);
        }
        List<String> suffixList = MIME_TYPE_MAP.get(key);
        if (suffixList == null || suffixList.isEmpty()) {
            return null;
        }
        return suffixList.iterator().next();
    }

}
