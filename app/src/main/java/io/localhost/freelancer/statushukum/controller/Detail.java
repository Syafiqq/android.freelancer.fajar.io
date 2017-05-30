package io.localhost.freelancer.statushukum.controller;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.database.model.MDM_DataTag;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Tag;
import io.localhost.freelancer.statushukum.model.entity.ME_Data;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;
import io.localhost.freelancer.statushukum.model.util.ME_TagAdapter;
import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

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

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(referenceId == Detail.this.downloadID)
            {

                Toast.makeText(Detail.this, "File Berhasil Diunduh", Toast.LENGTH_SHORT).show();
                final String tmpPath = String.format(Locale.getDefault(), "%s/%s/reference/%s.pdf", Detail.super.getExternalFilesDir("").toString(), Environment.DIRECTORY_DOWNLOADS, filename + "_tmp");
                final File oldFile = new File(path);
                final File newFile = new File(tmpPath);
                if(oldFile.exists())
                {
                    oldFile.delete();
                }
                newFile.renameTo(oldFile);
                Detail.this.download.setEnabled(true);
                Detail.this.checkOfflineData();
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
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreate");

        setContentView(R.layout.activity_detail);
        final Intent intent = getIntent();
        this.id = intent.getIntExtra(Detail.EXTRA_ID, -1);

        this.setToolbar();
        this.setProperty();
        this.setDetail();
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
        Log.d(CLASS_NAME, CLASS_PATH + ".setToolbar");

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
        Log.i(CLASS_NAME, CLASS_PATH + ".onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.activity_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onOptionsItemSelected");

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
        Log.i(CLASS_NAME, CLASS_PATH + ".onBackButtonPressed");

        this.onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onBackPressed");

        super.finish();
    }

    @SuppressWarnings("ConstantConditions")
    private void setDetail()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".setDetail");

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
                    Detail.this.no.setHtml(this.dbResultData.getNo());
                    Detail.this.description.setHtml(this.dbResultData.getDescription().equalsIgnoreCase("null") ? "-" : this.dbResultData.getDescription());
                    Detail.this.status.setHtml(this.dbResultData.getStatus().equalsIgnoreCase("null") ? "-" : this.dbResultData.getStatus());
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

    private void checkFileStatus(@NotNull final ME_Data dbResultData)
    {
        this.filename = dbResultData.getNo().trim().replaceAll(" ", "_");
        this.filename = filename.replaceAll("[\\p{Punct}&&[^_]]+", "");
        this.path = String.format(Locale.getDefault(), "%s/%s/reference/%s.pdf", super.getExternalFilesDir("").toString(), Environment.DIRECTORY_DOWNLOADS, filename);
        final File dlc = new File(path);

        if(!dbResultData.getReference().equalsIgnoreCase("null"))
        {
            final String finalFilename = filename;
            Detail.this.download.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final Uri uri = Uri.parse(dbResultData.getReference());
                    final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    //Setting title of request
                    request.setTitle("Download");

                    //Setting description of request
                    request.setDescription("Mohon Tunggu");

                    //Set the local destination for the downloaded file to a path within the application's external files directory
                    request.setDestinationInExternalFilesDir(Detail.this, Environment.DIRECTORY_DOWNLOADS + "/reference", String.format(Locale.getDefault(), "%s_tmp.pdf", finalFilename));

                    //Enqueue download and save the referenceId
                    Detail.this.download.setEnabled(false);
                    Detail.this.downloadID = downloadManager.enqueue(request);
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

    private void checkOfflineData()
    {
        if(this.path != null)
        {
            final File dlc = new File(path);
            Detail.this.open.setVisibility(View.VISIBLE);
            Detail.this.download.setVisibility(View.VISIBLE);
            Detail.this.open.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent;
                    if(Build.VERSION.SDK_INT < 24)
                    {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(dlc), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    else
                    {
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri pdfURI = FileProvider.getUriForFile(Detail.this, Detail.super.getApplicationContext().getPackageName() + ".provider", dlc);
                        intent.putExtra(Intent.EXTRA_STREAM, pdfURI);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setType("application/pdf");
                    }
                    try
                    {
                        if(intent.resolveActivity(getPackageManager()) != null)
                        {
                            startActivity(intent);
                        }
                        else
                        {
                            final Intent viewer = new Intent(Detail.this, PDFViewer.class);
                            viewer.putExtra(PDFViewer.EXTRA_URI, path);
                            Detail.super.startActivity(viewer);
                        }
                    }
                    catch(Exception e)
                    {
                        Log.e("ErrorPDF", e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    protected void onPostResume()
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".onPostResume");
        this.setDetail();

        super.onPostResume();
    }
}
