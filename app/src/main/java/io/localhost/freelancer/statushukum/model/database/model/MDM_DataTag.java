package io.localhost.freelancer.statushukum.model.database.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Locale;

import io.localhost.freelancer.statushukum.model.database.DatabaseContract.DataTag;
import io.localhost.freelancer.statushukum.model.database.DatabaseModel;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.database.model> created by :
 * Name         : syafiq
 * Date / Time  : 13 December 2016, 12:23 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class MDM_DataTag extends DatabaseModel
{
    public static final String CLASS_NAME = "MDM_DataTag";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag";

    private static MDM_DataTag mInstance = null;

    private MDM_DataTag(final Context context)
    {
        super(context);
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");
    }

    public static MDM_DataTag getInstance(final Context ctx)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getInstance");

        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if(MDM_DataTag.mInstance == null)
        {
            MDM_DataTag.mInstance = new MDM_DataTag(ctx);
        }
        return MDM_DataTag.mInstance;
    }

    public static void insert(final SQLiteDatabase database, int data, int tag, String timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".static insert");

        database.execSQL(
                String.format(Locale.getDefault(), "INSERT INTO %s(`data`, `tag`, `timestamp`) VALUES (?, ?, ?)", DataTag.TABLE_NAME),
                new Object[] {data, tag, timestamp});
    }

    public void insert(int data, int tag, String timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".insert");

        MDM_DataTag.insert(super.database, data, tag, timestamp);
    }

}
