package io.localhost.freelancer.statushukum.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdf.viewer.DocumentActivity;
import com.google.android.material.snackbar.Snackbar;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;
import io.localhost.freelancer.statushukum.model.entity.ME_Data;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;
import io.localhost.freelancer.statushukum.model.util.HtmlUtil;
import io.localhost.freelancer.statushukum.model.util.ME_TagAdapter;
import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

class DownloadHolder {
    public String realFilename;
    public String downloadFilename;

    public DownloadHolder(String realFilename, String downloadFilename) {
        this.realFilename = realFilename;
        this.downloadFilename = downloadFilename;
    }
}

public class Detail extends AppCompatActivity
{
    public static final String CLASS_NAME = "Detail";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Detail";
    public static final String EXTRA_ID = "id";
    private int id;
    private HtmlTextView no;
    private HtmlTextView description;
    private HtmlTextView status;
    private TagView tag;
    private TextView tagLabel;
    private Button download;
    private Button open;
    private long downloadID;
    private String path;
    private String filename;

    private HashMap<String, DownloadHolder> fileMapper = new HashMap<>();
    private HashMap<String, Runnable> mPendingPermissionRequests = new HashMap<>();
    private static final int PENDING_PERMISSION_REQUESTS = 0;

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(referenceId == Detail.this.downloadID && fileMapper.containsKey(String.valueOf(referenceId)))
            {
                DownloadHolder holder = fileMapper.remove(String.valueOf(referenceId));
                final String tmpPath = String.format(Locale.getDefault(), "%s/%s/reference/tmp/%s", Detail.super.getExternalFilesDir("").toString(), Environment.DIRECTORY_DOWNLOADS, holder.downloadFilename);
                final File oldFile = new File(holder.realFilename);
                final File newFile = new File(tmpPath);
                if(!newFile.exists()){
                    Toast.makeText(Detail.this, "File gagal diunduh", Toast.LENGTH_SHORT).show();
                    Detail.this.download.setEnabled(true);
                    return;
                }

                if(oldFile.exists())
                {
                    oldFile.delete();
                }
                newFile.renameTo(oldFile);
                Detail.this.download.setEnabled(true);
                Detail.this.checkOfflineData();
                Toast.makeText(Detail.this, "File Berhasil Diunduh", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        this.setDetail();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_detail);
        final Intent intent = getIntent();
        this.id = intent.getIntExtra(Detail.EXTRA_ID, -1);

        this.setToolbar();
        this.setProperty();
        this.setDetail();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(downloadReceiver);
        super.onDestroy();
    }

