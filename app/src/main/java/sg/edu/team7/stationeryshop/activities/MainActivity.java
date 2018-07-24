package sg.edu.team7.stationeryshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.fragments.DepartmentRepresentativeFragment;
import sg.edu.team7.stationeryshop.fragments.DisbursementFragment;
import sg.edu.team7.stationeryshop.fragments.ManagerDelegationFragment;
import sg.edu.team7.stationeryshop.fragments.RequisitionRequestFragment;
import sg.edu.team7.stationeryshop.fragments.StationeryRetrievalFragment;
import sg.edu.team7.stationeryshop.fragments.StockAdjustmentFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RequisitionRequestFragment.OnFragmentInteractionListener,
        DepartmentRepresentativeFragment.OnFragmentInteractionListener,
        ManagerDelegationFragment.OnFragmentInteractionListener,
        StationeryRetrievalFragment.OnFragmentInteractionListener,
        DisbursementFragment.OnFragmentInteractionListener,
        StockAdjustmentFragment.OnFragmentInteractionListener {

    private Fragment currentFragment;

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get roles from token
        Map token = (Map) getIntent().getSerializableExtra("accessToken");
        List rolesFromToken = (List) token.get("roles");

        List<String> roles = new ArrayList<>();
        rolesFromToken.forEach(x -> roles.add((String) x));

        // Set Starting Fragment
        currentFragment = null;
        Class fragmentClass;

        if (roles.stream()
                .filter(role -> role.equals("Employee") || role.equals("DepartmentHead"))
                .count() > 0)
            fragmentClass = RequisitionRequestFragment.class;
        else
            fragmentClass = StationeryRetrievalFragment.class;

        try {
            currentFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, currentFragment).commit();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        // Fill Navigation Menu Items based on Roles
        if (roles.stream()
                .filter(role -> role.equals("Employee") || role.equals("DepartmentHead"))
                .count() > 0) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_department_drawer);
        } else if (roles.stream()
                .filter(role -> role.equals("StoreClerk") || role.equals("StoreManager"))
                .count() > 0) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_store_drawer);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_admin_drawer);
        }
        navigationView.setNavigationItemSelectedListener(this);

        // Show email address of logged in user
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = headerView.findViewById(R.id.textView);
        navEmail.setText(token.get("email").toString());
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
        // TODO: If access token expires, revert to LoginActivity

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_stationery_retrievals) {
            fragmentClass = StationeryRetrievalFragment.class;
        } else if (id == R.id.nav_disbursements) {
            fragmentClass = DisbursementFragment.class;
        } else if (id == R.id.nav_stock_adjustments) {
            fragmentClass = StockAdjustmentFragment.class;
        } else if (id == R.id.nav_requisition_request) {
            fragmentClass = RequisitionRequestFragment.class;
        } else if (id == R.id.nav_department_representative) {
            fragmentClass = DepartmentRepresentativeFragment.class;
        } else if (id == R.id.nav_manager_delegation) {
            fragmentClass = ManagerDelegationFragment.class;
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
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
}
