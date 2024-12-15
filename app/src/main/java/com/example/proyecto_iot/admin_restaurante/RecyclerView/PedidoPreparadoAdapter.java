package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.EnPreparacionFragment;
import com.example.proyecto_iot.admin_restaurante.InicioRestauranteActivity;
import com.example.proyecto_iot.admin_restaurante.MasDetallesPedidoActivity;
import com.example.proyecto_iot.admin_restaurante.PedidoDetallesActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PedidoPreparadoAdapter extends RecyclerView.Adapter<PedidoPreparadoAdapter.PedidoViewHolder> {

    private List<Pedido> pedidoList;
    private Context context;
    private FirebaseFirestore db;

    public PedidoPreparadoAdapter(List<Pedido> pedidoList, Context context) {
        this.pedidoList = pedidoList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance(); // Inicializar Firestore
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurante_item_en_preparacion, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        // Extraer solo la hora de la fecha
        String horaFormateada = obtenerSoloHora(pedido.getFechaHora());
        holder.fechaHora.setText(horaFormateada); // Mostrar solo la hora

        // Mostrar datos básicos del pedido
        holder.cant_productos.setText(pedido.getProductos().size() + " productos");
        holder.tvPedidoCodigo.setText(pedido.getCodigo());
        holder.cliente.setText(pedido.getNombreCliente() + " " +
                (pedido.getApellidoCliente() != null ? pedido.getApellidoCliente() : "Cliente no disponible"));

        // Configurar botón "Pedido Listo"
        holder.btn_ready_to_deliver.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View customLayout = LayoutInflater.from(context).inflate(R.layout.restaurante_custom_pedido_preparado, null);

            TextView tvConfirmMessage = customLayout.findViewById(R.id.tv_confirm_message);
            Button btnConfirm = customLayout.findViewById(R.id.btn_confirm);
            Button btnCancel = customLayout.findViewById(R.id.btn_cancel);

            // Personalizar el mensaje de confirmación
            tvConfirmMessage.setText("¿El pedido de " + pedido.getNombreCliente() + " ya está listo para el delivery?");

            // Crear el diálogo
            AlertDialog dialog = builder.setView(customLayout).create();

            btnConfirm.setOnClickListener(v1 -> {
                cambiarEstadoPedido(pedido, 2);
                dialog.dismiss();
            });

            btnCancel.setOnClickListener(v12 -> dialog.dismiss());
            dialog.show();
        });

        holder.verMasDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, PedidoDetallesActivity.class);
            intent.putExtra("pedidoId", pedido.getId());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    private void cambiarEstadoPedido(Pedido pedido, int nuevoEstado) {
        if (pedido == null || pedido.getId() == null) {
            Toast.makeText(context, "Pedido no válido o ID no disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("pedidos").document(pedido.getId())
                .update("estado", nuevoEstado)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "El pedido ahora está listo para el delivery.", Toast.LENGTH_SHORT).show();
                    pedidoList.remove(pedido);
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al actualizar el estado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView fechaHora, cliente, cant_productos, verMasDetalles;
        Button btn_ready_to_deliver;
        TextView tvPedidoCodigo;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPedidoCodigo = itemView.findViewById(R.id.codID);
            fechaHora = itemView.findViewById(R.id.fecha_hora);
            cliente = itemView.findViewById(R.id.cliente);
            cant_productos = itemView.findViewById(R.id.cant_productos);
            verMasDetalles = itemView.findViewById(R.id.vermas);
            btn_ready_to_deliver = itemView.findViewById(R.id.btn_ready_to_deliver);
        }
    }

    private String obtenerSoloHora(String fechaCompleta) {
        try {
            // Formato de entrada: "15 de diciembre de 2024, 03:52:01 a. m."
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, hh:mm:ss a", Locale.getDefault());

            // Formato de salida: "03:52:01 a. m."
            SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());

            Date fecha = formatoEntrada.parse(fechaCompleta); // Parsear la fecha completa
            return formatoHora.format(fecha); // Formatear solo la hora
        } catch (ParseException e) {
            e.printStackTrace();
            return "Hora no disponible";
        }
    }
}


