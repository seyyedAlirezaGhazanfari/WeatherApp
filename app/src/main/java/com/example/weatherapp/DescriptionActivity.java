package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DescriptionActivity extends AppCompatActivity {
    TextView tempView;
    TextView feelsLikeView;
    TextView humidityView;
    TextView descriptionView;
    ImageView icon;
    RelativeLayout cityNameLayout;
    TextView cityNameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        icon = findViewById(R.id.weatherDescImageView);
        tempView = findViewById(R.id.descTempView);
        feelsLikeView = findViewById(R.id.descFeelTempView);
        humidityView = findViewById(R.id.descHumidityView);
        descriptionView = findViewById(R.id.descriptionView);
        cityNameLayout = findViewById(R.id.citynameLayoutId);
        cityNameView = cityNameLayout.findViewById(R.id.cityNameDesId);
        Intent intent = getIntent();
        int iconResourceID = intent.getIntExtra("iconDesc", R.drawable.finding);
        String temperature = intent.getStringExtra("tempDesc");
        String feelsLike = intent.getStringExtra("feelsLikeDesc");
        String humidity = intent.getStringExtra("humidityDesc");
        String description = intent.getStringExtra("description");
        String cityName = intent.getStringExtra("cityName");
        icon.setImageResource(iconResourceID);
        tempView.setText(temperature);
        feelsLikeView.setText(feelsLike);
        humidityView.setText(humidity);
        descriptionView.setText(description);
        cityNameView.setText(cityName);
    }
}
