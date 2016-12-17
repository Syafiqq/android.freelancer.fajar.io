package io.localhost.freelancer.statushukum.model.database.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import org.joda.time.LocalDateTime;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import io.localhost.freelancer.statushukum.model.database.DatabaseContract.Tag;
import io.localhost.freelancer.statushukum.model.database.DatabaseModel;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;
import io.localhost.freelancer.statushukum.model.util.Setting;

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
                String.format(Locale.getDefault(), "INSERT INTO %s(`id`, `name`, `description`, `color`, `colortext`, `timestamp`) VALUES (?, ?, ?, ?, ?, ?)", Tag.TABLE_NAME),
                new Object[] {id, name, description, color, colorText, timestamp});
    }

    public static void deleteAll(final SQLiteDatabase database)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".static deleteAll");

        database.execSQL(String.format(Locale.getDefault(), "DELETE FROM `%s`", Tag.TABLE_NAME), new Object[] {});
    }

    public void insert(int id, String name, String description, String color, String colorText, String timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".insert");

        MDM_Tag.insert(super.database, id, name, description, color, colorText, timestamp);
    }

    public Map<Integer, ME_Tag> getAll()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getAll");
        try
        {
            super.openRead();
        }
        catch(SQLException ignored)
        {
            Log.i(CLASS_NAME, "SQLException");
        }

        final Cursor cursor = super.database.rawQuery(
                String.format(
                        Locale.getDefault(),
                        "SELECT `%s`, `%s`, `%s`, `%s`, `%s` FROM `%s` ORDER BY `%s` ASC",
                        Tag.COLUMN_NAME_ID,
                        Tag.COLUMN_NAME_NAME,
                        Tag.COLUMN_NAME_DESCRIPTION,
                        Tag.COLUMN_NAME_COLOR,
                        Tag.COLUMN_NAME_COLORTEXT,
                        Tag.TABLE_NAME,
                        Tag.COLUMN_NAME_ID
                ), new String[] {});

        final Map<Integer, ME_Tag> records = new LinkedHashMap<>();
        if(cursor.moveToFirst())
        {
            do
            {
                records.put(cursor.getInt(0), new ME_Tag(cursor.getInt(0), cursor.getString(1), cursor.getString(2), Color.parseColor("#" + cursor.getString(3)), Color.parseColor("#" + cursor.getString(4))));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    public LocalDateTime getLatestTimestamp()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getLatestTimestamp");
        try
        {
            super.openRead();
        }
        catch(SQLException ignored)
        {
            Log.i(CLASS_NAME, "SQLException");
        }

        final Cursor cursor = super.database.rawQuery(
                String.format(
                        Locale.getDefault(),
                        "SELECT `%s` FROM `%s` ORDER BY `%s` DESC LIMIT 1",
                        Tag.COLUMN_NAME_TIMESTAMP,
                        Tag.TABLE_NAME,
                        Tag.COLUMN_NAME_TIMESTAMP
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

    public void insertOrUpdate(int id, String name, String description, String color, String colortext, String timestamp)
    {
        boolean exists = this.isExists(id);
        if(exists)
        {
            this.update(id, name, description, color, colortext, timestamp);
        }
        else
        {
            this.insert(id, name, description, color, colortext, timestamp);
        }
    }

    private void update(int id, String name, String description, String color, String colortext, String timestamp)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".update");
        try
        {
            super.openWrite();
        }
        catch(SQLException ignored)
        {
            Log.i(CLASS_NAME, "SQLException");
        }

        super.database.execSQL(
                String.format(Locale.getDefault(), "UPDATE `%s` SET `%s`=?,`%s`=?,`%s`=?,`%s`=?,`%s`=? WHERE `%s`=?",
                        Tag.TABLE_NAME,
                        Tag.COLUMN_NAME_NAME,
                        Tag.COLUMN_NAME_DESCRIPTION,
                        Tag.COLUMN_NAME_COLOR,
                        Tag.COLUMN_NAME_COLORTEXT,
                        Tag.COLUMN_NAME_TIMESTAMP,
                        Tag.COLUMN_NAME_ID
                ),
                new Object[] {name, description, color, colortext, timestamp, id});
    }

    private boolean isExists(int id)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".isExists");
        try
        {
            super.openRead();
        }
        catch(SQLException ignored)
        {
            Log.i(CLASS_NAME, "SQLException");
        }

        final Cursor cursor = super.database.rawQuery(
                String.format(
                        Locale.getDefault(),
                        "SELECT `%s` FROM `%s` WHERE `%s` = ? LIMIT 1",
                        Tag.COLUMN_NAME_ID,
                        Tag.TABLE_NAME,
                        Tag.COLUMN_NAME_ID
                ),
                new String[] {String.valueOf(id)});

        boolean exist = false;
        if(cursor.moveToFirst())
        {
            do
            {
                if(!cursor.isNull(0))
                {
                    exist = true;
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return exist;
    }

    public void deleteAll()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".static deleteAll");

        try
        {
            super.openWrite();
        }
        catch(SQLException ignored)
        {
            Log.i(CLASS_NAME, "SQLException");
        }

        MDM_Tag.deleteAll(super.database);
    }
}
