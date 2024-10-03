package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.DetallePlatoActivity;

import java.util.List;

public class PlatoAdapter extends RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder> {

    private List<Plato> listaPlatos;
    private Context context;

    public PlatoAdapter(List<Plato> listaPlatos, Context context) {
        this.listaPlatos = listaPlatos;
        this.context = context;
    }

    @NonNull
    @Override
    public PlatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_item_reporte, parent, false);
        return new PlatoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatoViewHolder holder, int position) {
        Plato plato = listaPlatos.get(position);
        holder.tvNombrePlato.setText(plato.getNombre());
        holder.tvCategoriaPlato.setText(plato.getCategoria());

        // Evento de clic para abrir los detalles del plato
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePlatoActivity.class);
            intent.putExtra("nombre", plato.getNombre());
            intent.putExtra("categoria", plato.getCategoria());
            intent.putExtra("descripcion", plato.getDescripcion());
            intent.putExtra("precio", plato.getPrecio());
            intent.putExtra("cantVendida", plato.getCantVendida());
            intent.putExtra("ganancia", plato.getGanancia());
            int imageResId = plato.getImageResId();
            if (imageResId != 0) { // Si el recurso de imagen es válido
                intent.putExtra("imageResId", String.valueOf(imageResId));
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaPlatos.size();
    }

    public static class PlatoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombrePlato;
        TextView tvCategoriaPlato;

        public PlatoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombrePlato = itemView.findViewById(R.id.tv_nombre_plato);
            tvCategoriaPlato = itemView.findViewById(R.id.tv_categoria_plato);
        }
    }
}

