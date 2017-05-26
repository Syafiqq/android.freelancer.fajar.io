package io.localhost.freelancer.statushukum.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.database> created by :
 * Name         : syafiq
 * Date / Time  : 12 December 2016, 8:56 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class DatabaseModel
{
    public static final String CLASS_NAME = "DatabaseModel";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.database.DatabaseModel";

    protected final Context context;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase database;

    public DatabaseModel(final Context context)
    {


        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public void openWrite() throws SQLException
    {


        if(!this.isDatabaseReady())
        {
            this.database = this.dbHelper.getWritableDatabase();
        }
    }

    public void openRead() throws SQLException
    {


        if(!this.isDatabaseReady())
        {
            this.database = this.dbHelper.getReadableDatabase();
        }
    }

    public void close()
    {


        this.dbHelper.close();
    }

    public boolean isDatabaseReady()
    {


        return this.database != null && this.database.isOpen();
    }
}
