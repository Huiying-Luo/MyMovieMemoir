package com.laverne.mymoviememoir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.Gravity;
import android.view.MenuItem;


import com.google.android.material.navigation.NavigationView;
import com.laverne.mymoviememoir.Fragment.HomeFragment;
import com.laverne.mymoviememoir.Fragment.MapsFragment;
import com.laverne.mymoviememoir.Fragment.MemoirFragment;
import com.laverne.mymoviememoir.Fragment.ReportFragment;
import com.laverne.mymoviememoir.Fragment.SearchFragment;
import com.laverne.mymoviememoir.Fragment.WatchlistFragment;
import com.laverne.mymoviememoir.NetworkConnection.NetworkConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private NetworkConnection networkConnection;

    private String userId;
    private String firstname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkConnection = new NetworkConnection();

        //adding the toolbar as the app bar for this activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get intent
        Intent intent = getIntent();
        String credId = intent.getStringExtra("credId");

        // get user info and set userId and firstname to sharedPreference to share to all fragments
        new GetUserInfoByCredIdTask().execute(credId);

        // configure navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        toggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //these two lines of code show the navicon drawer icon top left hand side
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new HomeFragment());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.homepage:
                replaceFragment(new HomeFragment());
                break;
            case R.id.search:
                replaceFragment(new SearchFragment());
                break;
            case R.id.memoir:
                replaceFragment(new MemoirFragment());
                break;
            case R.id.watchlist:
                replaceFragment(new WatchlistFragment());
                break;
            case R.id.report:
                replaceFragment(new ReportFragment());
                break;
            case R.id.map:
                replaceFragment(new MapsFragment());
                break;
        }
        // highlight the selected item
        navigationView.setCheckedItem(id);
        // set action bar title
        setTitle(item.getTitle());
        //this code closes the drawer after you selected an item from the menu, otherwise stay open
        drawerLayout.closeDrawer(GravityCompat.START, true);
        return true;
    }


    private void replaceFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, nextFragment);
        fragmentTransaction.commit();
    }


    private void setUpSharedPref() {
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("userId", userId);
        spEditor.putString("firstName", firstname);
        spEditor.apply();
    }


    private class GetUserInfoByCredIdTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getUserByCredId(Integer.parseInt(params[0]));
        }

        @Override
        protected void onPostExecute(String user) {
            if (user != null) {
                try {
                    JSONArray jsonArray = new JSONArray(user);
                    if (jsonArray.length() != 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        userId = jsonObject.getString("userId");
                        firstname = jsonObject.getString("userFname");

                        setUpSharedPref();
                        // first view
                        navigationView.setCheckedItem(R.id.homepage);
                        replaceFragment(new HomeFragment());

                    } else {
                        Utilities.showAlertDialogwithOkButton(MainActivity.this, "Error", "Something went wrong, please try again later.");
                    }
                } catch (JSONException e) {
                    Utilities.showAlertDialogwithOkButton(MainActivity.this, "Error", "Something went wrong, please try again later.");
                    e.printStackTrace();
                }
            } else {
                Utilities.showAlertDialogwithOkButton(MainActivity.this, "Error", "Something went wrong, please try again later.");
            }
        }
    }
}
