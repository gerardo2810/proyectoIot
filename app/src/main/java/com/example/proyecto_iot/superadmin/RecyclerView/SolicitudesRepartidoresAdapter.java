package com.example.proyecto_iot.superadmin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class SolicitudesRepartidoresAdapter extends RecyclerView.Adapter<SolicitudesRepartidoresAdapter.RepartidorSAViewHolder> {

    private List<RepartidorSA> repartidores;

    public SolicitudesRepartidoresAdapter(List<RepartidorSA> repartidores) {
        this.repartidores = repartidores;
    }

    @NonNull
    @Override
    public SolicitudesRepartidoresAdapter.RepartidorSAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.superadmin_item_repartidores, parent, false);
        return new SolicitudesRepartidoresAdapter.RepartidorSAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudesRepartidoresAdapter.RepartidorSAViewHolder holder, int position) {
        RepartidorSA repartidor = repartidores.get(position);
        String nombre_completo = repartidor.getNombre() + ' ' + repartidor.getApellido();

        holder.textViewNombre.setText(nombre_completo);
        holder.textViewFecha.setText(repartidor.getFecha());
    }

    @Override
    public int getItemCount() {
        return repartidores.size();
    }

    public static class RepartidorSAViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewFecha;

        public RepartidorSAViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
        }
    }

}
