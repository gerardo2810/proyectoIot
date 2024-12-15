package com.example.proyecto_iot.superadmin.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.info_solitud_repartidor_superadmin;

import java.util.List;

public class RepartidorAdapterSA extends RecyclerView.Adapter<RepartidorAdapterSA.RepartidorSAViewHolder> {

    private Context context;
    private List<RepartidorSA> repartidores;

    public RepartidorAdapterSA(Context context,List<RepartidorSA> repartidores) {
        this.context = context;
        this.repartidores = repartidores;
    }

    @NonNull
    @Override
    public RepartidorAdapterSA.RepartidorSAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.superadmin_item_repartidores, parent, false);
        return new RepartidorAdapterSA.RepartidorSAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RepartidorAdapterSA.RepartidorSAViewHolder holder, int position) {
        RepartidorSA repartidor = repartidores.get(position);

        // Asignar datos al ViewHolder
        holder.textViewNombre.setText(repartidor.getNombre() + " " + repartidor.getApellido());
        holder.textViewFecha.setText(repartidor.getFecha());

        // Cargar imagen desde el URL usando Glide
        Glide.with(context)
                .load(repartidor.getFoto()) // Cargar la URL de la foto
                .placeholder(R.drawable.placeholder) // Imagen de carga por defecto
                .into(holder.imageViewFoto); // El ImageView donde se mostrará la foto

        // Configurar redirección al hacer clic en el item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, info_solitud_repartidor_superadmin.class);
            intent.putExtra("document_id", repartidor.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return repartidores.size();
    }

    public static class RepartidorSAViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewFecha;
        ImageView imageViewFoto;

        public RepartidorSAViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            imageViewFoto = itemView.findViewById(R.id.imageViewFoto);
        }
    }

}
