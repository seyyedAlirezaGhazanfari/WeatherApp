package com.example.weatherapp.network;

import android.os.Looper;

import com.example.weatherapp.models.WeatherResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Network {

    static final String APP_ID = "dab3af44de7d24ae7ff86549334e45bd";
    static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    static RequestParams params;
    static String mainCityResult;

    static String getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        //networking(params);
        return networking(params);
    }

    private static String networking(RequestParams params) {
        Looper.prepare();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mainCityResult =  response.toString();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        return mainCityResult;
    }
}
