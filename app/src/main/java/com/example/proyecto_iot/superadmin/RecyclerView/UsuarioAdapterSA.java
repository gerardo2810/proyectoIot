package com.example.proyecto_iot.superadmin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.proyecto_iot.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsuarioAdapterSA extends RecyclerView.Adapter<UsuarioAdapterSA.UsuarioSAViewHolder> {

    private List<UsuarioSA> usuarios;

    public UsuarioAdapterSA(List<UsuarioSA> usuarios) {
        this.usuarios = usuarios;
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
        String nombre_completo = usuario.getNombre() + ' ' + usuario.getApellido();

        holder.textViewNombre.setText(nombre_completo);
        holder.textViewRol.setText(usuario.getRol());
        holder.textViewEstadoCuenta.setText(usuario.getEstado());
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class UsuarioSAViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewRol, textViewEstadoCuenta;

        public UsuarioSAViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewRol = itemView.findViewById(R.id.textViewRol);
            textViewEstadoCuenta = itemView.findViewById(R.id.textViewEstadoCuenta);
        }
    }

}
