package io.localhost.freelancer.statushukum.model.util;

import android.content.Context;
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
    public static final String CLASS_NAME   = "Setting";
    public static final String CLASS_PATH   = "io.localhost.freelancer.statushukum.model.util.Setting";
    public static final int    SYNC_FAILED  = 0;
    public static final int    SYNC_SUCCESS = 1;

    public static final DateTimeFormatter timeStampFormat = DateTimeFormat.forPattern(DatabaseHelper.TIMESTAMP_FORMAT);
    private static Setting ourInstance;

    private final Context  context;
    private       boolean  isAllShowed;
    private       Observer sycnObserve;

    private Setting(Context context)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.context = context;
        this.isAllShowed = false;
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

    public boolean isAllShowed()
    {
        return this.isAllShowed;
    }

    public void setAllShowed(boolean allShowed)
    {
        this.isAllShowed = allShowed;
    }

    public synchronized void doSync(final Observer message)
    {
        this.sycnObserve = message;
        this.getServerVersion();
    }

    private void getStreamData(final LocalDateTime serverVersion, final LocalDateTime dbVersion)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getStreamData");
        Log.i(CLASS_NAME, serverVersion.toString(timeStampFormat) + " " + dbVersion.toString(timeStampFormat));
        String url = null;
        try
        {
            url = NetworkConstants.API_SITE_URL + "/api/stream?from=" + URLEncoder.encode(dbVersion.toString(timeStampFormat), "UTF-8") + "&to=" + URLEncoder.encode(serverVersion.toString(timeStampFormat), "UTF-8");
        }
        catch(UnsupportedEncodingException ignored)
        {
            Log.i(CLASS_NAME, "UnsupportedEncodingException");
            this.sycnObserve.update(null, SYNC_FAILED);
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
                                            Setting.this.syncData(response.getJSONArray("data"));
                                            Setting.this.syncDataTag(response.getJSONArray("datatag"));
                                            Setting.this.syncTag(response.getJSONArray("tag"));
                                            Setting.this.sycnObserve.update(null, SYNC_SUCCESS);
                                            return;
                                        }
                                    }
                                    catch(JSONException ignored)
                                    {
                                        Log.i(CLASS_NAME, "JSONException");
                                    }
                                }
                                Setting.this.sycnObserve.update(null, SYNC_FAILED);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError ignored)
                            {
                                Log.i(CLASS_NAME, "onErrorResponse");
                                Setting.this.sycnObserve.update(null, SYNC_FAILED);
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

    private void syncTag(JSONArray tag)
    {
        MDM_Tag dataTag = MDM_Tag.getInstance(this.context);
        for(int i = -1, is = tag.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = tag.getJSONObject(i);
                dataTag.insertOrUpdate(
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

    private void syncDataTag(JSONArray datatag)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".populateDataTagTable");

        MDM_DataTag dataTagModel = MDM_DataTag.getInstance(this.context);
        dataTagModel.deleteAll();
        for(int i = -1, is = datatag.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = datatag.getJSONObject(i);
                dataTagModel.insert(
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

    private void syncData(final JSONArray data)
    {
        MDM_Data dataModel = MDM_Data.getInstance(this.context);
        for(int i = -1, is = data.length(); ++i < is; )
        {
            try
            {
                final JSONObject entry = data.getJSONObject(i);
                dataModel.insertOrUpdate(
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

    private void getDBVersion(LocalDateTime serverVersion)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getDBVersion");
        if(serverVersion == null)
        {
            this.sycnObserve.update(null, SYNC_FAILED);
            return;
        }
        else
        {
            final MDM_Data      modelData        = MDM_Data.getInstance(this.context);
            final MDM_Tag       modelTag         = MDM_Tag.getInstance(this.context);
            LocalDateTime       defaultTimeStamp = LocalDateTime.parse("2000-01-01 00:00:00", timeStampFormat);
            final LocalDateTime latestData       = modelData.getLatestTimestamp();
            final LocalDateTime latestTag        = modelTag.getLatestTimestamp();
            if((latestData != null) && defaultTimeStamp.isBefore(latestData))
            {
                defaultTimeStamp = latestData;
            }
            if((latestTag != null) && defaultTimeStamp.isBefore(latestTag))
            {
                defaultTimeStamp = latestTag;
            }
            this.getStreamData(serverVersion, defaultTimeStamp);
        }
    }

    private void getServerVersion()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getServerVersion");
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
                                            Setting.this.getDBVersion(LocalDateTime.parse(response.getString("timestamp"), timeStampFormat));
                                            return;
                                        }
                                    }
                                    catch(JSONException ignored)
                                    {
                                        Log.i(CLASS_NAME, "JSONException");
                                    }
                                }
                                Setting.this.sycnObserve.update(null, SYNC_FAILED);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError ignored)
                            {
                                Log.i(CLASS_NAME, "onErrorResponse");
                                Setting.this.sycnObserve.update(null, SYNC_FAILED);
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

    public synchronized void doDeepSync()
    {

    }
}
