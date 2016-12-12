package io.localhost.freelancer.statushukum.model.entity;

import android.util.Log;

import org.joda.time.DateTime;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.entity> created by :
 * Name         : syafiq
 * Date / Time  : 12 December 2016, 9:00 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class ME_DataTag
{
    public static final String CLASS_NAME = "ME_DataTag";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.entity.ME_DataTag";

    private final int      data;
    private final int      tag;
    private final DateTime timestamp;

    public ME_DataTag(int data, int tag, DateTime timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.data = data;
        this.tag = tag;
        this.timestamp = timestamp;
    }

    public int getData()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getData");

        return this.data;
    }

    public int getTag()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getTag");

        return this.tag;
    }

    public DateTime getTimestamp()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getTimestamp");

        return this.timestamp;
    }
}
