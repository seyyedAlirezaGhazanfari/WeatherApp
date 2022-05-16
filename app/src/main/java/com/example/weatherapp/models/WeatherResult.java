package com.example.weatherapp.models;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WeatherResult {
    private Coord coord;
    //private List<Weather> weather;
    private Weather weather;
    private String base;
    private Main main;
    private int visibility;
    private Wind wind;
    private int dt;
    private Sys sys;
    private int timezone;
    private int id;
    private String cityName;
    private int cod;
    private String micon;
    public static ArrayList<WeatherResult> results = new ArrayList<>();
    private static final DecimalFormat df = new DecimalFormat("0.");


    public static void fromJson(JSONObject jsonObject) {

    }


    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public void setMicon(String micon) {
        this.micon = micon;
    }

    public static ArrayList<WeatherResult> getResults() {
        return results;
    }

    public static void setResults(ArrayList<WeatherResult> results) {
        WeatherResult.results = results;
    }

    public static DecimalFormat getDf() {
        return df;
    }

    public static String updateWeatherIcon(int condition)
    {
        if(condition>=0 && condition<=300)
        {
            return "thunder_cloud_rain";
        }
        else if(condition>=300 && condition<=500)
        {
            return "rain";
        }
        else if(condition>=500 && condition<=600)
        {
            return "thunder_cloud_rain";
        }
        else  if(condition>=600 && condition<=700)
        {
            return "snow";
        }
        else if(condition>=701 && condition<=771)
        {
            return "moon_cloud";
        }

        else if(condition>=772 && condition<=800)
        {
            return "overcast";
        }
        else if(condition==800)
        {
            return "sun";
        }
        else if(condition>=801 && condition<=804)
        {
            return "cloudy";
        }
        else  if(condition>=900 && condition<=902)
        {
            return "thunder";
        }
        if(condition==903)
        {
            return "very_snow_cloud";
        }
        if(condition==904)
        {
            return "sun_cloud";
        }
        if(condition>=905 && condition<=1000)
        {
            return "thunder";
        }

        return "dunno";
    }

    public Coord getCoord() {
        return coord;
    }

    public int getWeatherId() {
        return weather.getId();
    }

    public double getTemp() {
        return main.getTemp();
    }

    public String getCityName() {
        return cityName;
    }

    public String getMicon() {
        return micon;
    }

    public Weather getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }
}
