package com.example.weatherapp.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class WeatherLoader extends AsyncTaskLoader<String> {

    String city;
    public WeatherLoader(@NonNull Context context, String city) {
        super(context);
        this.city = city;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return Network.getWeatherForNewCity(city);
    }

    @Override
    protected void onStartLoading() {
        forceLoad(); // Starts the loadInBackground method
    }
}
