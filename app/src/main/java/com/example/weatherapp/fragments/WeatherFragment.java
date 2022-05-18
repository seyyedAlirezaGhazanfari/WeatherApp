package com.example.weatherapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.DescriptionActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.adaptor.RecyclerviewAdapter;
import com.example.weatherapp.models.City;
import com.example.weatherapp.models.Main;
import com.example.weatherapp.models.Root;
import com.example.weatherapp.models.Sys;
import com.example.weatherapp.models.Weather;
import com.example.weatherapp.models.WeatherList;
import com.example.weatherapp.models.WeatherResult;
import com.example.weatherapp.models.Wind;
import com.example.weatherapp.network.WeatherLoader;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    RelativeLayout bottomRelative;
    Root root = new Root();
    WeatherResult mainCityWeatherResult;
    long delay = 5000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private static final DecimalFormat df = new DecimalFormat("0.");
    ArrayList<WeatherList> list = new ArrayList<>();
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;
    String Location_Provider = LocationManager.GPS_PROVIDER;
    LocationManager mLocationManager;
    LocationListener mLocationListner;
    ArrayList<WeatherResult> weatherResults = new ArrayList<>();

    public WeatherFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check if a Loader is running, if it is, reconnect to it
        if(getActivity().getSupportLoaderManager().getLoader(0)!=null){
            getActivity().getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                Bundle queryBundle = new Bundle();
                queryBundle.putString("q", cityNameOrWidthET.getText().toString());
                queryBundle.putString("cnt", "7");
                WeatherFragment.this.getActivity().getSupportLoaderManager().restartLoader(0, queryBundle, WeatherFragment.this);
            }
        }
    };

    private Runnable coordinate_input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                Bundle queryBundle = new Bundle();
                queryBundle.putString("lat", cityNameOrWidthET.getText().toString());
                queryBundle.putString("lon", heightET.getText().toString());
                queryBundle.putString("cnt", "7");
                WeatherFragment.this.getActivity().getSupportLoaderManager().restartLoader(0, queryBundle, WeatherFragment.this);
            }
        }
    };

    TextWatcher coordinateTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(
                CharSequence s,
                int start,
                int count,
                int after
        ) {
        }

        @Override
        public void onTextChanged(
                final CharSequence s,
                int start,
                int before,
                int count
        ) {
            handler.removeCallbacks(input_finish_checker);
        }

        @Override
        public void afterTextChanged(final Editable s) {
            if (s.length() > 0) {
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(coordinate_input_finish_checker, delay);
            } else {

            }
        }
    };

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(
                CharSequence s,
                int start,
                int count,
                int after
        ) {
        }

        @Override
        public void onTextChanged(
                final CharSequence s,
                int start,
                int before,
                int count
        ) {
            handler.removeCallbacks(input_finish_checker);
        }

        @Override
        public void afterTextChanged(final Editable s) {
            if (s.length() > 0) {
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            } else {

            }
        }
    };


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        // Inflate the layout for this fragment
        RelativeLayout topRelativeLayout = rootView.findViewById(R.id.relative_layout);
        radioGroup = topRelativeLayout.findViewById(R.id.radio_group);
        coorRadioBtn = topRelativeLayout.findViewById(R.id.coorRatioBtn);
        cityRadioBtn = topRelativeLayout.findViewById(R.id.cityNameRatioBtn);

        bottomRelative = rootView.findViewById(R.id.bottentRelativeId);
        weatherIcon = bottomRelative.findViewById(R.id.weatherIconId);

        layoutMainCity = bottomRelative.findViewById(R.id.layoutMainCityId);
        mainCityTemp = layoutMainCity.findViewById(R.id.temperatureId);
        mainCityHum = layoutMainCity.findViewById(R.id.humiId);
        mainCityFeels = layoutMainCity.findViewById(R.id.feelsLikeId);


        cityNameOrWidthET = bottomRelative.findViewById(R.id.locationInputView);
        heightET = bottomRelative.findViewById(R.id.latitudeView);
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
        cityNameOrWidthET.addTextChangedListener(coordinateTextWatcher);
        heightET.addTextChangedListener(coordinateTextWatcher);

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
                cityNameOrWidthET.removeTextChangedListener(textWatcher);
                cityNameOrWidthET.addTextChangedListener(coordinateTextWatcher);

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

                cityNameOrWidthET.removeTextChangedListener(coordinateTextWatcher);
                cityNameOrWidthET.addTextChangedListener(textWatcher);
            }
        });


        recyclerView = bottomRelative.findViewById(R.id.recyclerviewId);
        list = root.getList();

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

        WeatherList weatherList = root.getList().get(position);
        intent.putExtra("cityName", root.getCity().getName());
        int resourceID = getResources().getIdentifier(weatherList.getWeather().getIcon(), "drawable", requireActivity().getPackageName());
        intent.putExtra("iconDesc", resourceID);
        intent.putExtra("tempDesc", String.valueOf(weatherList.getMain().getTemp()));
        intent.putExtra("feelsLikeDesc", String.valueOf(weatherList.getMain().getFeels_like()));
        intent.putExtra("humidityDesc", String.valueOf(weatherList.getMain().getHumidity()));
        intent.putExtra("description", weatherList.getWeather().getDescription());
        startActivity(intent);

    }


    @Override
    public void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    }

