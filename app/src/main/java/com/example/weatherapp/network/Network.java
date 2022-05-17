package com.example.weatherapp.network;
import java.net.InetAddress;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.weatherapp.MainActivity;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class Network {

    static final String APP_ID = "ba7e8a630bb4d31538ddf6128ceccd37";
    static final String WEATHER_URL = "api.openweathermap.org";
    static RequestParams params;
    static String mainCityResult;

    static String getWeatherForNewCity(Bundle bundle) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                //.appendPath("daily")
                .authority(WEATHER_URL)
                .appendQueryParameter("appid", APP_ID)
        ;
        for (String key : bundle.keySet()) {
            builder.appendQueryParameter(key, bundle.get(key).toString());
        }
        return networking(builder.build().toString(), bundle);
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    private static String networking(String uri, Bundle extraData) {
        try {
            if (isInternetAvailable()) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = null;
                response = httpclient.execute(new HttpGet(uri));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    mainCityResult = out.toString();
                    out.close();

                }
            }
        } catch (Throwable t) {
        }
        String key = "";
        if (extraData.containsKey("q")) {
            key = extraData.get("q").toString();
        } else {
            key = extraData.getString("lat") + "," + extraData.getString("lon");
        }
        if (mainCityResult == null) {
            String data = MainActivity.sharedPreferences.getString(key, null);
            if (data != null && System.currentTimeMillis() < Long.parseLong(data.split("@@@")[1])) {
                mainCityResult = data.split("@@@")[0];
            }
        } else {
            SharedPreferences.Editor sharedPreferencesEditor = MainActivity.sharedPreferences.edit();
            sharedPreferencesEditor.putString(key, mainCityResult + "@@@" + (12 * 60 * 60 * 1000 + System.currentTimeMillis()));
            sharedPreferencesEditor.commit();
        }
        return mainCityResult;
    }
}