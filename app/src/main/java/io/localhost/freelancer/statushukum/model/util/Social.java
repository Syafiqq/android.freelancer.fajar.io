package io.localhost.freelancer.statushukum.model.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import io.localhost.freelancer.statushukum.R;

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
        this.facebook = new Facebook("https://www.facebook.com/statushukum/", "statushukum");
        this.twitter = new Twitter("https://twitter.com/status_hukum");
        this.instagram = new Instagram("https://www.instagram.com/statushukum/", "statushukum");
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

        private List<Intent> getFacebookApp(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            PackageManager packageManager = context.getPackageManager();
            try
            {
                final String url;
                int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                if(versionCode >= 3002850)
                { //newer versions of fb app
                    url = "fb://facewebmodal/f?href=" + this.url;
                }
                else
                { //older versions of fb app
                    url = "fb://page/" + this.user;
                }
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
                if(!resolveInfoList.isEmpty())
                {
                    for(final ResolveInfo resolveInfo : resolveInfoList)
                    {
                        Log.d("match", resolveInfo.activityInfo.packageName.toLowerCase());
                    }
                    for(ResolveInfo resolveInfo : resolveInfoList)
                    {
                        if(resolveInfo.activityInfo.packageName.contains("facebook"))
                        {
                            final String packageName = resolveInfo.activityInfo.packageName;
                            Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            target.setPackage(packageName);
                            intents.add(target);
                        }
                    }
                }
            }
            catch(PackageManager.NameNotFoundException ignored)
            {

            }
            return intents;
        }

        private List<Intent> getDefaultFacebook(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            final String url = this.url;
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
            if(!resolveInfoList.isEmpty())
            {
                for(final ResolveInfo resolveInfo : resolveInfoList)
                {
                    Log.d("match", resolveInfo.activityInfo.packageName.toLowerCase());
                }
                for(ResolveInfo resolveInfo : resolveInfoList)
                {
                    if(!resolveInfo.activityInfo.packageName.contains("facebook"))
                    {
                        final String packageName = resolveInfo.activityInfo.packageName;
                        Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        target.setPackage(packageName);
                        intents.add(target);
                    }
                }
            }
            return intents;
        }

        public Intent getFacebookIntent(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            intents.addAll(this.getFacebookApp(context));
            intents.addAll(this.getDefaultFacebook(context));
            if(!intents.isEmpty())
            {
                final Intent chooser = Intent.createChooser(intents.remove(0), context.getResources().getString(R.string.global_social_app_chooser_title));
                if(!intents.isEmpty())
                {
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
                }
                return chooser;
            }
            else
            {
                return null;
            }
        }
    }

    public class Twitter
    {
        private final String url;

        public Twitter(String url)
        {
            this.url = url;
        }

        private List<Intent> getTwitterApp(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            PackageManager packageManager = context.getPackageManager();
            try
            {
                final String url;
                packageManager.getPackageInfo("com.twitter.android", PackageManager.GET_ACTIVITIES);
                url = this.url;
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
                if(!resolveInfoList.isEmpty())
                {
                    for(final ResolveInfo resolveInfo : resolveInfoList)
                    {
                        Log.d("match", resolveInfo.activityInfo.packageName.toLowerCase());
                    }
                    for(ResolveInfo resolveInfo : resolveInfoList)
                    {
                        if(resolveInfo.activityInfo.packageName.contains("twitter"))
                        {
                            final String packageName = resolveInfo.activityInfo.packageName;
                            Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            target.setPackage(packageName);
                            intents.add(target);
                        }
                    }
                }
            }
            catch(PackageManager.NameNotFoundException ignored)
            {

            }
            return intents;
        }

        private List<Intent> getDefaultTwitter(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            final String url = this.url;
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://twitter.com/intent/tweet?url=%s", Social.urlEncode(url))));
            final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
            if(!resolveInfoList.isEmpty())
            {
                for(final ResolveInfo resolveInfo : resolveInfoList)
                {
                    Log.d("match", resolveInfo.activityInfo.packageName.toLowerCase());
                }
                for(ResolveInfo resolveInfo : resolveInfoList)
                {
                    if(!resolveInfo.activityInfo.packageName.contains("twitter"))
                    {
                        final String packageName = resolveInfo.activityInfo.packageName;
                        Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        target.setPackage(packageName);
                        intents.add(target);
                    }
                }
            }
            return intents;
        }

        public Intent getTwitterIntent(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            intents.addAll(this.getTwitterApp(context));
            intents.addAll(this.getDefaultTwitter(context));
            if(!intents.isEmpty())
            {
                final Intent chooser = Intent.createChooser(intents.remove(0), context.getResources().getString(R.string.global_social_app_chooser_title));
                if(!intents.isEmpty())
                {
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
                }
                return chooser;
            }
            else
            {
                return null;
            }
        }
    }

    public class Instagram
    {
        private final String url;
        private final String user;

        public Instagram(String url, String user)
        {
            this.url = url;
            this.user = user;
        }

        private List<Intent> getInstagramApp(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            PackageManager packageManager = context.getPackageManager();
            try
            {
                final String url;
                packageManager.getPackageInfo("com.instagram.android", PackageManager.GET_ACTIVITIES);
                url = "http://instagram.com/_u/" + this.user;
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
                if(!resolveInfoList.isEmpty())
                {
                    for(final ResolveInfo resolveInfo : resolveInfoList)
                    {
                        Log.d("match", resolveInfo.activityInfo.packageName.toLowerCase());
                    }
                    for(ResolveInfo resolveInfo : resolveInfoList)
                    {
                        if(resolveInfo.activityInfo.packageName.contains("instagram"))
                        {
                            final String packageName = resolveInfo.activityInfo.packageName;
                            Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            target.setPackage(packageName);
                            intents.add(target);
                        }
                    }
                }
            }
            catch(PackageManager.NameNotFoundException ignored)
            {

            }
            return intents;
        }

        private List<Intent> getDefaultInstagram(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            final String url = this.url;
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
            if(!resolveInfoList.isEmpty())
            {
                for(final ResolveInfo resolveInfo : resolveInfoList)
                {
                    Log.d("match", resolveInfo.activityInfo.packageName.toLowerCase());
                }
                for(ResolveInfo resolveInfo : resolveInfoList)
                {
                    if(!resolveInfo.activityInfo.packageName.contains("instagram"))
                    {
                        final String packageName = resolveInfo.activityInfo.packageName;
                        Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        target.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
                        target.setPackage(packageName);
                        intents.add(target);
                    }
                }
            }
            return intents;
        }

        public Intent getInstagramIntent(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            intents.addAll(this.getInstagramApp(context));
            intents.addAll(this.getDefaultInstagram(context));
            if(!intents.isEmpty())
            {
                final Intent chooser = Intent.createChooser(intents.remove(0), context.getResources().getString(R.string.global_social_app_chooser_title));
                if(!intents.isEmpty())
                {
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
                }
                return chooser;
            }
            else
            {
                return null;
            }
        }
    }

    public class GPlus
    {
        private final String url;

        public GPlus(String url)
        {
            this.url = url;
        }

        private List<Intent> getGPlusApp(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            PackageManager packageManager = context.getPackageManager();
            try
            {
                final String url;
                packageManager.getPackageInfo("com.google.android.apps.plus", PackageManager.GET_ACTIVITIES);
                url = this.url;
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
                if(!resolveInfoList.isEmpty())
                {
                    for(final ResolveInfo resolveInfo : resolveInfoList)
                    {
                        Log.d("match", resolveInfo.activityInfo.packageName.toLowerCase());
                    }
                    for(ResolveInfo resolveInfo : resolveInfoList)
                    {
                        if(resolveInfo.activityInfo.packageName.contains("plus"))
                        {
                            final String packageName = resolveInfo.activityInfo.packageName;
                            Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            target.setPackage(packageName);
                            intents.add(target);
                        }
                    }
                }
            }
            catch(PackageManager.NameNotFoundException ignored)
            {

            }
            return intents;
        }

        private List<Intent> getDefaultGPlus(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            final String url = this.url;
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
            if(!resolveInfoList.isEmpty())
            {
                for(final ResolveInfo resolveInfo : resolveInfoList)
                {
                    Log.d("match", resolveInfo.activityInfo.packageName.toLowerCase());
                }
                for(ResolveInfo resolveInfo : resolveInfoList)
                {
                    if(!resolveInfo.activityInfo.packageName.contains("plus"))
                    {
                        final String packageName = resolveInfo.activityInfo.packageName;
                        Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        target.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
                        target.setPackage(packageName);
                        intents.add(target);
                    }
                }
            }
            return intents;
        }

        public Intent getGPlusIntent(Context context)
        {
            final List<Intent> intents = new LinkedList<>();
            intents.addAll(this.getGPlusApp(context));
            intents.addAll(this.getDefaultGPlus(context));
            if(!intents.isEmpty())
            {
                final Intent chooser = Intent.createChooser(intents.remove(0), context.getResources().getString(R.string.global_social_app_chooser_title));
                if(!intents.isEmpty())
                {
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
                }
                return chooser;
            }
            else
            {
                return null;
            }
        }
    }
}