/*
    private void getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        letsdoSomeNetworking(params);
    }
 */


    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(getActivity(), "on location changed", Toast.LENGTH_SHORT).show();
                String Latitude1 = String.valueOf(location.getLatitude());
                String Longitude1 = String.valueOf(location.getLongitude());
                //RequestParams params = new RequestParams();
                //params.put("lat", Latitude);
                //params.put("lon", Longitude);
                //params.put("appid", APP_ID);
                Bundle queryBundle1 = new Bundle();
                queryBundle1.putString("lat", Latitude1);
                queryBundle1.putString("lon", Longitude1);
                WeatherFragment.this.getActivity().getSupportLoaderManager().restartLoader(0, queryBundle1, WeatherFragment.this);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    private void updateUI(Root root) {
        WeatherList weatherList = root.getList().get(0);
        mainCityTemp.setText(String.valueOf(weatherList.getMain().getTemp()) + "°C");
        mainCityFeels.setText("City: " + root.getCity().getName());
        int resourceID = getResources().getIdentifier(weatherList.getWeather().getIcon(), "drawable", getActivity().getPackageName());
        int dayID = getResources().getIdentifier("day", "drawable", getActivity().getPackageName());
        int nightID = getResources().getIdentifier("night2", "drawable", getActivity().getPackageName());
        weatherIcon.setImageResource(resourceID);
        String dayAtt = weatherList.getSys().getPod();
        if (dayAtt.matches("d")){
            bottomRelative.setBackground(getResources().getDrawable(dayID));
        }
        else if (dayAtt.matches("n")){
            bottomRelative.setBackground(getResources().getDrawable(nightID));
        }
        //bottomRelative.setBackground(getResources().getIdentifier());
        mainCityHum.setText("Feels like: " + String.valueOf(weatherList.getMain().getFeels_like()));
        adapter = new RecyclerviewAdapter(rootView.getContext(), root.getList(), this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
        return new WeatherLoader(getContext(), args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            if (data == null) {
                return;
            }

            JSONObject result = new JSONObject(data);
            JSONArray days = result.getJSONArray("list");
            root = new Root();
            City city = new City();
            city.setName(result.getJSONObject("city").getString("name"));
            root.setCity(city);

            for(int i = 0; i<7; i++){
                JSONObject jsonObject = ( JSONObject ) days.getJSONObject(i);
                df.setRoundingMode(RoundingMode.UP);
                WeatherList weatherList = new WeatherList();
                weatherList.setMain(new Main(
                        Double.parseDouble(df.format(jsonObject.getJSONObject("main").getDouble("temp") - 273.15)),
                        Double.parseDouble(df.format(jsonObject.getJSONObject("main").getDouble("feels_like") - 273.15)),
                        (int) jsonObject.getJSONObject("main").getDouble("humidity")
                ));
                weatherList.setWeather(new Weather(
                        jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id"),
                        jsonObject.getJSONArray("weather").getJSONObject(0).getString("main"),
                        jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
                ));
                weatherList.getWeather().setIcon(WeatherResult.updateWeatherIcon(weatherList.getWeather().getId()));
                weatherList.setWind(new Wind(
                        jsonObject.getJSONObject("wind").getDouble("speed")
                ));
                weatherList.setSys(new Sys());
                weatherList.getSys().setPod(jsonObject.getJSONObject("sys").getString("pod"));
                root.getList().add(weatherList);
            }
            updateUI(root);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}