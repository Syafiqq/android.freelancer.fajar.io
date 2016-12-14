package io.localhost.freelancer.statushukum.model.util;

import android.content.Context;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.util> created by :
 * Name         : syafiq
 * Date / Time  : 14 December 2016, 9:20 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */
public class Setting
{
    private static Setting ourInstance;

    private final Context context;
    private       boolean isAllShowed;

    private Setting(Context context)
    {
        this.context = context;
        this.isAllShowed = false;
    }

    public static Setting getInstance(final Context context)
    {
        if(Setting.ourInstance == null)
        {
            Setting.ourInstance = new Setting(context);
        }
        return Setting.ourInstance;
    }

    public boolean isAllShowed()
    {
        return this.isAllShowed;
    }

    public void setAllShowed(boolean allShowed)
    {
        isAllShowed = allShowed;
    }

    public synchronized void doSync()
    {

    }

    public synchronized void doDeepSync()
    {

    }
}
