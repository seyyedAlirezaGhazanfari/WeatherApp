package com.example.weatherapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
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


    public static WeatherResult fromJson(JSONObject jsonObject) {
        try {
            WeatherResult weatherR = new WeatherResult();
            df.setRoundingMode(RoundingMode.UP);
            weatherR.cityName = jsonObject.getString("name");
            weatherR.weather = new Weather();
            weatherR.weather.setId(jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id"));
            weatherR.weather.setMain(jsonObject.getJSONArray("weather").getJSONObject(0).getString("main"));
            weatherR.micon = updateWeatherIcon(weatherR.weather.getId());
            weatherR.main = new Main();
            weatherR.main.setTemp(Double.parseDouble(df.format(jsonObject.getJSONObject("main").getDouble("temp") - 273.15)));
            weatherR.main.setFeels_like(Double.parseDouble(df.format(jsonObject.getJSONObject("main").getDouble("feels_like") - 273.15)));
            weatherR.main.setHumidity((int) jsonObject.getJSONObject("main").getDouble("humidity"));
            weatherR.weather.setDesctiption(jsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));
            results.add(weatherR);
            weatherR.wind = new Wind();
            weatherR.wind.setSpeed(jsonObject.getJSONObject("wind").getDouble("speed"));
            return weatherR;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String updateWeatherIcon(int condition)
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
