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

public class ME_Data
{
    public static final String CLASS_NAME = "ME_Data";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.entity.ME_Data";

    private final int      id;
    private final int      year;
    private final String   no;
    private final String   description;
    private final String   status;
    private final DateTime timestamp;

    public ME_Data(int id, int year, String no, String description, String status, DateTime timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.id = id;
        this.year = year;
        this.no = no;
        this.description = description;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getId()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getId");

        return this.id;
    }

    public int getYear()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getYear");

        return this.year;
    }

    public String getNo()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getNo");

        return this.no;
    }

    public String getDescription()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getDescription");

        return this.description;
    }

    public String getStatus()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getStatus");

        return this.status;
    }

    public DateTime getTimestamp()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getTimestamp");

        return this.timestamp;
    }
}
