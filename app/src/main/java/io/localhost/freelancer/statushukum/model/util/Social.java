package io.localhost.freelancer.statushukum.model.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * This <StatusHukum> project created by :
 * Name         : syafiq
 * Date / Time  : 05 June 2017, 8:43 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class Social
{
    public Facebook facebook;
    public Twitter twitter;
    public Instagram instagram;
    public GPlus gPlus;

    public Social()
    {
        this.facebook = new Facebook("https://www.facebook.com/582966285239620/", "582966285239620");
        this.twitter = new Twitter("https://twitter.com/status_hukum");
        this.instagram = new Instagram("https://www.instagram.com/statushukum/");
        this.gPlus = new GPlus("https://plus.google.com/u/0/102250368241027727812/");
    }

    public static String urlEncode(String str)
    {
        try
        {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException("URLEncoder.encode() failed for " + str);
        }
    }

    public class Facebook
    {
        private final String url;
        private final String user;

        public Facebook(String url, String user)
        {
            this.url = url;
            this.user = user;
        }

        public String getFacebookPageURL(Context context)
        {
            return this.getFacebookPageURL(this.url, this.user, context);
        }

        public String getFacebookPageURL(String facebookUrl, String pageName, Context context)
        {
            PackageManager packageManager = context.getPackageManager();
            try
            {
                int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                if(versionCode >= 3002850)
                { //newer versions of fb app
                    return "fb://facewebmodal/f?href=" + facebookUrl;
                }
                else
                { //older versions of fb app
                    return "fb://page/" + pageName;
                }
            }
            catch(PackageManager.NameNotFoundException e)
            {
                return facebookUrl; //normal web url
            }
        }

        public Intent getFacebookIntent(Context context)
        {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            String facebookUrl = this.getFacebookPageURL(context);
            intent.setData(Uri.parse(facebookUrl));
            return intent;
        }
    }

    public class Twitter
    {
        private final String url;

        public Twitter(String url)
        {
            this.url = url;
        }

        public String getTwitterPageURL(Context context)
        {
            return this.getTwitterPageURL(this.url, context);
        }

        public String getTwitterPageURL(String url, Context context)
        {
            String tweetUrl = String.format("https://twitter.com/intent/tweet?url=%s", Social.urlEncode(url));
            return tweetUrl;
        }

        public Intent getTwitterIntent(Context context)
        {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.getTwitterPageURL(context)));

            // Narrow down to official Twitter app, if available:
            List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
            for(final ResolveInfo match : matches)
            {
                if(match.activityInfo.packageName.toLowerCase().startsWith("com.twitter"))
                {
                    intent.setPackage(match.activityInfo.packageName);
                }
            }
            return intent;
        }
    }

    public class Instagram
    {

        private final String url;

        public Instagram(String url)
        {
            this.url = url;
        }

        public String getInstagramPageURL(Context context)
        {
            return this.getInstagramPageURL(this.url, context);
        }

        public String getInstagramPageURL(String url, Context context)
        {
            return url;
        }

        public Intent getInstagramIntent(Context context)
        {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.getInstagramPageURL(context)));

            // Narrow down to official Instagram app, if available:
            List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
            for(final ResolveInfo match : matches)
            {
                if(match.activityInfo.packageName.toLowerCase().startsWith("com.instagram.android"))
                {
                    intent.setPackage(match.activityInfo.packageName);
                }
            }
            return intent;
        }
    }

    public class GPlus
    {
        private final String url;

        public GPlus(String url)
        {
            this.url = url;
        }

        public String getGPlusPageURL(Context context)
        {
            return this.getGPlusPageURL(this.url, context);
        }

        public String getGPlusPageURL(String url, Context context)
        {
            return url;
        }

        public Intent getGPlusIntent(Context context)
        {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.getGPlusPageURL(context)));

            // Narrow down to official GPlus app, if available:
            List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
            for(final ResolveInfo match : matches)
            {
                if(match.activityInfo.packageName.toLowerCase().startsWith("com.google.android.apps.plus"))
                {
                    intent.setPackage(match.activityInfo.packageName);
                }
            }
            return intent;
        }
    }
}
