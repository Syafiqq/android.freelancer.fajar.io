package io.localhost.freelancer.statushukum.model.database.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.LocalDateTime;

import java.sql.SQLException;
import java.util.Locale;

import io.localhost.freelancer.statushukum.model.database.DatabaseContract.Version;
import io.localhost.freelancer.statushukum.model.database.DatabaseModel;
import io.localhost.freelancer.statushukum.model.util.Setting;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.database.model> created by :
 * Name         : syafiq
 * Date / Time  : 20 December 2016, 3:34 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class MDM_Version extends DatabaseModel
{
    public static final String CLASS_NAME = "MDM_Version";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.database.model.MDM_Version";

    private static MDM_Version mInstance = null;

    private MDM_Version(final Context context)
    {
        super(context);

    }

    public static MDM_Version getInstance(final Context ctx)
    {


        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:

         */
        if(MDM_Version.mInstance == null)
        {
            MDM_Version.mInstance = new MDM_Version(ctx);
        }
        return MDM_Version.mInstance;
    }

    public static void insert(final SQLiteDatabase database, int id, String timestamp)
    {


        database.execSQL(
                String.format(Locale.getDefault(), "INSERT INTO %s(`%s`, `%s`) VALUES (?, ?)",
                        Version.TABLE_NAME,
                        Version.COLUMN_NAME_ID,
                        Version.COLUMN_NAME_TIMESTAMP),
                new Object[] {id, timestamp});
    }

    public static void deleteAll(final SQLiteDatabase database)
    {


        database.execSQL(String.format(Locale.getDefault(), "DELETE FROM `%s`", Version.TABLE_NAME), new Object[] {});
    }

    public LocalDateTime getVersion()
    {

        try
        {
            super.openRead();
        }
        catch(SQLException ignored)
        {

        }

        final Cursor cursor = super.database.rawQuery(
                String.format(
                        Locale.getDefault(),
                        "SELECT `%s` FROM `%s` ORDER BY `%s` DESC LIMIT 1",
                        Version.COLUMN_NAME_TIMESTAMP,
                        Version.TABLE_NAME,
                        Version.COLUMN_NAME_TIMESTAMP
                ),
                new String[] {});

        LocalDateTime total = null;
        if(cursor.moveToFirst())
        {
            do
            {
                total = LocalDateTime.parse(cursor.getString(0), Setting.timeStampFormat);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return total;
    }

    public void insert(int id, String timestamp)
    {

        try
        {
            super.openWrite();
        }
        catch(SQLException ignored)
        {

        }

        MDM_Version.insert(super.database, id, timestamp);
    }

    public void deleteAll()
    {


        try
        {
            super.openWrite();
        }
        catch(SQLException ignored)
        {

        }

        MDM_Version.deleteAll(super.database);
    }
}
