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

import androidx.annotation.NonNull;
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

import java.util.List;

public class PedidoPreparadoAdapter extends RecyclerView.Adapter<PedidoPreparadoAdapter.PedidoViewHolder> {

    private List<Pedido> pedidoList;
    private Context context;
    private OnOrderReadyListener listener;

    public PedidoPreparadoAdapter(List<Pedido> pedidoList, Context context, OnOrderReadyListener listener) {
        this.pedidoList = pedidoList;
        this.context = context;
        this.listener = listener;
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

        // Bind data to views
        holder.cliente.setText(pedido.getIdCliente());
        holder.cant_productos.setText(pedido.getProductos().size() + " productos");
        holder.time_remaining.setText(pedido.getFechaHora()); // Display preparation time

        // Set up the "Ready to Deliver" button
        holder.btn_ready_to_deliver.setOnClickListener(v -> {
            listener.onOrderReady(pedido);
        });
    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView id_new_pedido, cliente, cant_productos, time_remaining;
        Button btn_ready_to_deliver;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            cliente = itemView.findViewById(R.id.cliente);
            cant_productos = itemView.findViewById(R.id.cant_productos);
            time_remaining = itemView.findViewById(R.id.time_remaining);
            btn_ready_to_deliver = itemView.findViewById(R.id.btn_ready_to_deliver);
        }
    }

    public interface OnOrderReadyListener {
        void onOrderReady(Pedido pedido);
    }
}


