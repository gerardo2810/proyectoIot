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
import com.example.proyecto_iot.admin_restaurante.DetalleUsuarioActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private Context context;
    private FirebaseFirestore db;

    public UsuarioAdapter(List<Usuario> listaUsuarios, Context context) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);

        // Asumiendo que 'usuario.getNombre()' retorna el idCliente
        String idCliente = usuario.getNombre();

        holder.tvCantPedidos.setText(usuario.getCantPedidos() + " pedidos");

        // Consultar la colección "clientes" para obtener la foto
        db.collection("clientes").document(idCliente).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String fotoURL = doc.getString("FotoURL");
                        String nombreC = doc.getString("Nombre");
                        String apellidoC = doc.getString("Apellido");
                        holder.tvNombreCliente.setText(nombreC + " " + apellidoC);
                        if (fotoURL != null && !fotoURL.isEmpty()) {
                            // Cargar imagen con Glide
                            Glide.with(context)
                                    .load(fotoURL)
                                    .placeholder(R.drawable.placeholder) // Ajustar tu placeholder
                                    .into(holder.ivClienteImage);
                        } else {
                            holder.ivClienteImage.setImageResource(R.drawable.placeholder);
                        }
                    } else {
                        // Si no se encuentra el cliente, colocar placeholder
                        holder.ivClienteImage.setImageResource(R.drawable.placeholder);
                    }
                })
                .addOnFailureListener(e -> {
                    // En caso de error, colocar placeholder
                    holder.ivClienteImage.setImageResource(R.drawable.placeholder);
                });

        // Evento de clic para ir a DetalleUsuarioActivity pasando el idCliente y cantPedidos
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleUsuarioActivity.class);
            intent.putExtra("idCliente", idCliente);
            intent.putExtra("cantPedidos", usuario.getCantPedidos());
            intent.putExtra("montoacumulado", usuario.getGastado());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        ImageView ivClienteImage;
        TextView tvNombreCliente, tvCantPedidos;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            ivClienteImage = itemView.findViewById(R.id.cliente_image);
            tvNombreCliente = itemView.findViewById(R.id.tv_nombre_cliente);
            tvCantPedidos = itemView.findViewById(R.id.cant_pedidos);
        }
    }
}
