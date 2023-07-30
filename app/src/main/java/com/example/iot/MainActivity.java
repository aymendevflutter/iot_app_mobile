package com.example.iot;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.iot.fragment.AboutFragment;
import com.example.iot.fragment.AdminListFragment;
import com.example.iot.fragment.HomeFragment;
import com.example.iot.fragment.PressureFragment;
import com.example.iot.fragment.SettingsFragment;
import com.example.iot.ref.admin;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements
       BottomNavigationView
                   .OnNavigationItemSelectedListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    Intent mServiceIntent;
    boolean check;
    BottomNavigationView bottomNavigationView;
   // private pushnotifbackground mYourService;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private   View headerView;
    SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    Fragment fragment;
    String names;
    String urls;

    FirebaseFirestore db;




    @Override
    protected void onDestroy() {
        //    stopService(mServiceIntent);
        //    Intent broadcastIntent = new Intent();
        //   broadcastIntent.setAction("restartservice");
        //   broadcastIntent.setClass(this, Restarter.class);
        //   this.sendBroadcast(broadcastIntent);
        super.onDestroy();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   mYourService = new pushnotifbackground();
        //    mServiceIntent = new Intent(this, mYourService.getClass());
        //    if (!isMyServiceRunning(mYourService.getClass())) {
        //       startService(mServiceIntent);
        //  }
        admin.firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(MainActivity.this);
        bottomNavigationView.setSelectedItemId(R.id.heart);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);



        nvDrawer = (NavigationView) findViewById(R.id.nvView);
         headerView = nvDrawer.getHeaderView(0);




        getDateforNav();

        // Setup drawer view
        setupDrawerContent(nvDrawer);




        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                toolbar,

                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        if(admin.check){
           fragment = new AdminListFragment();

        }else {
             fragment = new HomeFragment();}

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        nvDrawer.setCheckedItem(R.id.nav_home);
        setTitle("Home");
        if(admin.check){
            bottomNavigationView.setVisibility(View.GONE);
        }




    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.nav_logout:
                //  mDrawer.openDrawer(GravityCompat.START);
                return true;
            default:

        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = new HomeFragment();
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                if(!admin.check){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    fragmentClass = HomeFragment.class;
                }else {
                    fragmentClass = AdminListFragment.class;
                }

                break;
            case R.id.nav_logout:
                logout();
                //   fragmentClass = homeeFragment.class;


                //  break;
            case R.id.nav_settings:
                bottomNavigationView.setVisibility(View.GONE);

                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_about:
                bottomNavigationView.setVisibility(View.GONE);
                fragmentClass = AboutFragment.class;

                break;

            default:
                if(!admin.check){
                    fragmentClass = HomeFragment.class;
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }else {
                    fragmentClass = AdminListFragment.class;
                }

        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            if(!admin.check){
            fragment = new HomeFragment();}
            else {
                fragment = new AdminListFragment();
            }
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.syncState();
    }
    private void logout() {
        // Clear session data
        // ...
        admin.firebaseAuth.signOut();
        ClearPref();

        // Start the login activity
        Intent intent = new Intent(this, logInActivity.class);
        startActivity(intent);

        finish(); // Close the current activity
    }
    HomeFragment homeFragment = new HomeFragment();
    PressureFragment pressureFragment = new PressureFragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId()) {


            case R.id.heart:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, homeFragment)
                        .commit();
                return true;

            case R.id.water:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, pressureFragment)
                        .commit();
                return true;
        }
        return false;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    void getDateforNav(){


        FirebaseUser currentUser = admin.firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            getDataFromPref("name","url");
            if(check){
              //  getDataFromPref(names,urls);
                setData(names,urls);
            }else {
                getdb(uid);
            }


        }else {
            Toast.makeText(MainActivity.this,"null uid warnning",Toast.LENGTH_SHORT);
        }

    }
    void  setData(String name , String url){
        TextView nameTextView = headerView.findViewById(R.id.myname);
        nameTextView.setText(name);

        // Load the image into a circular ImageView in the navigation header
        ImageView imageView = headerView.findViewById(R.id.myimage);
        Glide.with(getApplicationContext())
                .load(url)
                .circleCrop()
                .into(imageView);

    }
   void getDataFromPref(String name , String url){

        if( !sharedPreferences.contains("name") && !sharedPreferences.contains("url")){
            check = false;

        }else {
              names  = sharedPreferences.getString(name,"noname");
             urls =sharedPreferences.getString(url,"nourl");
            check = true;
        }}

   void getdb(String uid) {
           DocumentReference userRef = db.collection("users").document(uid);
           userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   if (documentSnapshot.exists()) {
                       String name = documentSnapshot.getString("fullName");
                       String imageUrl = documentSnapshot.getString("url");
                       String email = documentSnapshot.getString("email");
                       String phone = documentSnapshot.getString("phone");
                       setData(name,imageUrl);
                       SaveData("name",name);
                       SaveData("url",imageUrl);
                       SaveData("phone",phone);
                       SaveData("email", email);




                   }else {
                       Toast.makeText(MainActivity.this, "there no Data warning", Toast.LENGTH_SHORT).show();
                   }
               }
           });
       }
    void SaveData(String key,String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,data);
        editor.apply();

    }
    void ClearPref(){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }





}