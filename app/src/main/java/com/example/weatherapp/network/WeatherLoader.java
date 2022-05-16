package com.example.weatherapp.network;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.HashMap;

public class WeatherLoader extends AsyncTaskLoader<String> {

    Bundle customParams;
    public WeatherLoader(@NonNull Context context, Bundle customParams) {
        super(context);
        this.customParams = customParams;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return Network.getWeatherForNewCity(customParams);
    }

    @Override
    protected void onStartLoading() {
        forceLoad(); // Starts the loadInBackground method
    }
}
