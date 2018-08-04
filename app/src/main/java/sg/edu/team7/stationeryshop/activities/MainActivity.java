package sg.edu.team7.stationeryshop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Set;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.fragments.DepartmentOptionsFragment;
import sg.edu.team7.stationeryshop.fragments.DisbursementFragment;
import sg.edu.team7.stationeryshop.fragments.MakeStockAdjustmentFragment;
import sg.edu.team7.stationeryshop.fragments.RequisitionRequestFragment;
import sg.edu.team7.stationeryshop.fragments.StationeryRetrievalFragment;
import sg.edu.team7.stationeryshop.fragments.StockAdjustmentRequestsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RequisitionRequestFragment.OnFragmentInteractionListener,
        DepartmentOptionsFragment.OnFragmentInteractionListener,
        StationeryRetrievalFragment.OnFragmentInteractionListener,
        DisbursementFragment.OnFragmentInteractionListener,
        StockAdjustmentRequestsFragment.OnFragmentInteractionListener,
        MakeStockAdjustmentFragment.OnFragmentInteractionListener {

    private Fragment currentFragment;

    private static Context context;

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set Starting Fragment
        currentFragment = null;
        Class fragmentClass;

        Set<String> roles = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getStringSet("roles", null);

        if (roles.contains("Employee") || roles.contains("Department Head"))
            fragmentClass = RequisitionRequestFragment.class;
        else if (roles.contains("Store Clerk"))
            fragmentClass = StationeryRetrievalFragment.class;
        else
            fragmentClass = StockAdjustmentRequestsFragment.class;

        try {
            currentFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, currentFragment).commit();

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        // Fill Navigation Menu Items based on Roles
        if (roles.contains("Admin")) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_admin_drawer);
        } else if (roles.contains("Store Supervisor") || roles.contains("Store Manager")) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_manager_drawer);
        } else if (roles.contains("Store Clerk")) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_store_drawer);
        } else if (roles.contains("Department Head")) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_department_drawer);
        }

        navigationView.setNavigationItemSelectedListener(this);

        // Show email address of logged in user
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = headerView.findViewById(R.id.textView);
        navEmail.setText(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", "no email"));

        //Show app token for firebase
        System.out.println("FCM token: "+ FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_option_1:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Class fragmentClass = null;

        if (id == R.id.nav_stationery_retrievals) {
            fragmentClass = StationeryRetrievalFragment.class;
        } else if (id == R.id.nav_disbursements) {
            fragmentClass = DisbursementFragment.class;
        } else if (id == R.id.nav_make_stock_adjustments) {
            fragmentClass = StockAdjustmentRequestsFragment.class;
        } else if (id == R.id.nav_requisition_request) {
            fragmentClass = RequisitionRequestFragment.class;
        } else if (id == R.id.nav_department_options) {
            fragmentClass = DepartmentOptionsFragment.class;
        } else if (id == R.id.nav_stock_adjustment_requests) {
            fragmentClass = StockAdjustmentRequestsFragment.class;
        } else if (id == R.id.nav_department_usage) {
            Intent intent = new Intent(MainActivity.this, DepartmentUsageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_store_operations) {
            Intent intent = new Intent(MainActivity.this, StoreOperationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        try {
            currentFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, currentFragment).commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode,resultCode,data);

    }
}
