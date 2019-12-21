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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.context = context;
        this.social = new Social();
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

                reactive =subject.throttleFirst(100L, TimeUnit.MILLISECONDS)
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

                                        getStreamData(_next -> {
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
                                                syncMessage.setIndeterminate(true);
                                                syncMessage.setMessage("Process data");
                                                publishProgress(syncMessage);

                                                new Handler().postDelayed(() -> {
                                                    publishProgress(SYNC_SUCCESS);
                                                }, 3000);
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




        /*Log.i(CLASS_NAME, CLASS_PATH + ".doSync");

        return new AirtableDataFetcherTask(NetworkRequestQueue.getInstance(activity).getRequestQueue()) {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();


            }

            @Override
            protected AirtableDataFetcher doInBackground(Void... voids) {

                AirtableDataFetcher airtableDataFetcher =  super.doInBackground(voids);
                if (airtableDataFetcher == null || airtableDataFetcher.ex != null) {
                    if (this.isCancelled())
                        publishProgress(SYNC_CANCELLED);
                    else
                        publishProgress(SYNC_FAILED);
                } else {
                    MDM_Data dataModel = MDM_Data.getInstance(activity);
                    dataModel.deleteAll();

                    JSONArray data = airtableDataFetcher.getData();



                    for (int i = -1, is = data.length(); ++i < is; ) {
                        try {
                            subject.onNext(i + 1);
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
                            Log.i(CLASS_NAME, "JSONException");
                        }
                    }

                    MDM_Tag tagModel = MDM_Tag.getInstance(activity);
                    tagModel.deleteAll();
                    MDM_DataTag dataTagModel = MDM_DataTag.getInstance(activity);
                    dataTagModel.deleteAll();
                    publishProgress(SYNC_SUCCESS);
                    reactive.dispose();
                }
                return airtableDataFetcher;
            }

            @Override

        };*/
    }

    private static synchronized void getStreamData(final TaskDelegatable subject, final VersionEntity serverVersion, final TaskDelegatable delegatable)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getStreamData");
        StorageReference islandRef = FirebaseStorage.getInstance().getReference("stream/"+serverVersion.milis+".json");

        Executor x = Executors.newSingleThreadExecutor();
        islandRef.getStream()
                .addOnSuccessListener(x, stream -> {
                    try {
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        int nRead;
                        byte[] data = new byte[1024];
                        while ((nRead = stream.getStream().read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                        }
                        buffer.flush();
                        JSONObject response = new JSONObject(new String(buffer.toByteArray()));
                        buffer.close();
                        if (response.has("data")) {
                            try {
                                response = response.getJSONObject("data");
                                if (response.has("data") && response.has("tag") && response.has("datatag") && response.has("version")) {
                                    delegatable.delegate(response.getJSONArray("data"), response.getJSONArray("tag"), response.getJSONArray("datatag"), response.getJSONArray("version"));
                                }
                            } catch (JSONException ignored) {
                                Log.e(CLASS_NAME, "JSONException", ignored);
                            }
                        }
                    } catch (JSONException | IOException ignored) {
                        Log.e(CLASS_NAME, "JSONException", ignored);
                    }
                    delegatable.delegate(SYNC_FAILED);
                })
                .addOnFailureListener(exception -> {
                    delegatable.delegate(SYNC_FAILED);
                })
                .addOnProgressListener(x, taskSnapshot -> {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //displaying percentage in progress dialog
                    Log.d("Uploaded ", ((int) progress) + "%...");
                });
    }

    private static synchronized void getDBVersion(Context context, VersionEntity serverVersion, final TaskDelegatable delegatable)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getDBVersion");
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
        Log.i(CLASS_NAME, CLASS_PATH + ".getServerVersion");

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
                Log.i(CLASS_NAME, "onErrorResponse");
                delegatable.delegate(SYNC_FAILED);
            }
        });
    }

    private interface TaskDelegatable
    {
        void delegate(Object... data);
    }
}
