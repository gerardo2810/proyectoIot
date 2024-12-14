package com.example.proyecto_iot.superadmin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RestReportAdapter extends RecyclerView.Adapter<RestReportAdapter.RestauranteViewHolder> {
    private List<RestauranteReporte> restaurantes;
    private FirebaseFirestore db;
    private OnItemClickListener listener;

    // Interfaz para manejar clics en los ítems
    public interface OnItemClickListener {
        void onItemClick(String restauranteUID);
    }

    public RestReportAdapter(List<RestauranteReporte> restaurantes, OnItemClickListener listener) {
        this.restaurantes = restaurantes;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RestauranteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.superadmin_item_restaurantes, parent, false);
        return new RestauranteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestauranteViewHolder holder, int position) {
        RestauranteReporte restaurante = restaurantes.get(position);
        holder.textViewNombreRestaurante.setText(restaurante.getNombre());

        // Cargar la imagen desde el URL con Glide
        Glide.with(holder.itemView.getContext())
                .load(restaurante.getFoto())
                .placeholder(R.drawable.placeholder) // Imagen mientras se carga el recurso
                .error(R.drawable.platos_sa) // Imagen en caso de error
                .into(holder.imageViewTipoReporte);

        // Buscar administrador por UID
        db.collection("administradores").document(restaurante.getIdAdministrador())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        holder.textViewAdminRestaurante.setText(documentSnapshot.getString("nombre") + " " + documentSnapshot.getString("apellido"));
                    } else {
                        holder.textViewAdminRestaurante.setText("Sin administrador encargado");
                    }
                })
                .addOnFailureListener(e -> holder.textViewAdminRestaurante.setText("Error al cargar admin"));

        // Manejar clic en el ítem
        holder.itemView.setOnClickListener(v -> listener.onItemClick(restaurante.getUid()));
    }

    @Override
    public int getItemCount() {
        return restaurantes.size();
    }

    // ViewHolder interno
    public static class RestauranteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombreRestaurante, textViewAdminRestaurante;
        ImageView imageViewTipoReporte;

        public RestauranteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreRestaurante = itemView.findViewById(R.id.textViewNombreRestaurante);
            textViewAdminRestaurante = itemView.findViewById(R.id.textViewAdminRestaurante);
            imageViewTipoReporte = itemView.findViewById(R.id.imageViewTipoReporte);
        }
    }
}
