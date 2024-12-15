package com.example.proyecto_iot.repartidor.RecyclerView;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;


import java.util.List;

public class GananciasDiaAdapter extends RecyclerView.Adapter<GananciasDiaAdapter.GananciaxDiaViewHolder>{
    private List<GananciaxDia> listaGanancias;
    private Context context;

    public GananciasDiaAdapter(Context context, List<GananciaxDia> listaGanancias) {
        this.listaGanancias = listaGanancias;
        this.context = context;
    }

    @NonNull
    @Override
    public GananciasDiaAdapter.GananciaxDiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ganancia_dia, parent, false);
        return new GananciaxDiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GananciasDiaAdapter.GananciaxDiaViewHolder holder, int position) {
        GananciaxDia gananciaxDia = listaGanancias.get(position);
        holder.gananciaxDia = gananciaxDia;

        TextView textViewFecha = holder.itemView.findViewById(R.id.fecha);
        textViewFecha.setText(gananciaxDia.getFecha());
        TextView textViewNombreRestaurante = holder.itemView.findViewById(R.id.nombre_restaurante);
        textViewNombreRestaurante.setText(gananciaxDia.getNombreRestaurante());

    }

    @Override
    public int getItemCount(){
        return listaGanancias.size();
    }

    public static class GananciaxDiaViewHolder extends RecyclerView.ViewHolder{
        GananciaxDia gananciaxDia;
        public GananciaxDiaViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }


}
