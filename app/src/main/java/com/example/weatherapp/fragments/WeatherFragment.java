package com.example.weatherapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.Gravity;
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

import com.example.weatherapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {

    View rootView;
    RadioGroup radioGroup;
    RadioButton coorRadioBtn;
    RadioButton cityRadioBtn;
    ImageView weatherIcon;
    RecyclerView recyclerView;
    EditText cityNameOrWidthET;
    EditText heightET;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //* @param param1 Parameter 1.
     //* @param param2 Parameter 2.
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
        cityNameOrWidthET = bottomRelative.findViewById(R.id.citynameOrWidthET_Id);
        heightET = bottomRelative.findViewById(R.id.heightET_Id);

        //recyclerView = bottomRelative.findViewById(R.id.recyclerviewId);

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
                Toast.makeText(getActivity(),coorRadioBtn.getText().toString(),Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),cityRadioBtn.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

}