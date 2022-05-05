package com.example.weatherapp.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.models.Weather;

import java.util.ArrayList;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder>{
    ArrayList<Weather> weathers_in_weak;
    private LayoutInflater inflater;
    OnNoteListener onNoteListener;

    public RecyclerviewAdapter( Context context,ArrayList<Weather> weathers_in_weak, OnNoteListener onNoteListener) {
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


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rowTextView.setText(weathers_in_weak.get(position).getName());
    }

    @SuppressLint("NotifyDataSetChanged")
    void addItems(ArrayList<Weather> w) {
        weathers_in_weak.addAll(w);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView rowTextView;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            rowTextView = itemView.findViewById(R.id.recyclerRow_Id);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.OnNoteListener(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void OnNoteListener(int position);
    }
}
