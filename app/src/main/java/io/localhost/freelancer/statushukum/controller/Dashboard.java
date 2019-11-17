package io.localhost.freelancer.statushukum.controller;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.util.Setting;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Law.OnFragmentInteractionListener
{
    public static final String CLASS_NAME = "Dashboard";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Dashboard";

    public DrawerLayout drawer;
    public Toolbar toolbar;
    private TextView toolbarTitle;
    private ProgressDialog progressBar;
    private Law fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_wrapper);

        this.setToolbar();
        this.setNavigationSwipe();
        this.setProgressView();

        if(savedInstanceState == null)
        {
            Class fragmentClass;
            fragmentClass = Law.class;
            try
            {
                fragment = (Law) fragmentClass.newInstance();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_dashboard_root, fragment).commit();
        }

        new Handler().postDelayed(() -> {
            fragment.updateCategory(1, R.string.nav_header_dashboard_drawer_rule_tap_mpr);
        }, 1000);
    }

    @Override
    protected void onStart()
    {
        final View socialFacebook = super.findViewById(R.id.activity_dashboard_wrapper_imagebutton_social_facebook);
        final View socialTwitter = super.findViewById(R.id.activity_dashboard_wrapper_imagebutton_social_twitter);
        final View socialInstagram = super.findViewById(R.id.activity_dashboard_wrapper_imagebutton_social_instagram);
        final View socialGPlus = super.findViewById(R.id.activity_dashboard_wrapper_imagebutton_social_google_plus);
        socialFacebook.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Dashboard.this.onFacebookSocialPressed(v);
            }
        });
        socialTwitter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Dashboard.this.onTwitterSocialPressed(v);
            }
        });
        socialInstagram.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Dashboard.this.onInstagramSocialPressed(v);
            }
        });
        socialGPlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Dashboard.this.onGPlusSocialPressed(v);
            }
        });
        super.onStart();
    }

    private void onGPlusSocialPressed(View view)
    {
        try
        {
            this.onBackPressed();
            super.startActivity(Setting.getInstance(this).social.gPlus.getGPlusIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void onInstagramSocialPressed(View view)
    {
        try
        {
            this.onBackPressed();
            super.startActivity(Setting.getInstance(this).social.instagram.getInstagramIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void onTwitterSocialPressed(View view)
    {
        try
        {
            this.onBackPressed();
            super.startActivity(Setting.getInstance(this).social.twitter.getTwitterIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void onFacebookSocialPressed(View view)
    {
        try
        {
            this.onBackPressed();
            super.startActivity(Setting.getInstance(this).social.facebook.getFacebookIntent(this));
        }
        catch(ActivityNotFoundException | NullPointerException e)
        {
            Toast.makeText(this, "Tidak ada aplikasi yang mendukung perintah ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void setProgressView()
    {
        this.progressBar = new ProgressDialog(Dashboard.this);
        progressBar.setMessage(super.getResources().getString(R.string.content_setting_s_sync_label_desc));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(false);
    }

    @Override
    public void onBackPressed()
    {
        if(this.drawer.isDrawerOpen(GravityCompat.START))
        {
            this.drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                Dashboard.this.onBackButtonPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        item.setCheckable(false);
        switch(id)
        {
            case R.id.nav_menu_dashboard_rule_tap_mpr:
            {
                fragment.updateCategory(1, R.string.nav_header_dashboard_drawer_rule_tap_mpr);
            }
            break;
            case R.id.nav_menu_dashboard_rule_uu:
            {
                fragment.updateCategory(2, R.string.nav_header_dashboard_drawer_rule_uu);
            }
            break;
            case R.id.nav_menu_dashboard_rule_uu_darurat:
            {
                fragment.updateCategory(3, R.string.nav_header_dashboard_drawer_rule_uu_darurat);
            }
            break;
            case R.id.nav_menu_dashboard_rule_perpu:
            {
                fragment.updateCategory(4, R.string.nav_header_dashboard_drawer_rule_perpu);
            }
            break;
            case R.id.nav_menu_dashboard_rule_pp:
            {
                fragment.updateCategory(5, R.string.nav_header_dashboard_drawer_rule_pp);
            }
            break;
            case R.id.nav_menu_dashboard_rule_perpres:
            {
                fragment.updateCategory(6, R.string.nav_header_dashboard_drawer_rule_perpres);
            }
            break;
            case R.id.nav_menu_dashboard_sync:
            {
                this.onBackPressed();

                this.progressBar.show();
                io.localhost.freelancer.statushukum.model.util.Setting.doSync(
                        () -> new Handler().postDelayed(() -> fragment.updateCategory(), 500),
                        null,
                        () -> Dashboard.this.progressBar.dismiss(),
                        Dashboard.this);
                return true;
            }
        }

        this.onBackPressed();
        return true;
    }

    private void changeLayout(Class<? extends Fragment> fragmentClass)
    {
        try
        {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final Fragment oldFragment = fragmentManager.findFragmentByTag(fragmentClass.getName());
            if(oldFragment == null || !oldFragment.isVisible())
            {
                final Fragment newFragment = fragmentClass.newInstance();
                fragmentManager.beginTransaction().replace(R.id.content_dashboard_root, newFragment, fragmentClass.getName()).commit();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private void setToolbar()
    {


        this.toolbar = (Toolbar) super.findViewById(R.id.activity_dashboard_toolbar);
        this.toolbarTitle = (TextView) this.toolbar.findViewById(R.id.activity_dashboard_toolbar_title);
        super.setSupportActionBar(this.toolbar);
        final ActionBar actionBar = super.getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayShowTitleEnabled(false);
            this.toolbar.setContentInsetStartWithNavigation(4);
        }
    }

    private void setNavigationSwipe()
    {
        this.drawer = (DrawerLayout) findViewById(R.id.activity_dashboard_wrapper_drawerlayout_container);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_dashboard_wrapper_navigationview_nav);
        ImageButton button = (ImageButton) navigationView.getHeaderView(0).findViewById(R.id.nav_header_dashboard_imagebutton_back);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Dashboard.this.onBackPressed();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void onBackButtonPressed()
    {


        this.onBackPressed();
    }

    @Override
    public void onFragmentChangeForTitle(int string)
    {
        this.toolbarTitle.setText(super.getResources().getString(string));
    }
}
