package com.example.weatherapp.network;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class Network {

    static final String APP_ID = "dab3af44de7d24ae7ff86549334e45bd";
    static final String WEATHER_URL = "api.openweathermap.org";
    static RequestParams params;
    static String mainCityResult;

    static String getWeatherForNewCity(Bundle bundle) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("weather")
                .authority(WEATHER_URL)
                .appendQueryParameter("appid", APP_ID)
        ;
        for (String key : bundle.keySet()) {
            builder.appendQueryParameter(key,bundle.get(key).toString());
        }
        return networking(builder.build().toString());
    }

    private static String networking(String uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = httpclient.execute(new HttpGet(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                response.getEntity().writeTo(out);
                mainCityResult = out.toString();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mainCityResult;
    }
}
