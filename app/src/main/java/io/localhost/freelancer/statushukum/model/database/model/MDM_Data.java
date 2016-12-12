package io.localhost.freelancer.statushukum.model.database.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import io.localhost.freelancer.statushukum.model.database.DatabaseModel;

import static io.localhost.freelancer.statushukum.model.database.DatabaseContract.Data;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.database.model> created by :
 * Name         : syafiq
 * Date / Time  : 12 December 2016, 10:00 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class MDM_Data extends DatabaseModel
{
    public static final String CLASS_NAME = "MDM_Data";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.database.model.MDM_Data";

    private static MDM_Data mInstance = null;

    private MDM_Data(final Context context)
    {
        super(context);
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");
    }

    public static MDM_Data getInstance(final Context ctx)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getInstance");

        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if(MDM_Data.mInstance == null)
        {
            MDM_Data.mInstance = new MDM_Data(ctx);
        }
        return MDM_Data.mInstance;
    }

    public static void insert(final SQLiteDatabase database, int id, int year, String no, String description, String status, String timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".static insert");

        database.execSQL(
                String.format("INSERT INTO %s(`id`, `year`, `no`, `description`, `status`, `timestamp`) VALUES (?, ?, ?, ?, ?, ?)", Data.TABLE_NAME),
                new Object[] {id, year, no, description, status, timestamp});
    }

    public void insert(int id, int year, String no, String description, String status, String timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".insert");

        MDM_Data.insert(super.database, id, year, no, description, status, timestamp);
    }
}
