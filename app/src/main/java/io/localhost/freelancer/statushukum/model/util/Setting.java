package io.localhost.freelancer.statushukum.model.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observer;

import io.localhost.freelancer.statushukum.model.database.DatabaseHelper;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Version;
import io.localhost.freelancer.statushukum.networking.NetworkConstants;
import io.localhost.freelancer.statushukum.networking.NetworkRequestQueue;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.util> created by :
 * Name         : syafiq
 * Date / Time  : 14 December 2016, 9:20 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */
public class Setting
{
    public static final String CLASS_NAME = "Setting";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.util.Setting";
    public static final int SYNC_FAILED = 0;
    public static final int SYNC_SUCCESS = 1;
    public static final int SYNC_EQUAL = 2;


    public static final DateTimeFormatter timeStampFormat = DateTimeFormat.forPattern(DatabaseHelper.TIMESTAMP_FORMAT);
    private static Setting ourInstance;

    private final Context context;
    private Observer syncObserve;

    private Setting(Context context)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.context = context;
    }

    public static Setting getInstance(final Context context)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getInstance");

        if(Setting.ourInstance == null)
        {
            Setting.ourInstance = new Setting(context);
        }
        return Setting.ourInstance;
    }

    public synchronized void doSync(final Observer message)
    {
        this.syncObserve = message;
        this.getServerVersion(new TaskDelegatable()
        {
            @Override
            public void delegate(Object... data)
            {
                if(data[0] instanceof JSONObject)
                {
                    try
                    {
                        Setting.this.getDBVersion(LocalDateTime.parse(((JSONObject) data[0]).getString("timestamp"), timeStampFormat), new TaskDelegatable()
                        {
                            @Override
                            public void delegate(Object... data)
                            {
                                Setting.this.getStreamData((LocalDateTime) data[0], (LocalDateTime) data[1], new TaskDelegatable()
                                {
                                    @Override
                                    public void delegate(final Object... data)
                                    {
                                        new AsyncTask<Void, Void, Void>()
                                        {

                                            @Override
                                            protected Void doInBackground(Void... params)
                                            {
                                                MDM_Data dataModel = MDM_Data.getInstance(Setting.this.context);
                                                dataModel.deleteAll();
                                                Setting.this.insertData((JSONArray) data[0]);
                                                MDM_Tag tagModel = MDM_Tag.getInstance(Setting.this.context);
                                                tagModel.deleteAll();
                                                Setting.this.insertTag((JSONArray) data[1]);
                                                MDM_DataTag dataTagModel = MDM_DataTag.getInstance(Setting.this.context);
                                                dataTagModel.deleteAll();
                                                Setting.this.insertDataTag((JSONArray) data[2]);
                                                MDM_Version versionModel = MDM_Version.getInstance(Setting.this.context);
                                                versionModel.deleteAll();
                                                Setting.this.insertVersion((JSONArray) data[3]);
                                                Setting.this.syncObserve.update(null, SYNC_SUCCESS);
                                                return null;
                                            }
                                        }.execute();
                                    }
                                });
                            }
                        });
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void insertVersion(JSONArray version)
    {
        MDM_Version versionTag = MDM_Version.getInstance(this.context);
        for(int i = -1, is = version.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = version.getJSONObject(i);
                versionTag.insert(
                        entry.getInt("id"),
                        entry.getString("timestamp"));
            }
            catch(JSONException ignored)
            {
                Log.i(CLASS_NAME, "JSONException");
            }
        }
    }

    private synchronized void insertTag(JSONArray tag)
    {
        MDM_Tag dataTag = MDM_Tag.getInstance(this.context);
        for(int i = -1, is = tag.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = tag.getJSONObject(i);
                dataTag.insert(
                        entry.getInt("id"),
                        entry.getString("name"),
                        entry.getString("description"),
                        entry.getString("color"),
                        entry.getString("colortext"));
            }
            catch(JSONException ignored)
            {
                Log.i(CLASS_NAME, "JSONException");
            }
        }
    }

    private synchronized void insertDataTag(JSONArray datatag)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".populateDataTagTable");

        MDM_DataTag dataTagModel = MDM_DataTag.getInstance(this.context);
        for(int i = -1, is = datatag.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = datatag.getJSONObject(i);
                dataTagModel.insert(
                        entry.getInt("data"),
                        entry.getInt("tag"));
            }
            catch(JSONException ignored)
            {
                Log.i(CLASS_NAME, "JSONException");
            }
        }
    }

    private synchronized void insertData(JSONArray data)
    {
        MDM_Data dataModel = MDM_Data.getInstance(this.context);
        for(int i = -1, is = data.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = data.getJSONObject(i);
                dataModel.insert(
                        entry.getInt("id"),
                        entry.getInt("year"),
                        entry.getString("no"),
                        entry.getString("description"),
                        entry.getString("status"),
                        entry.getInt("category"),
                        entry.getString("reference"));
            }
            catch(JSONException ignored)
            {

            }
        }
    }

    private synchronized void getStreamData(final LocalDateTime serverVersion, final LocalDateTime dbVersion, final TaskDelegatable delegatable)
    {

        if(serverVersion.isEqual(dbVersion))
        {
            Setting.this.syncObserve.update(null, SYNC_EQUAL);
            return;
        }
        String url = null;
        try
        {
            url = NetworkConstants.API_SITE_URL + "/api/stream?from=" + URLEncoder.encode(dbVersion.toString(timeStampFormat), "UTF-8") + "&to=" + URLEncoder.encode(serverVersion.toString(timeStampFormat), "UTF-8");
        }
        catch(UnsupportedEncodingException ignored)
        {

            this.syncObserve.update(null, SYNC_FAILED);
            return;
        }
        @NotNull
        final JsonObjectRequest request = new JsonObjectRequest
                (
                        Request.Method.GET,
                        url,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                if(response.has("data"))
                                {
                                    try
                                    {
                                        response = response.getJSONObject("data");
                                        if(response.has("data") && response.has("tag") && response.has("datatag"))
                                        {
                                            delegatable.delegate(response.getJSONArray("data"), response.getJSONArray("tag"), response.getJSONArray("datatag"), response.getJSONArray("version"));
                                            return;
                                        }
                                    }
                                    catch(JSONException ignored)
                                    {

                                    }
                                }
                                Setting.this.syncObserve.update(null, SYNC_FAILED);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError ignored)
                            {

                                Setting.this.syncObserve.update(null, SYNC_FAILED);
                            }
                        }
                )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                @NotNull
                final Map<String, String> headers = new LinkedHashMap<>(super.getHeaders());
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("X-Request-Access-Certificate", "cb2f4b3f8af65c661f71bef52cb80f4fd2590c0c7e45810573e93d4645cf0b8eca978af15b953dfbc5db2dda33ad66e02477f647e32b5b72a467e3b9f63bcb7e");
                return headers;
            }
        };

        NetworkRequestQueue.getInstance(this.context).addToRequestQueue(request);

    }

    private synchronized void getDBVersion(LocalDateTime serverVersion, TaskDelegatable delegatable)
    {

        if(serverVersion == null)
        {
            this.syncObserve.update(null, SYNC_FAILED);
            return;
        }
        else
        {
            final MDM_Version versionData = MDM_Version.getInstance(this.context);
            LocalDateTime defaultTimeStamp = LocalDateTime.parse("2000-01-01 00:00:00", timeStampFormat);
            final LocalDateTime latestData = versionData.getVersion();
            if((latestData != null) && defaultTimeStamp.isBefore(latestData))
            {
                defaultTimeStamp = latestData;
            }
            delegatable.delegate(serverVersion, defaultTimeStamp);
        }
    }

    private synchronized void getServerVersion(final TaskDelegatable delegatable)
    {

        String url = NetworkConstants.API_SITE_URL + "/api/latest";
        @NotNull
        final JsonObjectRequest request = new JsonObjectRequest
                (
                        Request.Method.GET,
                        url,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                if(response.has("data"))
                                {
                                    try
                                    {
                                        response = response.getJSONObject("data");
                                        if(response.has("timestamp"))
                                        {
                                            delegatable.delegate(response);
                                            return;
                                        }
                                    }
                                    catch(JSONException ignored)
                                    {

                                    }
                                }
                                Setting.this.syncObserve.update(null, SYNC_FAILED);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError ignored)
                            {

                                Setting.this.syncObserve.update(null, SYNC_FAILED);
                            }
                        }
                )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                @NotNull
                final Map<String, String> headers = new LinkedHashMap<>(super.getHeaders());
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("X-Request-Access-Certificate", "cb2f4b3f8af65c661f71bef52cb80f4fd2590c0c7e45810573e93d4645cf0b8eca978af15b953dfbc5db2dda33ad66e02477f647e32b5b72a467e3b9f63bcb7e");
                return headers;
            }
        };

        NetworkRequestQueue.getInstance(this.context).addToRequestQueue(request);
    }

    private interface TaskDelegatable
    {
        void delegate(Object... data);
    }
}
