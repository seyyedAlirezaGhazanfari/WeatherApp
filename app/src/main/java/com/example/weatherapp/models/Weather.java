package com.example.weatherapp.models;

import java.util.ArrayList;

public class Weather {
    private String status;
    private int temperature;
    private int feelsLike;
    private int humidity;
    public static ArrayList<Weather> weathers = new ArrayList<>();

    public Weather(String status, int temperature, int feelsLike, int humidity) {
        this.status = status;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        weathers.add(this);
    }

    public static ArrayList<Weather> getWeathers() {
        return weathers;
    }

    public int getFeelsLike() {
        return feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getStatus() {
        return status;
    }
}
