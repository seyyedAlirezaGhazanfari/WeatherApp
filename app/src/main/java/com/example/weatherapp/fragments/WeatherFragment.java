package com.example.weatherapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.DescriptionActivity;
import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.adaptor.RecyclerviewAdapter;
import com.example.weatherapp.models.Main;
import com.example.weatherapp.models.Weather;
import com.example.weatherapp.models.WeatherResult;
import com.example.weatherapp.models.Wind;
import com.example.weatherapp.network.WeatherLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment implements RecyclerviewAdapter.OnNoteListener, LoaderManager.LoaderCallbacks<String> {

    View rootView;
    RadioGroup radioGroup;
    RadioButton coorRadioBtn;
    RadioButton cityRadioBtn;
    ImageView weatherIcon;
    RecyclerView recyclerView;
    EditText cityNameOrWidthET;
    EditText heightET;
    RecyclerviewAdapter adapter;
    LinearLayout layoutMainCity;
    TextView mainCityTemp;
    TextView mainCityHum;
    TextView mainCityFeels;
    WeatherResult mainCityWeatherResult;
    private static final DecimalFormat df = new DecimalFormat("0.");

    final String APP_ID = "dab3af44de7d24ae7ff86549334e45bd";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;
    String Location_Provider = LocationManager.GPS_PROVIDER;
    LocationManager mLocationManager;
    LocationListener mLocationListner;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //* @param param1 Parameter 1.
     * //* @param param2 Parameter 2.
     *
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: do what you need here
                // ............
                // ............
                Bundle queryBundle = new Bundle();
                queryBundle.putString("queryString", cityNameOrWidthET.getText().toString());
                WeatherFragment.this.getActivity().getSupportLoaderManager().restartLoader(0, queryBundle, WeatherFragment.this);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        // Inflate the layout for this fragment
        RelativeLayout topRelativeLayout = rootView.findViewById(R.id.relative_layout);
        radioGroup = topRelativeLayout.findViewById(R.id.radio_group);
        coorRadioBtn = topRelativeLayout.findViewById(R.id.coorRatioBtn);
        cityRadioBtn = topRelativeLayout.findViewById(R.id.cityNameRatioBtn);

        RelativeLayout bottomRelative = rootView.findViewById(R.id.bottentRelativeId);
        weatherIcon = bottomRelative.findViewById(R.id.weatherIconId);

        layoutMainCity = bottomRelative.findViewById(R.id.layoutMainCityId);
        mainCityTemp = layoutMainCity.findViewById(R.id.temperatureId);
        mainCityHum = layoutMainCity.findViewById(R.id.humiId);
        mainCityFeels = layoutMainCity.findViewById(R.id.feelsLikeId);


        cityNameOrWidthET = bottomRelative.findViewById(R.id.citynameOrWidthET_Id);
        heightET = bottomRelative.findViewById(R.id.heightET_Id);
        recyclerView = bottomRelative.findViewById(R.id.recyclerviewId);

        // not clean at all !
        cityNameOrWidthET.setText("");
        heightET.setText("");
        cityNameOrWidthET.setVisibility(View.VISIBLE);
        heightET.setVisibility(View.VISIBLE);
        cityNameOrWidthET.setHint("Width");
        heightET.setHint("Height");
        cityNameOrWidthET.setInputType(InputType.TYPE_CLASS_NUMBER);
        heightET.setInputType(InputType.TYPE_CLASS_NUMBER);

        coorRadioBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                cityNameOrWidthET.setText("");
                heightET.setText("");
                cityNameOrWidthET.setVisibility(View.VISIBLE);
                heightET.setVisibility(View.VISIBLE);
                cityNameOrWidthET.setHint("Width");
                heightET.setHint("Height");
                cityNameOrWidthET.setInputType(InputType.TYPE_CLASS_NUMBER);
                heightET.setInputType(InputType.TYPE_CLASS_NUMBER);
                Toast.makeText(getActivity(), coorRadioBtn.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        cityRadioBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                cityNameOrWidthET.setText("");
                heightET.setText("");
                cityNameOrWidthET.setVisibility(View.VISIBLE);
                heightET.setVisibility(View.INVISIBLE);
                cityNameOrWidthET.setHint("City Name");
                cityNameOrWidthET.setInputType(InputType.TYPE_CLASS_TEXT);
                Toast.makeText(getActivity(), cityRadioBtn.getText().toString(), Toast.LENGTH_SHORT).show();

                cityNameOrWidthET.addTextChangedListener(new TextWatcher() {
                                                    @Override
                                                    public void beforeTextChanged (CharSequence s,int start, int count,
                                                                                   int after){
                                                    }
                                                    @Override
                                                    public void onTextChanged ( final CharSequence s, int start, int before,
                                                                                int count){
                                                        //You need to remove this to run only once
                                                        handler.removeCallbacks(input_finish_checker);

                                                    }
                                                    @Override
                                                    public void afterTextChanged ( final Editable s){
                                                        //avoid triggering event when text is empty
                                                        if (s.length() > 0) {
                                                            last_text_edit = System.currentTimeMillis();
                                                            handler.postDelayed(input_finish_checker, delay);
                                                        } else {

                                                        }
                                                    }
                                                }

                );
            }
        });


        recyclerView = bottomRelative.findViewById(R.id.recyclerviewId);
        adapter = new RecyclerviewAdapter(rootView.getContext(), WeatherResult.results, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public void OnNoteListener(ImageView icon, TextView temp, TextView feelsLike, TextView humidity, int position) {
        Toast toast = Toast.makeText(getActivity(), "CLICKED", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(
                this.getActivity(),
                DescriptionActivity.class
        );
        //int resourceID = Integer.parseInt(icon.getTag().toString());

        WeatherResult weatherResult = WeatherResult.results.get(position);
        int resourceID = getResources().getIdentifier(weatherResult.getMicon(), "drawable", requireActivity().getPackageName());
        intent.putExtra("iconDesc", resourceID);
        intent.putExtra("tempDesc", String.valueOf(weatherResult.getTemp()));
        intent.putExtra("feelsLikeDesc", String.valueOf(weatherResult.getMain().getFeels_like()));
        intent.putExtra("humidityDesc", String.valueOf(weatherResult.getMain().getHumidity()));
        intent.putExtra("description", weatherResult.getWeather().getDesctiption());
        startActivity(intent);

    }


    @Override
    public void onResume() {
        super.onResume();
        //getWeatherForCurrentLocation();
    }

/*
    private void getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        letsdoSomeNetworking(params);
    }
 */


/*
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(getActivity(), "on location changed", Toast.LENGTH_SHORT).show();
                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("lat", Latitude);
                params.put("lon", Longitude);
                params.put("appid", APP_ID);
                Toast.makeText(getActivity(), Latitude, Toast.LENGTH_SHORT).show();
                letsdoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        //mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner);

        mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, (LocationListener) mLocationListner);
        mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, (LocationListener) mLocationListner);
    }
*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Locationget Succesffully", Toast.LENGTH_SHORT).show();
                //getWeatherForCurrentLocation();
            } else {
                //user denied the permission
            }
        }


    }

/*
    private void letsdoSomeNetworking(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(getContext(), "Data Get Success", Toast.LENGTH_SHORT).show();

                if (mainCityWeatherResult == null) {
                    mainCityWeatherResult = WeatherResult.fromJson(response);
                    updateUI(mainCityWeatherResult);
                    super.onSuccess(statusCode, headers, response);
                } else {

                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }

 */

    @SuppressLint("SetTextI18n")
    private void updateUI(WeatherResult weather) {
        mainCityTemp.setText(String.valueOf(weather.getTemp()) + "°C");
        mainCityFeels.setText("City: " + weather.getCityName());
        int resourceID = getResources().getIdentifier(weather.getMicon(), "drawable", getActivity().getPackageName());
        weatherIcon.setImageResource(resourceID);
        mainCityHum.setText("Feels like: " + String.valueOf(weather.getMain().getFeels_like()));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListner);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new WeatherLoader(getContext(), args.getString("queryString"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            Toast.makeText(getActivity(), "Data !!!! : " + data, Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject(data);
            WeatherResult weatherR = new WeatherResult();
            df.setRoundingMode(RoundingMode.UP);
            weatherR.setCityName(jsonObject.getString("name"));
            weatherR.setWeather(new Weather());
            weatherR.getWeather().setId(jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id"));
            weatherR.getWeather().setMain(jsonObject.getJSONArray("weather").getJSONObject(0).getString("main"));
            weatherR.setMicon(WeatherResult.updateWeatherIcon(weatherR.getWeather().getId()));
            weatherR.setMain(new Main());
            weatherR.getMain().setTemp(Double.parseDouble(df.format(jsonObject.getJSONObject("main").getDouble("temp") - 273.15)));
            weatherR.getMain().setFeels_like(Double.parseDouble(df.format(jsonObject.getJSONObject("main").getDouble("feels_like") - 273.15)));
            weatherR.getMain().setHumidity((int) jsonObject.getJSONObject("main").getDouble("humidity"));
            weatherR.getWeather().setDesctiption(jsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));
            WeatherResult.results.add(weatherR);
            weatherR.setWind(new Wind());
            weatherR.getWind().setSpeed(jsonObject.getJSONObject("wind").getDouble("speed"));
            updateUI(weatherR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }


}