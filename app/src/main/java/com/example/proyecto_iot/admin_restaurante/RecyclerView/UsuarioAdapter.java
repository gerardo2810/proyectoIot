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
import com.example.proyecto_iot.admin_restaurante.DetalleUsuarioActivity;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private Context context;

    public UsuarioAdapter(List<Usuario> listaUsuarios, Context context) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;
    }

    @NonNull
    @Override
    public UsuarioAdapter.UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_item_usuario, parent, false);
        return new UsuarioAdapter.UsuarioViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull UsuarioAdapter.UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);
        holder.tvNombre.setText(usuario.getNombre());

        // Evento de clic para abrir los detalles del plato
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleUsuarioActivity.class);
            intent.putExtra("nombre", usuario.getNombre());
            intent.putExtra("edad", usuario.getEdad());
            intent.putExtra("correo", usuario.getCorreo());
            intent.putExtra("dni", usuario.getDni());
            intent.putExtra("telefono", usuario.getTelefono());
            intent.putExtra("cantPedidos", usuario.getCantPedidos());
            intent.putExtra("gastado", usuario.getGastado());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_usuario);
        }
    }

}
