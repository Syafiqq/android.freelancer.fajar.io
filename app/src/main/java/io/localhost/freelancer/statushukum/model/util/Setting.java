package io.localhost.freelancer.statushukum.model.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observer;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.firebase.entity.VersionEntity;
import io.localhost.freelancer.statushukum.model.AirtableDataFetcher;
import io.localhost.freelancer.statushukum.model.AirtableDataFetcherTask;
import io.localhost.freelancer.statushukum.model.database.DatabaseHelper;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Version;
import io.localhost.freelancer.statushukum.networking.NetworkRequestQueue;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

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
    public static final int SYNC_CANCELLED = 3;


    public static final DateTimeFormatter timeStampFormat = DateTimeFormat.forPattern(DatabaseHelper.TIMESTAMP_FORMAT);
    private static Setting ourInstance;

    private final Context context;
    public Social social;

    private Setting(Context context)
    {


        this.context = context;
        this.social = new Social();
    }

    public static Setting getInstance(final Context context)
    {


        if(Setting.ourInstance == null)
        {
            Setting.ourInstance = new Setting(context);
        }
        return Setting.ourInstance;
    }

    public static synchronized AsyncTask<Void, Object, Void> doSync(final Context context, final Runnable onSuccess, final Runnable onFailed, final Runnable onComplete, Observer onUpdate, final Activity activity, CompositeDisposable disposable)
    {
        return new AsyncTask<Void, Object, Void>() {
            Disposable reactive;
            PublishSubject<Integer> subject;
            SyncMessage syncMessage = new SyncMessage();
            Observer callback;

            @Override
            protected void onPreExecute() {
                callback = (observable, o) -> activity.runOnUiThread(() -> {
                    switch ((Integer) o) {
                        case Setting.SYNC_FAILED: {
                            Toast.makeText(activity, activity.getString(R.string.system_setting_server_version_error), Toast.LENGTH_SHORT).show();
                            if (onFailed != null)
                                onFailed.run();
                        }
                        break;
                        case Setting.SYNC_SUCCESS: {
                            Toast.makeText(activity, activity.getString(R.string.system_setting_server_version_success), Toast.LENGTH_SHORT).show();
                            if (onSuccess != null)
                                onSuccess.run();
                        }
                        break;
                        case Setting.SYNC_EQUAL: {
                            Toast.makeText(activity, activity.getString(R.string.system_setting_server_version_equal), Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case Setting.SYNC_CANCELLED: {
                            Toast.makeText(activity, "cancel", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    if (onComplete != null)
                        onComplete.run();
                });

                subject = PublishSubject.create();

                reactive =subject.throttleFirst(1000L, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            try {
                                if(syncMessage == null) return;
                                syncMessage.setCurrent(integer);
                                publishProgress(syncMessage);
                            } catch (Exception ignored) {

                            }
                        });
                disposable.add(reactive);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                syncMessage.setIndeterminate(true);
                syncMessage.setMessage("Check Version");
                publishProgress(syncMessage);

                getServerVersion(versions -> {
                    if(versions.length > 0)
                    {
                        if(versions[0] instanceof VersionEntity)
                        {
                            getDBVersion(context, (VersionEntity) versions[0], (_versions) -> {
                                if(_versions.length > 1 && _versions[0] instanceof VersionEntity && _versions[1] instanceof LocalDateTime)
                                {
                                    LocalDateTime _serverVersion = LocalDateTime.parse(((VersionEntity) _versions[0]).timestamp, timeStampFormat);
                                    if(_serverVersion.isEqual((LocalDateTime) _versions[1]))
                                    {
                                        publishProgress(SYNC_EQUAL);
                                    }
                                    else
                                    {
                                        syncMessage.setIndeterminate(false);
                                        syncMessage.setMessage("Downloading data");
                                        syncMessage.setMax(100);
                                        syncMessage.setCurrent(0);
                                        publishProgress(syncMessage);

                                        getStreamData(context, _next -> {
                                            if(_next.length > 0 && _next[0] instanceof Integer)
                                            {
                                                subject.onNext((Integer) _next[0]);
                                            }
                                        }, (VersionEntity) _versions[0], _data -> {
                                            if(_data.length > 3
                                                    && _data[0] instanceof JSONArray
                                                    && _data[1] instanceof JSONArray
                                                    && _data[2] instanceof JSONArray
                                                    && _data[3] instanceof JSONArray)
                                            {
                                                syncMessage.setMessage("Process data");
                                                publishProgress(syncMessage);
                                                parseData(context, _next1 -> {
                                                    if (_next1.length > 0 && _next1[0] instanceof Integer) {
                                                        subject.onNext((Integer) _next1[0]);
                                                    }
                                                }, (JSONArray) _data[0], (JSONArray) _data[1], (JSONArray) _data[2], (JSONArray) _data[3], _finalizing -> {
                                                    if (_finalizing.length > 0 && _finalizing[0] instanceof Integer) {
                                                        publishProgress((Integer) _finalizing[0]);
                                                    }
                                                });
                                            }
                                            else if(_data.length > 0 && _data[0] instanceof Integer)
                                            {
                                                publishProgress(_versions[0]);
                                            }
                                            else
                                            {
                                                publishProgress(SYNC_FAILED);
                                            }
                                        });
                                    }
                                }
                                else if(_versions.length > 0 && _versions[0] instanceof Integer)
                                {
                                    publishProgress(_versions[0]);
                                }
                                else
                                {
                                    publishProgress(SYNC_FAILED);
                                }
                            });
                        }
                        else if(versions[0] instanceof Integer)
                        {
                            publishProgress(versions[0]);
                        }
                        else
                        {
                            publishProgress(SYNC_FAILED);
                        }
                    }
                    else
                    {
                        publishProgress(SYNC_FAILED);
                    }
                });
                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                if(values == null || values.length <= 0) return;
                else if(values[0] instanceof SyncMessage) {
                    onUpdate.update(null, values[0]);
                }
                else if(values[0] instanceof Integer) {
                    callback.update(null, values[0]);
                }
            }
        };
    }

    private static synchronized void parseData(final Context context, final TaskDelegatable subject, JSONArray data, JSONArray tag, JSONArray dataTag, JSONArray version, final TaskDelegatable delegatable) {
        try
        {
            MDM_Data dataModel = MDM_Data.getInstance(context);
            dataModel.deleteAll();
            int is = data.length();
            for (int i = -1; ++i < is; ) {
                try {
                    subject.delegate((int) (75 + ((i * 100.0 / is) * 0.25)));
                    final JSONObject entry = data.getJSONObject(i);
                    dataModel.insert(
                            entry.getInt("id"),
                            entry.getInt("year"),
                            entry.getString("no"),
                            entry.getString("description"),
                            entry.getString("status"),
                            entry.getInt("category"),
                            entry.getString("reference"));
                } catch (JSONException ignored) {

                }
            }
            insertTag(context, tag);
            insertDataTag(context, dataTag);
            insertVersion(context, version);
            delegatable.delegate(SYNC_SUCCESS);
        }
        catch (Exception e)
        {

            delegatable.delegate(SYNC_FAILED);
        }
    }

    private static synchronized void getStreamData(final Context context, final TaskDelegatable subject, final VersionEntity serverVersion, final TaskDelegatable delegatable)
    {

        StorageReference islandRef = FirebaseStorage.getInstance().getReference("stream/"+serverVersion.milis+".json");

        File tmp = new File(context.getFilesDir(), "updates.json");
        islandRef.getFile(tmp)
                .addOnSuccessListener(stream -> {
                    new AsyncTask<Void, Void, Object>() {
                        @Override
                        protected Object doInBackground(Void... voids) {
                            try {
                                String text = null;
                                if(tmp.exists())
                                    text = getStringFromFile(tmp.getAbsolutePath());
                                if(text == null) {
                                    delegatable.delegate(SYNC_FAILED);
                                    return null;
                                }
                                JSONObject response = new JSONObject(text);
                                if (response.has("data")) {
                                    try {
                                        response = response.getJSONObject("data");
                                        if (response.has("data") && response.has("tag") && response.has("datatag") && response.has("version")) {
                                            delegatable.delegate(response.getJSONArray("data"), response.getJSONArray("tag"), response.getJSONArray("datatag"), response.getJSONArray("version"));
                                            tmp.delete();
                                            return null;
                                        }
                                    } catch (JSONException ignored) {

                                    }
                                }
                            } catch (JSONException | IOException ignored) {

                            }
                            tmp.delete();
                            delegatable.delegate(SYNC_FAILED);
                            return null;
                        }
                    }.execute();
                })
                .addOnFailureListener(exception -> {
                    delegatable.delegate(SYNC_FAILED);
                })
                .addOnProgressListener(taskSnapshot -> {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount() * 0.7;
                    //displaying percentage in progress dialog
                    subject.delegate((int) progress);
                });
    }

    private static synchronized void getDBVersion(Context context, VersionEntity serverVersion, final TaskDelegatable delegatable)
    {

        if(serverVersion == null)
        {
            delegatable.delegate(SYNC_FAILED);
        }
        else
        {
            final MDM_Version versionData = MDM_Version.getInstance(context);
            LocalDateTime defaultTimeStamp = LocalDateTime.parse("2000-01-01 00:00:00", timeStampFormat);
            final LocalDateTime latestData = versionData.getVersion();
            if((latestData != null) && defaultTimeStamp.isBefore(latestData))
            {
                defaultTimeStamp = latestData;
            }
            delegatable.delegate(serverVersion, defaultTimeStamp);
        }
    }

    private static synchronized void getServerVersion(final TaskDelegatable delegatable)
    {


        FirebaseDatabase.getInstance().getReference("versions").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0)
                {
                    VersionEntity serverVersion = dataSnapshot.getChildren().iterator().next().getValue(VersionEntity.class);
                    if(serverVersion != null)
                    {
                        delegatable.delegate(serverVersion);
                        return;
                    }
                    delegatable.delegate(SYNC_FAILED);
                }
                else {
                    delegatable.delegate(SYNC_FAILED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                delegatable.delegate(SYNC_FAILED);
            }
        });
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        // http://www.java2s.com/Code/Java/File-Input-Output/ConvertInputStreamtoString.htm
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        Boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if(firstLine){
                sb.append(line);
                firstLine = false;
            } else {
                sb.append("\n").append(line);
            }
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws IOException {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    private static synchronized void insertVersion(Context context, JSONArray version)
    {
        MDM_Version versionTag = MDM_Version.getInstance(context);
        versionTag.deleteAll();
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

            }
        }
    }

    private static  synchronized void insertTag(Context context, JSONArray tag)
    {
        MDM_Tag dataTag = MDM_Tag.getInstance(context);
        dataTag.deleteAll();
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

            }
        }
    }

    private static  synchronized void insertDataTag(Context context, JSONArray datatag)
    {


        MDM_DataTag dataTagModel = MDM_DataTag.getInstance(context);
        dataTagModel.deleteAll();
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

            }
        }
    }

    private interface TaskDelegatable
    {
        void delegate(Object... data);
    }
}
