package io.localhost.freelancer.statushukum.model.util.helper;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
    public static String trimOrNull(String s) {
        if(s == null)
            return null;
        else {
            String t = s.trim();
            if(TextUtils.isEmpty(t))
                return null;
            return t;
        }
    }

    public static String captureSingleValue(String value, Pattern pattern) {
        final Matcher m = pattern.matcher(value);

        if (m.find()) {
            try {
                return m.group(1);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
