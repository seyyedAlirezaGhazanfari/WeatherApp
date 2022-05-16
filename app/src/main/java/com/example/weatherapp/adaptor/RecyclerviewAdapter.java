package com.example.weatherapp.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.models.Weather;
import com.example.weatherapp.models.WeatherResult;

import java.util.ArrayList;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {
    ArrayList<WeatherResult> weathers_in_weak;
    private LayoutInflater inflater;
    OnNoteListener onNoteListener;

    public RecyclerviewAdapter(Context context, ArrayList<WeatherResult> weathers_in_weak, OnNoteListener onNoteListener) {
        this.weathers_in_weak = weathers_in_weak;
        this.inflater = LayoutInflater.from(context);
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.recycler_row, parent, false), onNoteListener);
    }

    @Override
    public int getItemCount() {
        return weathers_in_weak.size();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.temperature.setText(String.valueOf(weathers_in_weak.get(position).getMain().getTemp()) + "°C");
        holder.feelsLike.setText("Feels like: " + String.valueOf(weathers_in_weak.get(position).getMain().getFeels_like()));
        holder.cityName.setText("Humidity: " + String.valueOf(weathers_in_weak.get(position).getMain().getHumidity()));

        Context context = inflater.getContext();
        Resources resources = context.getResources();
        int resourceID=resources.getIdentifier(weathers_in_weak.get(position).getMicon(),"drawable",context.getPackageName());
        holder.icon.getContext();
        holder.icon.setImageResource(resourceID);
    }

    @SuppressLint("NotifyDataSetChanged")
    void addItems(ArrayList<WeatherResult> w) {
        weathers_in_weak.addAll(w);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout recRowLayout;
        TextView temperature;
        TextView feelsLike;
        TextView humidity;
        ImageView icon;
        TextView cityName;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            recRowLayout = itemView.findViewById(R.id.recRowLayout);
            temperature = recRowLayout.findViewById(R.id.temperatureRecId);
            feelsLike = recRowLayout.findViewById(R.id.feelsLikeRecId);
            cityName = recRowLayout.findViewById(R.id.humiRecId);
            icon = itemView.findViewById(R.id.imgRecId);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.OnNoteListener(icon, temperature, feelsLike, humidity, getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void OnNoteListener(ImageView icon, TextView temp, TextView feelsLike, TextView humidity, int position);
    }
}
