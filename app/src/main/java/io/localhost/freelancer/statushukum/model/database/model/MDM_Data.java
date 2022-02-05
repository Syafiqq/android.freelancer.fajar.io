package io.localhost.freelancer.statushukum.model.database.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.localhost.freelancer.statushukum.model.MenuModel;
import io.localhost.freelancer.statushukum.model.MenuModelType;
import io.localhost.freelancer.statushukum.model.database.DatabaseModel;
import io.localhost.freelancer.statushukum.model.entity.ME_Data;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;

import static io.localhost.freelancer.statushukum.model.database.DatabaseContract.Data;
import static io.localhost.freelancer.statushukum.model.database.DatabaseContract.DataTag;

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

    public static void insert(final SQLiteDatabase database, int id, int year, String no, String description, String status, int category, String reference)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".static insert");

        try
        {
            database.execSQL(
                    String.format(Locale.getDefault(), "INSERT INTO %s(`%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                            Data.TABLE_NAME,
                            Data.COLUMN_NAME_ID,
                            Data.COLUMN_NAME_YEAR,
                            Data.COLUMN_NAME_NO,
                            Data.COLUMN_NAME_DESCRIPTION,
                            Data.COLUMN_NAME_STATUS,
                            Data.COLUMN_NAME_CATEGORY,
                            Data.COLUMN_NAME_REFERENCE),
                    new Object[] {id, year, no, description, status, category, reference});
        }
        catch(android.database.SQLException e)
        {
            System.out.println(e);
        }

    }

    public static void deleteAll(final SQLiteDatabase database)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".static deleteAll");

        database.execSQL(String.format(Locale.getDefault(), "DELETE FROM `%s`", Data.TABLE_NAME), new Object[] {});
    }

    public void insert(int id, int year, String no, String description, String status, int category, String reference)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".insert");
        try
        {
            super.openWrite();
        }
        catch(SQLException ignored)
        {
            Log.i(CLASS_NAME, "SQLException");
        }


        MDM_Data.insert(super.database, id, year, no, description, status, category, reference);
    }

    public void deleteAll()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ". deleteAll");

        try
        {
            super.openWrite();
        }
        catch(SQLException ignored)
        {
            Log.i(CLASS_NAME, "SQLException");
        }

        MDM_Data.deleteAll(super.database);
    }

    public List<CountPerYear> getCountPerYear(int category)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getCountPerYear");
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
                        "SELECT `%s`, count(`%s`) AS 'count' FROM `%s` WHERE `%s` = ? GROUP BY `%s` ORDER BY `%s` ASC",
                        Data.COLUMN_NAME_YEAR,
                        Data.COLUMN_NAME_ID,
                        Data.TABLE_NAME,
                        Data.COLUMN_NAME_CATEGORY,
                        Data.COLUMN_NAME_YEAR,
                        Data.COLUMN_NAME_YEAR
                ),
                new String[] {String.valueOf(category)});

        List<CountPerYear> records = new LinkedList<>();
        if(cursor.moveToFirst())
        {
            do
            {
                records.add(new CountPerYear(cursor.getInt(0), cursor.getInt(1)));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    public List<YearMetadata> getYearList(final int year, int category)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getYearList");
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
                        "SELECT `%s`.`%s`, `%s`.`%s`, `%s`.`%s`, count(`%s`.`%s`) AS 'tag' FROM `%s` LEFT OUTER JOIN `%s` ON `%s`.`%s` = `%s`.`%s`  WHERE `%s`.`%s` = ? AND `%s`.`%s` = ? GROUP BY `%s`.`%s` ORDER BY `%s`.`%s` ASC",
                        Data.TABLE_NAME, Data.COLUMN_NAME_ID,
                        Data.TABLE_NAME, Data.COLUMN_NAME_YEAR,
                        Data.TABLE_NAME, Data.COLUMN_NAME_NO,
                        DataTag.TABLE_NAME, DataTag.COLUMN_NAME_TAG,
                        Data.TABLE_NAME,
                        DataTag.TABLE_NAME,
                        Data.TABLE_NAME, Data.COLUMN_NAME_ID,
                        DataTag.TABLE_NAME, DataTag.COLUMN_NAME_DATA,
                        Data.TABLE_NAME, Data.COLUMN_NAME_YEAR,
                        Data.TABLE_NAME, Data.COLUMN_NAME_CATEGORY,
                        Data.TABLE_NAME, Data.COLUMN_NAME_ID,
                        Data.TABLE_NAME, Data.COLUMN_NAME_ID
                ),
                new String[] {String.valueOf(year), String.valueOf(category)});

        final List<YearMetadata> records = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do
            {
                records.add(new YearMetadata(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3)));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    public ME_Data getFromID(int id)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getFromID");
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
                        "SELECT `%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s` FROM `%s` WHERE `%s` = ? LIMIT 1",
                        Data.COLUMN_NAME_ID,
                        Data.COLUMN_NAME_YEAR,
                        Data.COLUMN_NAME_NO,
                        Data.COLUMN_NAME_DESCRIPTION,
                        Data.COLUMN_NAME_STATUS,
                        Data.COLUMN_NAME_CATEGORY,
                        Data.COLUMN_NAME_REFERENCE,
                        Data.TABLE_NAME,
                        Data.COLUMN_NAME_ID
                ),
                new String[] {String.valueOf(id)});

        ME_Data total = null;
        if(cursor.moveToFirst())
        {
            do
            {
                total = new ME_Data(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getString(6));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return total;
    }

    public void insertOrUpdate(int id, int year, String no, String description, String status, int category, String reference)
    {
        boolean exists = this.isExists(id);
        if(exists)
        {
            this.update(id, year, no, description, status, category, reference);
        }
        else
        {
            this.insert(id, year, no, description, status, category, reference);
        }
    }

    private void update(int id, int year, String no, String description, String status, int category, String reference)
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
                String.format(Locale.getDefault(), "UPDATE `%s` SET `%s`=?,`%s`=?,`%s`=?,`%s`=?, `%s`=?, `%s`=? WHERE `%s`=?",
                        Data.TABLE_NAME,
                        Data.COLUMN_NAME_YEAR,
                        Data.COLUMN_NAME_NO,
                        Data.COLUMN_NAME_DESCRIPTION,
                        Data.COLUMN_NAME_STATUS,
                        Data.COLUMN_NAME_CATEGORY,
                        Data.COLUMN_NAME_REFERENCE,
                        Data.COLUMN_NAME_ID
                ),
                new Object[] {year, no, description, status, id, category, reference});
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
                        Data.COLUMN_NAME_ID,
                        Data.TABLE_NAME,
                        Data.COLUMN_NAME_ID
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

    public List<MetadataSearchable> getSearchableList(String query, int category)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getYearList");
        try
        {
            super.openRead();
        }
        catch(SQLException ignored)
        {
            Log.i(CLASS_NAME, "SQLException");
        }

        ArrayList<String> categoryWhitelist = new ArrayList<String>(MenuModel.lawMenusWhitelist.size());
        for (MenuModelType lawMenuId : MenuModel.lawMenusWhitelist) {
            categoryWhitelist.add(String.format("'%s'", MenuModel.getDatabaseCategory(lawMenuId)));
        }

        final Cursor cursor = super.database.rawQuery(
                String.format(
                        Locale.getDefault(),
                        "SELECT `%s`.`%s`, `%s`.`%s`, `%s`.`%s`, `%s`.`%s`, `%s`.`%s`, count(`%s`.`%s`) AS 'tag' FROM `%s` LEFT OUTER JOIN `%s` ON `%s`.`%s` = `%s`.`%s`  WHERE `%s`.`%s` LIKE ? AND `%s`.`%s` IN (?) GROUP BY `%s`.`%s` ORDER BY `%s`.`%s` ASC",
                        Data.TABLE_NAME, Data.COLUMN_NAME_ID,
                        Data.TABLE_NAME, Data.COLUMN_NAME_YEAR,
                        Data.TABLE_NAME, Data.COLUMN_NAME_NO,
                        Data.TABLE_NAME, Data.COLUMN_NAME_DESCRIPTION,
                        Data.TABLE_NAME, Data.COLUMN_NAME_CATEGORY,
                        DataTag.TABLE_NAME, DataTag.COLUMN_NAME_TAG,
                        Data.TABLE_NAME,
                        DataTag.TABLE_NAME,
                        Data.TABLE_NAME, Data.COLUMN_NAME_ID,
                        DataTag.TABLE_NAME, DataTag.COLUMN_NAME_DATA,
                        Data.TABLE_NAME, Data.COLUMN_NAME_DESCRIPTION,
                        Data.TABLE_NAME, Data.COLUMN_NAME_CATEGORY,
                        Data.TABLE_NAME, Data.COLUMN_NAME_ID,
                        Data.TABLE_NAME, Data.COLUMN_NAME_ID
                ),
                new String[] {
                        String.valueOf("%" + query + "%"),
                        TextUtils.join(",", categoryWhitelist)
                });

        final List<MetadataSearchable> records = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do
            {
                records.add(new MetadataSearchable(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(5), cursor.getString(3)));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    public static class CountPerYear
    {
        private final int year;
        private final int count;

        public CountPerYear(int year, int count)
        {
            this.year = year;
            this.count = count;
        }

        public int getYear()
        {
            return this.year;
        }

        public int getCount()
        {
            return this.count;
        }

        @Override
        public String toString()
        {
            return "CountPerYear{" +
                    "year=" + year +
                    ", count=" + count +
                    '}';
        }
    }

    public static class YearMetadata
    {
        private final int id;
        private final int year;
        private final String no;
        private final int tagSize;
        private final List<ME_Tag> tags;

        public YearMetadata(int id, int year, String no, int tagSize)
        {
            this.id = id;
            this.year = year;
            this.no = no;
            this.tagSize = tagSize;
            this.tags = new ArrayList<>(tagSize);
        }

        public int getTagSize()
        {
            return this.tagSize;
        }

        public boolean add(ME_Tag me_tag)
        {
            return this.tags.add(me_tag);
        }

        public int getId()
        {
            return this.id;
        }

        public int getYear()
        {
            return this.year;
        }

        public String getNo()
        {
            return this.no;
        }

        public List<ME_Tag> getTags()
        {
            return this.tags;
        }

        @Override
        public String toString()
        {
            return "YearMetadata{" +
                    "id=" + id +
                    ", year=" + year +
                    ", no='" + no + '\'' +
                    ", tagSize=" + tagSize +
                    ", tags=" + tags +
                    '}';
        }
    }

    public static class MetadataSearchable extends YearMetadata
    {
        private final String description;

        public MetadataSearchable(int id, int year, String no, int tagSize, String description)
        {
            super(id, year, no, tagSize);
            this.description = description;
        }

        public String getDescription()
        {
            return description;
        }

        @Override
        public String toString()
        {
            return "MetadataSearchable{" +
                    "description='" + description + '\'' +
                    "YearMetadata='" + super.toString() + '\'' +
                    '}';
        }
    }
}
