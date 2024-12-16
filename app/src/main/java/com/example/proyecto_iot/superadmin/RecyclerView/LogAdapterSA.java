package com.example.proyecto_iot.superadmin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LogAdapterSA extends RecyclerView.Adapter<LogAdapterSA.LogsSAViewHolder>{

    private List<LogSA> logsList;
    private FirebaseFirestore firestore;

    public LogAdapterSA(List<LogSA> logsList) {
        this.logsList = logsList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public LogsSAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.superadmin_item_logs, parent, false);
        return new LogsSAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogsSAViewHolder holder, int position) {
        LogSA log = logsList.get(position);

        // Configurar el mensaje
        holder.textViewMensaje.setText(log.getMensaje());

        // Configurar el rol
        holder.textViewRol.setText(log.getRol());

        // Configurar fecha y hora
        holder.textViewFechayHora.setText(log.getFecha() + "/" + log.getHora());

        // Verificar si el rol es null
        String rol = log.getRol();
        if (rol != null) {
            holder.textViewRol.setText(rol);

            // Configurar la imagen según el rol
            switch (rol) {
                case "Super Administrador":
                    holder.textViewImage.setImageResource(R.drawable.superadmin_icon);
                    break;
                case "Administrador":
                    holder.textViewImage.setImageResource(R.drawable.admin_icon);
                    break;
                case "Repartidor":
                    holder.textViewImage.setImageResource(R.drawable.repartidor_superadmin);
                    break;
                case "Cliente":
                    holder.textViewImage.setImageResource(R.drawable.cliente_icon);
                    break;
                default:
                    holder.textViewImage.setImageResource(R.drawable.placeholder); // Imagen por defecto
                    break;
            }
            }

        // Obtener el nombre del usuario según el rol
        firestore.collection(getCollectionByRole(log.getRol()))
                .document(log.getUsuarioUID())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        holder.textViewUser.setText(nombre != null ? nombre : "Usuario desconocido");
                    } else {
                        holder.textViewUser.setText("Usuario no encontrado");
                    }
                })
                .addOnFailureListener(e -> holder.textViewUser.setText("Error al cargar"));
    }

    @Override
    public int getItemCount() {
        return logsList.size();
    }

    // Método para obtener la colección según el rol
    private String getCollectionByRole(String rol) {
        switch (rol) {
            case "Cliente":
                return "clientes";
            case "Administrador":
                return "administradores";
            case "Repartidor":
                return "repartidores";
            case "Super Administrador":
                return "superadmin";
            default:
                return "";
        }
    }

    // Clase ViewHolder
    public static class LogsSAViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMensaje, textViewUser, textViewRol, textViewFechayHora;
        ImageView textViewImage;

        public LogsSAViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMensaje = itemView.findViewById(R.id.textViewMensaje);
            textViewUser = itemView.findViewById(R.id.textViewUser);
            textViewRol = itemView.findViewById(R.id.textViewRol);
            textViewFechayHora = itemView.findViewById(R.id.textViewFechayHora);
            textViewImage = itemView.findViewById(R.id.textViewImage);
        }
    }

}