    private void setProperty()
    {
        this.no = (HtmlTextView) super.findViewById(R.id.content_detail_htv_no);
        this.description = (HtmlTextView) super.findViewById(R.id.content_detail_htv_description);
        this.status = (HtmlTextView) super.findViewById(R.id.content_detail_htv_status);
        this.tag = (TagView) super.findViewById(R.id.content_detail_tv_tag);
        this.tagLabel = (TextView) super.findViewById(R.id.content_detail_tv_tag_label);
        this.download = (Button) super.findViewById(R.id.content_detail_button_download);
        this.open = (Button) super.findViewById(R.id.content_detail_button_open);
        this.tag.setOnTagClickListener(new OnTagClickListener()
        {
            @Override
            public void onTagClick(int i, Tag tag)
            {
                if(tag instanceof ME_TagAdapter)
                {
                    Toast.makeText(Detail.this, ((ME_TagAdapter) tag).description, Toast.LENGTH_SHORT).show();
                }
            }
        });
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    private void setToolbar()
    {


        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_detail_toolbar);
        super.setSupportActionBar(toolbar);
        final ActionBar actionBar = super.getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            toolbar.setContentInsetStartWithNavigation(4);
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_white_24));
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Detail.this.onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {


        getMenuInflater().inflate(R.menu.activity_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        switch(item.getItemId())
        {
            case R.id.activity_detail_menu_setting:
            {
                startActivity(new Intent(this, Setting.class));
                return true;
            }
            case android.R.id.home:
                //perhaps use intent if needed but i'm sure there's a specific intent action for up you can use to handle
                Detail.this.onBackButtonPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBackButtonPressed()
    {


        this.onBackPressed();
    }

    @Override
    public void onBackPressed()
    {


        super.finish();
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("ConstantConditions")
    private void setDetail()
    {


        new AsyncTask<Void, Void, Void>()
        {

            List<Integer> dbResultTagID;
            Map<Integer, ME_Tag> dbResultTag;
            ME_Data dbResultData;

            @Override
            protected Void doInBackground(Void... voids)
            {
                final MDM_Data modelData = MDM_Data.getInstance(Detail.this);
                final MDM_DataTag modelDataTag = MDM_DataTag.getInstance(Detail.this);
                final MDM_Tag modelTag = MDM_Tag.getInstance(Detail.this);
                this.dbResultData = modelData.getFromID(Detail.this.id);
                this.dbResultTag = modelTag.getAll();
                this.dbResultTagID = modelDataTag.getTagFromDataID(Detail.this.id);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                Detail.this.tag.removeAllTags();
                if(this.dbResultData != null)
                {
                    Detail.this.no.setHtml(HtmlUtil.sanitizeHtml(this.dbResultData.getNo()));
                    Detail.this.description.setHtml(HtmlUtil.sanitizeHtml(this.dbResultData.getDescription()));
                    Detail.this.status.setHtml(HtmlUtil.sanitizeHtml(this.dbResultData.getStatus()));
                    Detail.this.checkFileStatus(dbResultData);
                    if(!dbResultTagID.isEmpty())
                    {
                        Detail.this.tag.setVisibility(View.VISIBLE);
                        Detail.this.tagLabel.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Detail.this.tag.setVisibility(View.GONE);
                        Detail.this.tagLabel.setVisibility(View.GONE);
                    }
                    while(!dbResultTagID.isEmpty())
                    {
                        Detail.this.tag.addTag(new ME_TagAdapter(dbResultTag.get(dbResultTagID.remove(0)), 12f));
                    }
                }
                else
                {
                    Detail.this.no.setText("-");
                    Detail.this.description.setText("-");
                    Detail.this.status.setText("-");
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void checkFileStatus(final ME_Data dbResultData)
    {
        this.filename = dbResultData.getNo().trim().replaceAll(" ", "_");
        this.filename = filename.replaceAll("[\\p{Punct}&&[^_]]+", "");
        this.path = String.format(Locale.getDefault(), "%s/%s/reference/%s", super.getExternalFilesDir("").toString(), Environment.DIRECTORY_DOWNLOADS, filename);
        final File dlc = new File(path);

        if(!dbResultData.getReference().equalsIgnoreCase("null"))
        {
            final String finalFilename = filename;
            Detail.this.download.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    newPermissionRequester(
                            Detail.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,             // permission to request
                            "Kami membutuhkan permission tersebut untuk menyimpan file",  // explanation to user
                            () -> doDownload(dbResultData.getReference(), path)
                    ).run();
                }
            });
            Detail.this.download.setVisibility(View.VISIBLE);
        }
        else
        {
            Detail.this.download.setVisibility(View.GONE);
        }
        Detail.this.open.setVisibility(View.GONE);
        if(dlc.exists() && (!dbResultData.getReference().equalsIgnoreCase("null")))
        {
            this.checkOfflineData();
        }
    }

    private void doDownload(String url, String filename) {
        final Uri uri = Uri.parse(url);
        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Download");

        //Setting description of request
        request.setDescription("Mohon Tunggu");
        String _filename = generateRandomString(32);

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(Detail.this, Environment.DIRECTORY_DOWNLOADS + "/reference/tmp", _filename);

        //Enqueue download and save the referenceId
        Detail.this.download.setEnabled(false);
        Detail.this.downloadID = downloadManager.enqueue(request);
        fileMapper.put(String.valueOf(downloadID), new DownloadHolder(filename, _filename));
    }

    private void checkOfflineData()
    {
        if(this.path != null)
        {
            Detail.this.open.setVisibility(View.VISIBLE);
            Detail.this.download.setVisibility(View.VISIBLE);
            Detail.this.open.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        //File newFile = new File(path);
                        //Uri uri = FileProvider.getUriForFile(Detail.this, getApplicationContext().getPackageName() + ".provider", newFile);
                        Uri uri = Uri.fromFile(new File(path));
                        Detail.this.grantUriPermission(getApplicationContext().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        Intent intent = new Intent(Detail.this, DocumentActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                    catch(Exception e)
                    {

                        Toast.makeText(Detail.this, "Cannot parse file", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onPostResume()
    {

        this.setDetail();

        super.onPostResume();
    }

    private Runnable newPermissionRequester(Activity activity, String permission, String explanation, Runnable task) {
        return () -> {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                task.run();
                return;
            }

            Runnable requestPermission = () -> {
                ActivityCompat.requestPermissions(this, new String[] {permission}, PENDING_PERMISSION_REQUESTS);
                mPendingPermissionRequests.put(permission, task);
            };

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // Show an explanation to the user *asynchronously* -- don't block this thread waiting for the user's
                // response! After the user sees the explanation, try again to request the permission.
                Snackbar.make(getWindow().getDecorView().getRootView(), explanation, Snackbar.LENGTH_LONG)
                        .setAction(R.string.request, v -> requestPermission.run())
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                requestPermission.run();
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PENDING_PERMISSION_REQUESTS) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED && mPendingPermissionRequests.containsKey(permission)) {
                    // permission was granted, yay! Do the task you need to do.
                    mPendingPermissionRequests.remove(permission).run();
                }
            }
        }
    }

    private String generateRandomString(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = length;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        byte[] tmp = byteBuffer.toByteArray();
        inputStream.close();
        byteBuffer.close();
        return tmp;
    }
}
