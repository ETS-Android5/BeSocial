package com.example.besocial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.besocial.ui.LogoutDialog;
import com.example.besocial.ui.login.LoginActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.Menu;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fireBaseAuth;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private View logout;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fireBaseAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_profile, R.id.nav_notifications, R.id.nav_chat, R.id.nav_social_center, R.id.nav_bonus_area, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fireBaseAuth.getCurrentUser();
        if (currentUser == null) {
            sendUserToLogin();
        }
    }

    public void sendUserToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * if drawer is opened and the 'back' button was pressed-close the drawer
     * else, close the app if needed
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }


    /**
     * handles app bar menu items selection
     *
     * @param item -user's selection
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//     handles logout
        switch (item.getItemId()) {
            case R.id.logout:
                new LogoutDialog("Logging out").show(getSupportFragmentManager(), null);
                return true;

//if about was pressed while the current page is not about- navigate to about page, else do nothing
            case R.id.about:
                Context context;
                try {
                    if (!navController.getCurrentDestination().getLabel().equals("AboutFragment"))
                        navController.navigate(R.id.nav_about);
                }
                catch (NullPointerException e){
                    Log.d("NullPointerException","label was not found");
                }

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
