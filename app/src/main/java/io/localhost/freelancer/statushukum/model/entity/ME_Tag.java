package io.localhost.freelancer.statushukum.model.entity;

import android.graphics.Color;
import android.util.Log;

import org.joda.time.DateTime;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.entity> created by :
 * Name         : syafiq
 * Date / Time  : 12 December 2016, 9:00 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class ME_Tag
{
    public static final String CLASS_NAME = "ME_Tag";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.entity.ME_Tag";

    private final int      id;
    private final String   name;
    private final String   desc;
    private final Color    color;
    private final Color    colorText;
    private final DateTime timestamp;

    public ME_Tag(int id, String name, String desc, Color color, Color colorText, DateTime timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.id = id;
        this.name = name;
        this.desc = desc;
        this.color = color;
        this.colorText = colorText;
        this.timestamp = timestamp;
    }

    public int getId()
    {
        Log.i(CLASS_NAME, CLASS_PATH + "getId");

        return this.id;
    }

    public String getName()
    {
        Log.i(CLASS_NAME, CLASS_PATH + "getName");

        return this.name;
    }

    public String getDesc()
    {
        Log.i(CLASS_NAME, CLASS_PATH + "getDesc");

        return this.desc;
    }

    public Color getColor()
    {
        Log.i(CLASS_NAME, CLASS_PATH + "getColor");

        return this.color;
    }

    public Color getColorText()
    {
        Log.i(CLASS_NAME, CLASS_PATH + "getColorText");

        return this.colorText;
    }

    public DateTime getTimestamp()
    {
        Log.i(CLASS_NAME, CLASS_PATH + "getTimestamp");

        return this.timestamp;
    }
}
