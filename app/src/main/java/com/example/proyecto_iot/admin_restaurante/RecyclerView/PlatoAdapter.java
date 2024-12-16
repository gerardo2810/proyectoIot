package com.example.proyecto_iot.admin_restaurante.RecyclerView;

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
import com.example.proyecto_iot.admin_restaurante.DetallePlatoActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PlatoAdapter extends RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder> {

    private List<Plato> listaPlatos;
    private Context context;
    private FirebaseFirestore db;

    public PlatoAdapter(List<Plato> listaPlatos, Context context) {
        this.listaPlatos = listaPlatos;
        this.context = context;
        db = FirebaseFirestore.getInstance();
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

        // Por ahora mostramos la cantidad vendida directamente del objeto Plato
        holder.tvCantVendida.setText(plato.getCantVendida());

        // Limpia los campos mientras cargamos los datos
        holder.tvNombrePlato.setText("Cargando...");
        holder.tvCategoriaPlato.setText("Cargando categoría...");
        holder.ivPlatoImage.setImageResource(R.drawable.placeholder); // Imagen placeholder

        String idProducto = plato.getIdProducto();
        if (idProducto == null || idProducto.isEmpty()) {
            holder.tvNombrePlato.setText("Sin ID de producto");
            holder.tvCategoriaPlato.setText("");
            return;
        }

        // Consulta a la colección "platos"
        db.collection("platos").document(idProducto).get()
                .addOnSuccessListener(platoDoc -> {
                    if (platoDoc.exists()) {
                        String nombre = platoDoc.getString("Nombre");
                        String imagenUrl = platoDoc.getString("Imagen");
                        String idCategoria = platoDoc.getString("idCategoria");

                        // Actualizamos el nombre y la imagen del plato
                        if (nombre != null) {
                            holder.tvNombrePlato.setText(nombre);
                        } else {
                            holder.tvNombrePlato.setText("Nombre no disponible");
                        }

                        if (imagenUrl != null && !imagenUrl.isEmpty()) {
                            Glide.with(context)
                                    .load(imagenUrl)
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.ivPlatoImage);
                        } else {
                            holder.ivPlatoImage.setImageResource(R.drawable.placeholder);
                        }

                        // Ahora consultamos la categoría
                        if (idCategoria != null && !idCategoria.isEmpty()) {
                            db.collection("categorias").document(idCategoria).get()
                                    .addOnSuccessListener(catDoc -> {
                                        if (catDoc.exists()) {
                                            String nombreCategoria = catDoc.getString("nombre");
                                            if (nombreCategoria != null) {
                                                holder.tvCategoriaPlato.setText(nombreCategoria);
                                            } else {
                                                holder.tvCategoriaPlato.setText("Categoría sin nombre");
                                            }
                                        } else {
                                            holder.tvCategoriaPlato.setText("Categoría no encontrada");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        holder.tvCategoriaPlato.setText("Error al cargar categoría");
                                    });
                        } else {
                            holder.tvCategoriaPlato.setText("Sin categoría");
                        }

                    } else {
                        holder.tvNombrePlato.setText("Plato no encontrado");
                        holder.tvCategoriaPlato.setText("");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.tvNombrePlato.setText("Error al cargar plato");
                    holder.tvCategoriaPlato.setText("");
                });

        // Evento de clic para abrir detalles del plato
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePlatoActivity.class);
            // Pasamos el idProducto para cargar detalles allí
            intent.putExtra("idProducto", plato.getIdProducto());
            intent.putExtra("cantVendida", plato.getCantVendida());
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
        TextView tvCantVendida;
        ImageView ivPlatoImage;

        public PlatoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombrePlato = itemView.findViewById(R.id.tv_nombre_plato);
            tvCategoriaPlato = itemView.findViewById(R.id.tv_categoria_plato);
            tvCantVendida = itemView.findViewById(R.id.cant_pedidos);
            ivPlatoImage = itemView.findViewById(R.id.plato_image);
        }
    }
}
