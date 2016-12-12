package io.localhost.freelancer.statushukum.model.database.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import io.localhost.freelancer.statushukum.model.database.DatabaseContract.Tag;
import io.localhost.freelancer.statushukum.model.database.DatabaseModel;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.database.model> created by :
 * Name         : syafiq
 * Date / Time  : 13 December 2016, 12:05 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class MDM_Tag extends DatabaseModel
{
    public static final String CLASS_NAME = "MDM_Tag";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.database.model.MDM_Tag";

    private static MDM_Tag mInstance = null;

    private MDM_Tag(final Context context)
    {
        super(context);
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");
    }

    public static MDM_Tag getInstance(final Context ctx)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getInstance");

        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if(MDM_Tag.mInstance == null)
        {
            MDM_Tag.mInstance = new MDM_Tag(ctx);
        }
        return MDM_Tag.mInstance;
    }

    public static void insert(final SQLiteDatabase database, int id, String name, String description, String color, String colorText, String timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".static insert");

        database.execSQL(
                String.format("INSERT INTO %s(`id`, `name`, `description`, `color`, `colortext`, `timestamp`) VALUES (?, ?, ?, ?, ?, ?)", Tag.TABLE_NAME),
                new Object[] {id, name, description, color, colorText, timestamp});
    }

    public void insert(int id, String name, String description, String color, String colorText, String timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".insert");

        MDM_Tag.insert(super.database, id, name, description, color, colorText, timestamp);
    }
}
