package com.lianziyou.bot.utils.mj;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BannedPromptUtils {

    private static final String BANNED_WORDS_FILE_PATH = "/home/spring/config/banned-words.txt";
    private final List<String> BANNED_WORDS;

    static {
        List<String> lines;
        File file = new File(BANNED_WORDS_FILE_PATH);
        if (file.exists()) {
            lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
        } else {
            URL resource = BannedPromptUtils.class.getResource("/banned-words.txt");
            lines = FileUtil.readLines(resource, StandardCharsets.UTF_8);
        }
        BANNED_WORDS = lines.stream().filter(CharSequenceUtil::isNotBlank).collect(Collectors.toList());
        ;
    }

    public static boolean isBanned(String promptEn) {
        String finalPromptEn = promptEn.toLowerCase(Locale.ENGLISH);
        return BANNED_WORDS.stream().anyMatch(bannedWord -> Pattern.compile("\\b" + bannedWord + "\\b").matcher(finalPromptEn).find());
    }
}
