package io.localhost.freelancer.statushukum.model.util.helper;

import android.text.TextUtils;

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
}
