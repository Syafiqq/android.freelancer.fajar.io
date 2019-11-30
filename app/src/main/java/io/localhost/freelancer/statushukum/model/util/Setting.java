package io.localhost.freelancer.statushukum.model.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observer;
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
    private Observer syncObserve;

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

    public static void sendFeedback(Context context)
    {
        final Resources resources = context.getResources();
        final Intent mailto = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        mailto.putExtra(Intent.EXTRA_EMAIL, new String[] {resources.getString(R.string.activity_setting_mailto_receipt)});
        mailto.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.activity_setting_mailto_subject));
        mailto.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.activity_setting_mailto_content));
        context.startActivity(Intent.createChooser(mailto, "Send Feedback:"));
    }

    public static synchronized AsyncTask<Void, Object, AirtableDataFetcher> doSync(final Runnable onSuccess, final Runnable onFailed, final Runnable onComplete, Observer onUpdate, final Activity activity, CompositeDisposable disposable)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".doSync");

        return new AirtableDataFetcherTask(NetworkRequestQueue.getInstance(activity).getRequestQueue()) {
            private Disposable reactive;
            private PublishSubject<Integer> subject;
            SyncMessage syncMessage = new SyncMessage();
            private Observer callback;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

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
            protected AirtableDataFetcher doInBackground(Void... voids) {
                syncMessage.setIndeterminate(true);
                syncMessage.setMessage("Download Data");
                publishProgress(syncMessage);

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

                    syncMessage.setIndeterminate(false);
                    syncMessage.setMessage("Update Data");
                    syncMessage.setMax(data.length());
                    syncMessage.setCurrent(0);
                    publishProgress(syncMessage);

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

    public synchronized void doSync(Observer message, Observer onUpdate)
    {

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
                Log.i(CLASS_NAME, "JSONException");
            }
        }
    }

    private synchronized void getStreamData(final VersionEntity serverVersion, final LocalDateTime dbVersion, final TaskDelegatable delegatable)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getStreamData");
        LocalDateTime _serverVersion = LocalDateTime.parse(serverVersion.timestamp, timeStampFormat);
        if(_serverVersion.isEqual(dbVersion))
        {
            Setting.this.syncObserve.update(null, SYNC_EQUAL);
            return;
        }
        StorageReference islandRef = FirebaseStorage.getInstance().getReference("stream/"+serverVersion.milis+".json");

        final long ONE_MEGABYTE = 10 * 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            try {
                JSONObject response = new JSONObject(new String(bytes));
                if(response.has("data"))
                {
                    try
                    {
                        response = response.getJSONObject("data");
                        if(response.has("data") && response.has("tag") && response.has("datatag") && response.has("version"))
                        {
                            delegatable.delegate(response.getJSONArray("data"), response.getJSONArray("tag"), response.getJSONArray("datatag"), response.getJSONArray("version"));
                            return;
                        }
                    }
                    catch(JSONException ignored)
                    {
                        Log.i(CLASS_NAME, "JSONException");
                    }
                }
            } catch (JSONException ignored) {
            }
            Setting.this.syncObserve.update(null, SYNC_FAILED);
        }).addOnFailureListener(exception -> {
            Setting.this.syncObserve.update(null, SYNC_FAILED);
        });
    }

    private synchronized void getDBVersion(VersionEntity serverVersion, TaskDelegatable delegatable)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".getDBVersion");
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
                    Setting.this.syncObserve.update(null, SYNC_FAILED);
                }
                else {
                    Setting.this.syncObserve.update(null, SYNC_FAILED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(CLASS_NAME, "onErrorResponse");
                Setting.this.syncObserve.update(null, SYNC_FAILED);
            }
        });
    }

    private interface TaskDelegatable
    {
        void delegate(Object... data);
    }
}
