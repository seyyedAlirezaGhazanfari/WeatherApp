package com.example.weatherapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.DescriptionActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.adaptor.RecyclerviewAdapter;
import com.example.weatherapp.models.Main;
import com.example.weatherapp.models.Weather;
import com.example.weatherapp.models.WeatherResult;
import com.example.weatherapp.models.Wind;
import com.example.weatherapp.network.WeatherLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class WeatherFragment extends Fragment implements RecyclerviewAdapter.OnNoteListener, LoaderManager.LoaderCallbacks<String> {

    View rootView;
    RadioGroup radioGroup;
    RadioButton coordinationRadioButton;
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
    long delay = 5000;
    long last_text_edit = 0;
    Handler handler = new Handler();
    private static final DecimalFormat df = new DecimalFormat("0.");

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
        if (requireActivity().getSupportLoaderManager().getLoader(0) != null) {
            requireActivity().getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    private final Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                Bundle queryBundle = new Bundle();
                queryBundle.putString("q", cityNameOrWidthET.getText().toString());
                queryBundle.putString("cnt", "7");
                WeatherFragment.this.requireActivity().getSupportLoaderManager().restartLoader(
                        0, queryBundle, WeatherFragment.this
                );
            }
        }
    };

    private final Runnable coordinate_input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                Bundle queryBundle = new Bundle();
                queryBundle.putString("lat", cityNameOrWidthET.getText().toString());
                queryBundle.putString("lon", heightET.getText().toString());
                queryBundle.putString("cnt", "7");
                WeatherFragment.this.requireActivity().getSupportLoaderManager().restartLoader(
                        0, queryBundle, WeatherFragment.this
                );
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
            }
        }
    };


    View.OnClickListener coordinationButtonHandler = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            cityNameOrWidthET.setText("");
            heightET.setText("");
            cityNameOrWidthET.setVisibility(View.VISIBLE);
            heightET.setVisibility(View.VISIBLE);
            cityNameOrWidthET.setHint(R.string.width);
            heightET.setHint(R.string.height);
            cityNameOrWidthET.setInputType(InputType.TYPE_CLASS_NUMBER);
            heightET.setInputType(InputType.TYPE_CLASS_NUMBER);
            Toast.makeText(getActivity(), coordinationRadioButton.getText().toString(), Toast.LENGTH_SHORT).show();
            cityNameOrWidthET.removeTextChangedListener(textWatcher);
            cityNameOrWidthET.addTextChangedListener(coordinateTextWatcher);

        }
    };
    View.OnClickListener cityButtonHandler = new View.OnClickListener() {
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
    };

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        RelativeLayout topRelativeLayout = rootView.findViewById(R.id.relative_layout);
        radioGroup = topRelativeLayout.findViewById(R.id.radio_group);
        coordinationRadioButton = topRelativeLayout.findViewById(R.id.coorRatioBtn);
        cityRadioBtn = topRelativeLayout.findViewById(R.id.cityNameRatioBtn);
        RelativeLayout bottomRelative = rootView.findViewById(R.id.bottentRelativeId);
        weatherIcon = bottomRelative.findViewById(R.id.weatherIconId);
        layoutMainCity = bottomRelative.findViewById(R.id.layoutMainCityId);
        mainCityTemp = layoutMainCity.findViewById(R.id.temperatureId);
        mainCityHum = layoutMainCity.findViewById(R.id.humiId);
        mainCityFeels = layoutMainCity.findViewById(R.id.feelsLikeId);
        cityNameOrWidthET = bottomRelative.findViewById(R.id.locationInputView);
        heightET = bottomRelative.findViewById(R.id.latitudeView);
        recyclerView = bottomRelative.findViewById(R.id.recyclerviewId);
        recyclerView = bottomRelative.findViewById(R.id.recyclerviewId);
        // handlers
        cityNameOrWidthET.addTextChangedListener(coordinateTextWatcher);
        heightET.addTextChangedListener(coordinateTextWatcher);
        coordinationRadioButton.setOnClickListener(coordinationButtonHandler);
        cityRadioBtn.setOnClickListener(cityButtonHandler);
        //adapter
        adapter = new RecyclerviewAdapter(
                rootView.getContext(),
                WeatherResult.results,
                this
        );
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return rootView;
    }

    @Override
    public void OnNoteListener(ImageView icon, TextView temp, TextView feelsLike, TextView humidity, int position) {
        Intent intent = new Intent(
                this.getActivity(),
                DescriptionActivity.class
        );
        WeatherResult weatherResult = WeatherResult.results.get(position);
        intent.putExtra("cityName", weatherResult.getCityName());
        int resourceID = getResources().getIdentifier(
                weatherResult.getMicon(),
                "drawable",
                requireActivity().getPackageName()
        );
        intent.putExtra("iconDesc", resourceID);
        intent.putExtra("tempDesc", String.valueOf(weatherResult.getTemp()));
        intent.putExtra("feelsLikeDesc", String.valueOf(weatherResult.getMain().getFeels_like()));
        intent.putExtra("humidityDesc", String.valueOf(weatherResult.getMain().getHumidity()));
        intent.putExtra("description", weatherResult.getWeather().getDescription());
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
                Toast.makeText(getActivity(), "Location has been gotten Successfully", Toast.LENGTH_SHORT).show();
                //getWeatherForCurrentLocation();
            } else {
                //user denied the permission
            }
        }


    }

    @SuppressLint("SetTextI18n")
    private void updateUI(WeatherResult weather) {
        mainCityTemp.setText(String.valueOf(weather.getTemp()) + "°C");
        mainCityFeels.setText("City: " + weather.getCityName());
        int resourceID = getResources().getIdentifier(
                weather.getMicon(),
                "drawable",
                requireActivity().getPackageName()
        );
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
        return new WeatherLoader(requireContext(), args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            if (data == null) {
                return;
            }
            Toast.makeText(getActivity(), "Data !!!! : " + data, Toast.LENGTH_SHORT).show();

            JSONObject result = new JSONObject(data);
            JSONArray days = result.getJSONArray("list");
            WeatherResult weatherR = new WeatherResult();

            for (int i = 0; i < 5; i++) {
                JSONObject jsonObject = (JSONObject) days.getJSONObject(i);
                df.setRoundingMode(RoundingMode.UP);
                //weatherR.setCityName(jsonObject.getString("name"));
                Weather weather = new Weather(
                        jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id"),
                        jsonObject.getJSONArray("weather").getJSONObject(0).getString("main"),
                        jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
                );
                Main main = new Main(
                        Double.parseDouble(df.format(jsonObject.getJSONObject("main").getDouble("temp") - 273.15)),
                        Double.parseDouble(df.format(jsonObject.getJSONObject("main").getDouble("feels_like") - 273.15)),
                        (int) jsonObject.getJSONObject("main").getDouble("humidity")
                );
                Wind wind = new Wind(
                        jsonObject.getJSONObject("wind").getDouble("speed")
                );
                weatherR.setWeather(weather);
                weatherR.setMicon(WeatherResult.updateWeatherIcon(weatherR.getWeather().getId()));
                weatherR.setMain(main);
                WeatherResult.results.add(weatherR);
                weatherR.setWind(wind);
            }
            updateUI(weatherR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }
}