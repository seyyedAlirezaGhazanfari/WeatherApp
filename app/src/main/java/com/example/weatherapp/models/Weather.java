package com.example.weatherapp.models;

import java.util.ArrayList;

public class Weather {
    private String name;
    public static ArrayList<Weather> weathers = new ArrayList<>();

    public Weather(String name) {
        this.name = name;
        weathers.add(this);
    }

    public String getName() {
        return name;
    }
}
