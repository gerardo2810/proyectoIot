package com.example.proyecto_iot.superadmin.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.info_usuario_superadmin;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsuarioAdapterSA extends RecyclerView.Adapter<UsuarioAdapterSA.UsuarioSAViewHolder> {

    private List<UsuarioSA> usuarios;
    private Context context;

    public UsuarioAdapterSA(List<UsuarioSA> usuarios, Context context) {
        this.usuarios = usuarios;
        this.context = context;
    }

    public void setUsuarios(List<UsuarioSA> usuarios) {
        this.usuarios = usuarios;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsuarioSAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.superadmin_item_usuario, parent, false);
        return new UsuarioSAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioSAViewHolder holder, int position) {
        UsuarioSA usuario = usuarios.get(position);
        holder.nombre.setText(usuario.getNombre());
        holder.rol.setText(usuario.getRol());
        holder.estado.setText(usuario.getEstado());
        //Picasso.get().load(usuario.getFoto()).into(holder.foto);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, info_usuario_superadmin.class);
            intent.putExtra("usuario_id", usuario.getId());
            intent.putExtra("rol", usuario.getRol());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class UsuarioSAViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, rol, estado;
        //ImageView foto;

        public UsuarioSAViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textViewNombre);
            rol = itemView.findViewById(R.id.textViewRol);
            estado = itemView.findViewById(R.id.textViewEstadoCuenta);
            //foto = itemView.findViewById(R.id.imageViewFoto);
        }
    }

}
