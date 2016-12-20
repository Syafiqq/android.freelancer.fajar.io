package io.localhost.freelancer.statushukum.model.database;

import android.provider.BaseColumns;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.database> created by :
 * Name         : syafiq
 * Date / Time  : 12 December 2016, 6:13 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public final class DatabaseContract
{
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract()
    {
    }

    /* Inner class that defines the table contents */
    public static class Data implements BaseColumns
    {
        public static final String TABLE_NAME              = "data";
        public static final String COLUMN_NAME_ID          = "id";
        public static final String COLUMN_NAME_YEAR        = "year";
        public static final String COLUMN_NAME_NO          = "no";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_STATUS      = "status";
    }

    /* Inner class that defines the table contents */
    public static class Tag implements BaseColumns
    {
        public static final String TABLE_NAME              = "tag";
        public static final String COLUMN_NAME_ID          = "id";
        public static final String COLUMN_NAME_NAME        = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COLOR       = "color";
        public static final String COLUMN_NAME_COLORTEXT   = "colortext";
    }

    /* Inner class that defines the table contents */
    public static class DataTag implements BaseColumns
    {
        public static final String TABLE_NAME       = "datatag";
        public static final String COLUMN_NAME_DATA = "data";
        public static final String COLUMN_NAME_TAG  = "tag";
    }

    /* Inner class that defines the table contents */
    public static class Version implements BaseColumns
    {
        public static final String TABLE_NAME            = "version";
        public static final String COLUMN_NAME_ID        = "id";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
