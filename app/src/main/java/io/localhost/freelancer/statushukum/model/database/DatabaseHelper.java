package io.localhost.freelancer.statushukum.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;

import static io.localhost.freelancer.statushukum.model.database.DatabaseContract.Data;
import static io.localhost.freelancer.statushukum.model.database.DatabaseContract.DataTag;
import static io.localhost.freelancer.statushukum.model.database.DatabaseContract.Tag;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.database> created by :
 * Name         : syafiq
 * Date / Time  : 12 December 2016, 6:24 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String CLASS_NAME = "DatabaseHelper";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.database.DatabaseHelper";

    // If you change the database schema, you must increment the database version.
    public static final int    DATABASE_VERSION = 1;
    public static final String DATABASE_NAME    = "status_hukum.mcrypt";

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final char COMMA_SEPARATOR = ',';
    private static final char WHITESPACE      = ' ';

    private static final String TYPE_TEXT    = "TEXT";
    private static final String TYPE_INTEGER = "INTEGER";

    private static final String CONSTRAINT_NOT_NULL          = "NOT NULL";
    private static final String CONSTRAINT_CURRENT_TIMESTAMP = "DEFAULT CURRENT_TIMESTAMP";

    private static final String SQL_CREATE_DATA_ENTRIES = "" +
            "CREATE TABLE IF NOT EXISTS" + WHITESPACE + Data.TABLE_NAME + WHITESPACE +
            "( " +
            Data.COLUMN_NAME_ID + WHITESPACE + TYPE_INTEGER + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            Data.COLUMN_NAME_YEAR + WHITESPACE + TYPE_INTEGER + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            Data.COLUMN_NAME_NO + WHITESPACE + TYPE_TEXT + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            Data.COLUMN_NAME_DESCRIPTION + WHITESPACE + TYPE_TEXT + COMMA_SEPARATOR + WHITESPACE +
            Data.COLUMN_NAME_STATUS + WHITESPACE + TYPE_TEXT + COMMA_SEPARATOR + WHITESPACE +
            Data.COLUMN_NAME_TIMESTAMP + WHITESPACE + TYPE_TEXT + WHITESPACE + CONSTRAINT_CURRENT_TIMESTAMP + WHITESPACE +
            " );";

    private static final String SQL_CREATE_TAG_ENTRIES = "" +
            "CREATE TABLE IF NOT EXISTS" + WHITESPACE + Tag.TABLE_NAME + WHITESPACE +
            "( " +
            Tag.COLUMN_NAME_ID + WHITESPACE + TYPE_INTEGER + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            Tag.COLUMN_NAME_NAME + WHITESPACE + TYPE_TEXT + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            Tag.COLUMN_NAME_DESCRIPTION + WHITESPACE + TYPE_TEXT + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            Tag.COLUMN_NAME_COLOR + WHITESPACE + TYPE_TEXT + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            Tag.COLUMN_NAME_COLORTEXT + WHITESPACE + TYPE_TEXT + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            Tag.COLUMN_NAME_TIMESTAMP + WHITESPACE + TYPE_TEXT + WHITESPACE + CONSTRAINT_CURRENT_TIMESTAMP + WHITESPACE +
            " );";

    private static final String SQL_CREATE_DATATAG_ENTRIES = "" +
            "CREATE TABLE IF NOT EXISTS" + WHITESPACE + DataTag.TABLE_NAME + WHITESPACE +
            "( " +
            DataTag.COLUMN_NAME_DATA + WHITESPACE + TYPE_INTEGER + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            DataTag.COLUMN_NAME_TAG + WHITESPACE + TYPE_INTEGER + WHITESPACE + CONSTRAINT_NOT_NULL + COMMA_SEPARATOR + WHITESPACE +
            DataTag.COLUMN_NAME_TIMESTAMP + WHITESPACE + TYPE_TEXT + WHITESPACE + CONSTRAINT_CURRENT_TIMESTAMP + WHITESPACE +
            " );";

    private static final String SQL_DROP_DATA_ENTRIES = "" +
            "DROP TABLE IF EXISTS " + Data.TABLE_NAME;

    private static final String SQL_DROP_TAG_ENTRIES = "" +
            "DROP TABLE IF EXISTS " + Tag.TABLE_NAME;

    private static final String SQL_DROP_DATATAG_ENTRIES = "" +
            "DROP TABLE IF EXISTS " + DataTag.TABLE_NAME;

    private static DatabaseHelper mInstance = null;
    private final Context context;

    private DatabaseHelper(final Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.context = context;
    }

    public static DatabaseHelper getInstance(final Context ctx)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getInstance");

        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if(DatabaseHelper.mInstance == null)
        {
            DatabaseHelper.mInstance = new DatabaseHelper(ctx);
        }
        return DatabaseHelper.mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        sqLiteDatabase.execSQL(SQL_CREATE_DATA_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_TAG_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_DATATAG_ENTRIES);
        this.prePopulateDatabase(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onUpgrade");

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL(SQL_DROP_DATA_ENTRIES);
        sqLiteDatabase.execSQL(SQL_DROP_TAG_ENTRIES);
        sqLiteDatabase.execSQL(SQL_DROP_DATATAG_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        super.onDowngrade(db, oldVersion, newVersion);
        Log.i(CLASS_NAME, CLASS_PATH + ".onDowngrade");
    }

    private void prePopulateDatabase(final SQLiteDatabase sqLiteDatabase)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".prePopulateDatabase");

        final String json = this.readDataFromAssets("stream.json");
        try
        {
            final JSONObject jsonObject = new JSONObject(json);
            final JSONArray  data       = jsonObject.getJSONObject("data").getJSONArray("data");
            final JSONArray  tag        = jsonObject.getJSONObject("data").getJSONArray("tag");
            final JSONArray  datatag    = jsonObject.getJSONObject("data").getJSONArray("datatag");
            this.populateDataTable(sqLiteDatabase, data);
            this.populateTagTable(sqLiteDatabase, tag);
            this.populateDataTagTable(sqLiteDatabase, datatag);
        }
        catch(JSONException e)
        {
            Log.i(CLASS_NAME, "JSONException");
        }
    }

    private void populateDataTable(final SQLiteDatabase sqLiteDatabase, final JSONArray data)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".populateDataTable");

        MDM_Data.deleteAll(sqLiteDatabase);
        for(int i = -1, is = data.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = data.getJSONObject(i);
                MDM_Data.insert(
                        sqLiteDatabase,
                        entry.getInt("id"),
                        entry.getInt("year"),
                        entry.getString("no"),
                        entry.getString("description"),
                        entry.getString("status"),
                        entry.getString("timestamp"));
            }
            catch(JSONException ignored)
            {
                Log.i(CLASS_NAME, "JSONException");
            }
        }
    }


    private void populateTagTable(final SQLiteDatabase sqLiteDatabase, final JSONArray tag)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".populateTagTable");

        MDM_Tag.deleteAll(sqLiteDatabase);
        for(int i = -1, is = tag.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = tag.getJSONObject(i);
                MDM_Tag.insert(
                        sqLiteDatabase,
                        entry.getInt("id"),
                        entry.getString("name"),
                        entry.getString("description"),
                        entry.getString("color"),
                        entry.getString("colortext"),
                        entry.getString("timestamp"));
            }
            catch(JSONException ignored)
            {
                Log.i(CLASS_NAME, "JSONException");
            }
        }
    }


    private void populateDataTagTable(final SQLiteDatabase sqLiteDatabase, final JSONArray datatag)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".populateDataTagTable");

        MDM_DataTag.deleteAll(sqLiteDatabase);
        for(int i = -1, is = datatag.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = datatag.getJSONObject(i);
                MDM_DataTag.insert(
                        sqLiteDatabase,
                        entry.getInt("data"),
                        entry.getInt("tag"),
                        entry.getString("timestamp"));
            }
            catch(JSONException ignored)
            {
                Log.i(CLASS_NAME, "JSONException");
            }
        }
    }

    @NonNull
    private String readDataFromAssets(String path)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".readDataFromAssets");

        final StringBuilder sb     = new StringBuilder();
        BufferedReader      reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(this.context.getAssets().open(path)));

            // do reading, usually loop until end of file reading
            String mLine;
            while((mLine = reader.readLine()) != null)
            {
                sb.append(mLine);
            }
        }
        catch(IOException ignored)
        {
            Log.i(CLASS_NAME, "IOException");
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch(IOException ignored)
                {
                    Log.i(CLASS_NAME, "IOException");
                }
            }
        }

        return sb.toString();
    }
}
