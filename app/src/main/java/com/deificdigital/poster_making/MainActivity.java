package com.deificdigital.poster_making;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.deificdigital.poster_making.fragments.HomeFragment;
import com.deificdigital.poster_making.fragments.ProfileFragment;
import com.deificdigital.poster_making.fragments.SaveImagesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

//    private LottieAnimationView lottieVegetables;
    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        lottieVegetables = findViewById(R.id.lottieVegetables);
        fragmentContainer = findViewById(R.id.fragment_container);

//        registerReceiver(networkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
//        checkInternetAndUpdateUI();

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//            if (!isNetworkAvailable(this)) {
//                lottieVegetables.setVisibility(View.VISIBLE);
//                fragmentContainer.setVisibility(View.GONE);
//                return false;
//            }

            int id = item.getItemId();
            if (id == R.id.nav_home){
                loadFrag(new HomeFragment(), false);
            } else if (id == R.id.nav_saved) {
                loadFrag(new SaveImagesFragment(), false);
            } else {
                loadFrag(new ProfileFragment(), false);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

//    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            checkInternetAndUpdateUI();
//        }
//    };
//
//    private void checkInternetAndUpdateUI() {
//        if (isNetworkAvailable(this)) {
//            lottieVegetables.setVisibility(View.GONE);
//            fragmentContainer.setVisibility(View.VISIBLE);
//        } else {
//            lottieVegetables.setVisibility(View.VISIBLE);
//            fragmentContainer.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(networkReceiver);
//    }
    public void loadFrag(Fragment fragment, boolean flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag){
            ft.add(R.id.fragment_container, fragment);
        }else {
            ft.replace(R.id.fragment_container, fragment);
        }
        ft.commit();
    }

//    public boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
//                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
//            } else {
//                return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
//            }
//        }
//        return false;
//    }
}