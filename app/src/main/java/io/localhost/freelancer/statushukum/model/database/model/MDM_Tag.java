package io.localhost.freelancer.statushukum.model.database.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import io.localhost.freelancer.statushukum.model.database.DatabaseContract.Tag;
import io.localhost.freelancer.statushukum.model.database.DatabaseModel;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;

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

    }

    public static MDM_Tag getInstance(final Context ctx)
    {


        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:

         */
        if(MDM_Tag.mInstance == null)
        {
            MDM_Tag.mInstance = new MDM_Tag(ctx);
        }
        return MDM_Tag.mInstance;
    }

    public static void insert(final SQLiteDatabase database, int id, String name, String description, String color, String colorText)
    {


        database.execSQL(
                String.format(Locale.getDefault(), "INSERT INTO %s(`id`, `name`, `description`, `color`, `colortext`) VALUES (?, ?, ?, ?, ?)", Tag.TABLE_NAME),
                new Object[] {id, name, description, color, colorText});
    }

    public static void deleteAll(final SQLiteDatabase database)
    {


        database.execSQL(String.format(Locale.getDefault(), "DELETE FROM `%s`", Tag.TABLE_NAME), new Object[] {});
    }

    public void insert(int id, String name, String description, String color, String colorText)
    {


        MDM_Tag.insert(super.database, id, name, description, color, colorText);
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

        MDM_Tag.deleteAll(super.database);
    }

    public Map<Integer, ME_Tag> getAll()
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

    public void insertOrUpdate(int id, String name, String description, String color, String colortext)
    {
        boolean exists = this.isExists(id);
        if(exists)
        {
            this.update(id, name, description, color, colortext);
        }
        else
        {
            this.insert(id, name, description, color, colortext);
        }
    }

    private void update(int id, String name, String description, String color, String colortext)
    {

        try
        {
            super.openWrite();
        }
        catch(SQLException ignored)
        {

        }

        super.database.execSQL(
                String.format(Locale.getDefault(), "UPDATE `%s` SET `%s`=?,`%s`=?,`%s`=?,`%s`=? WHERE `%s`=?",
                        Tag.TABLE_NAME,
                        Tag.COLUMN_NAME_NAME,
                        Tag.COLUMN_NAME_DESCRIPTION,
                        Tag.COLUMN_NAME_COLOR,
                        Tag.COLUMN_NAME_COLORTEXT,
                        Tag.COLUMN_NAME_ID
                ),
                new Object[] {name, description, color, colortext, id});
    }

    private boolean isExists(int id)
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
}
