package io.localhost.freelancer.statushukum.model.entity;


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

    private final int data;
    private final int tag;

    public ME_DataTag(int data, int tag)
    {


        this.data = data;
        this.tag = tag;
    }

    public int getData()
    {
        return this.data;
    }

    public int getTag()
    {
        return this.tag;
    }
}
