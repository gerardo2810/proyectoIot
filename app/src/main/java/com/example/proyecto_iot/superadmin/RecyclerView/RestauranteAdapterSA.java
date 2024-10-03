package com.example.proyecto_iot.superadmin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class RestauranteAdapterSA extends RecyclerView.Adapter<RestauranteAdapterSA.RestauranteSAViewHolder> {

    private List<RestauranteSA> restaurantes;

    public RestauranteAdapterSA(List<RestauranteSA> restaurantes) {
        this.restaurantes = restaurantes;
    }

    @NonNull
    @Override
    public RestauranteAdapterSA.RestauranteSAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.superadmin_item_restaurantes, parent, false);
        return new RestauranteAdapterSA.RestauranteSAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestauranteAdapterSA.RestauranteSAViewHolder holder, int position) {
        RestauranteSA restaurante = restaurantes.get(position);

        holder.textViewRestauranteVerReporte.setText(restaurante.getNombre());
    }

    @Override
    public int getItemCount() {
        return restaurantes.size();
    }

    public static class RestauranteSAViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRestauranteVerReporte;

        public RestauranteSAViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRestauranteVerReporte = itemView.findViewById(R.id.textViewRestauranteVerReporte);
        }
    }

}
