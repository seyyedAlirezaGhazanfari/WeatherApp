package com.example.weatherapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.models.Weather;
import com.example.weatherapp.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity  {
    private static final String preferencesKey = "weatherApp";
    public static SharedPreferences sharedPreferences;
    public static ConnectivityManager connectivityManager;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean(getString(R.string.darkModeSwitchKey), false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
//        Weather w1 = new Weather("sun", 10, 9, 5);
//        Weather w2 = new Weather("rain", 25, 22, 10);
//        Weather w3 = new Weather("snow", 30, 27, 20);
//        Weather w4 = new Weather("thunder", 2, 1, 3);
//        Weather w5 = new Weather("moon", -4, 2, 9);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }
}