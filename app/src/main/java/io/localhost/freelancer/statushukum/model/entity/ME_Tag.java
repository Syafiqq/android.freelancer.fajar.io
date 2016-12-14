package io.localhost.freelancer.statushukum.model.entity;

import android.util.Log;

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

    private final int    id;
    private final String name;
    private final String desc;
    private final int    color;
    private final int    colorText;

    public ME_Tag(int id, String name, String desc, int color, int colorText)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.id = id;
        this.name = name;
        this.desc = desc;
        this.color = color;
        this.colorText = colorText;
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDesc()
    {
        return this.desc;
    }

    public int getColor()
    {
        return this.color;
    }

    public int getColorText()
    {
        return this.colorText;
    }

    @Override
    public String toString()
    {
        return "ME_Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", color=" + color +
                ", colorText=" + colorText +
                '}';
    }
}
