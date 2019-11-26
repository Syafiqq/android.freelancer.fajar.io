package io.localhost.freelancer.statushukum.model.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {
    public static Pattern nl2brMatcher = Pattern.compile("([^>\\r\\n]?)(\\r\\n|\\n\\r|\\r|\\n)");
    public static String sanitizeHtml(String html) {
        if(TextUtils.isEmpty(html))
            return "-";
        if(html.equalsIgnoreCase("null"))
            return "-";
        return nl2br(html);
    }

    public static String nl2br(String html) {
        return html.replaceAll("([^>\\r\\n]?)(\\r\\n|\\n\\r|\\r|\\n)", "$1<br>$2");
    }
}
