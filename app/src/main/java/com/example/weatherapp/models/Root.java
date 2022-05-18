package com.example.weatherapp.models;

import java.util.ArrayList;
import java.util.List;

public class Root {
    public String cod;
    public int message;
    public int cnt;
    public ArrayList<WeatherList> list = new ArrayList<>();
    public City city;

    public Root() {
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public ArrayList<WeatherList> getList() {
        return list;
    }

    public void setList(ArrayList<WeatherList> list) {
        this.list = list;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }



    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
