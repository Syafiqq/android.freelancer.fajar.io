package io.localhost.freelancer.statushukum.controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import io.localhost.freelancer.statushukum.R;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Constitution.OnFragmentInteractionListener, GovrnRule.OnFragmentInteractionListener
{
    public static final String CLASS_NAME = "Dashboard";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.Dashboard";

    public DrawerLayout drawer;
    public Toolbar toolbar;
    private TextView toolbarTitle;
    private ProgressDialog progressBar;

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
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = Constitution.class;
            try
            {
                fragment = (Fragment) fragmentClass.newInstance();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_dashboard_root, fragment).commit();
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
            case R.id.nav_menu_dashboard_rule_constitution:
            {
                this.changeLayout(Constitution.class);
            }
            break;
            case R.id.nav_menu_dashboard_rule_govrn_rule:
            {
                this.changeLayout(GovrnRule.class);
            }
            break;
            case R.id.nav_menu_dashboard_sync:
            {
                this.onBackPressed();

                this.progressBar.show();
                io.localhost.freelancer.statushukum.model.util.Setting.doSync(new Observer()
                {
                    @Override
                    public void update(Observable o, Object arg)
                    {
                        Dashboard.this.progressBar.dismiss();
                    }
                }, Dashboard.this);
                return true;
            }
            case R.id.nav_menu_dashboard_mailto:
            {
                this.onBackPressed();
                io.localhost.freelancer.statushukum.model.util.Setting.sendFeedback(Dashboard.this);
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
