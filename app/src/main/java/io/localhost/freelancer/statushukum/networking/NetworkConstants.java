package io.localhost.freelancer.statushukum.networking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This <TestOAuth_001> project in package <id.ac.ub.filkom.se.kcv.testoauth_001.model.networking> created by :
 * Name         : syafiq
 * Date / Time  : 11 November 2016, 7:06 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class NetworkConstants
{
    public static String API_BASE_URL;
    public static String API_SITE_URL;

    static
    {
        NetworkConstants.API_BASE_URL = "http://status-hukum.esy.es/";
        NetworkConstants.API_SITE_URL = "http://status-hukum.esy.es/";
    }

    public static String getURL(@NonNull String domain, @NonNull String path, @Nullable String data)
    {
        return domain + path + (data == null ? "" : "?" + data);
    }
}
