package ru.nsu.fit.markelov.subtitles;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagFilter {

    private static final Pattern TAG_REGEX = Pattern.compile("[<{].+?[>}]", Pattern.DOTALL);

    public static String filter(String str, Set<String> permittedTagNames) {
        Matcher matcher = TAG_REGEX.matcher(str);

        while (matcher.find()) {
            String foundTag = matcher.group(0);
            String tag = foundTag
                .replaceFirst("\\{", "<")
                .replaceFirst("}", ">");

            String tagName = tag.substring(tag.charAt(1) == '/' ? 2 : 1, tag.length() - 1);
            if (permittedTagNames.contains(tagName)) {
                str = str.replaceFirst(Pattern.quote(foundTag), Matcher.quoteReplacement(tag));
            } else {
                str = str.replaceFirst(Pattern.quote(foundTag), "");
            }
        }

        return str;
    }
}
