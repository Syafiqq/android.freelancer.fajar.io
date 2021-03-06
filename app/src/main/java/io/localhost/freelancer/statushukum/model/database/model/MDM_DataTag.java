package io.localhost.freelancer.statushukum.model.database.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
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

    }

    public static MDM_DataTag getInstance(final Context ctx)
    {


        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:

         */
        if(MDM_DataTag.mInstance == null)
        {
            MDM_DataTag.mInstance = new MDM_DataTag(ctx);
        }
        return MDM_DataTag.mInstance;
    }

    public static void insert(final SQLiteDatabase database, int data, int tag)
    {


        database.execSQL(
                String.format(Locale.getDefault(), "INSERT INTO %s(`data`, `tag`) VALUES (?, ?)", DataTag.TABLE_NAME),
                new Object[] {data, tag});
    }

    public static void deleteAll(SQLiteDatabase database)
    {


        database.execSQL(String.format(Locale.getDefault(), "DELETE FROM `%s`", DataTag.TABLE_NAME), new Object[] {});
    }

    public void insert(int data, int tag)
    {


        try
        {
            super.openWrite();
        }
        catch(SQLException ignored)
        {

        }

        MDM_DataTag.insert(super.database, data, tag);
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

        MDM_DataTag.deleteAll(super.database);
    }

    public List<Integer> getTagFromDataID(int data)
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
                        "SELECT `%s` FROM `%s` WHERE `%s` = ? ORDER BY `%s` ASC",
                        DataTag.COLUMN_NAME_TAG,
                        DataTag.TABLE_NAME,
                        DataTag.COLUMN_NAME_DATA,
                        DataTag.COLUMN_NAME_TAG
                ),
                new String[] {String.valueOf(data)});

        final List<Integer> records = new LinkedList<>();
        if(cursor.moveToFirst())
        {
            do
            {
                records.add(cursor.getInt(0));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return records;
    }
}
