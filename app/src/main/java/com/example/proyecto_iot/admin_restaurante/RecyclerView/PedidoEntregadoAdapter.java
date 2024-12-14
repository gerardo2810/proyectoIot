package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.MasDetallesPedidoActivity;
import com.example.proyecto_iot.admin_restaurante.PedidoDetallesActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PedidoEntregadoAdapter extends RecyclerView.Adapter<PedidoEntregadoAdapter.PedidoEntregadoViewHolder> {

    private List<Pedido> pedidoList;
    private Context context;
    private FirebaseFirestore db;

    public PedidoEntregadoAdapter(List<Pedido> pedidoList, Context context) {
        this.pedidoList = pedidoList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance(); // Inicializar Firestore
    }

    @NonNull
    @Override
    public PedidoEntregadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurante_item_por_entregar, parent, false);
        return new PedidoEntregadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoEntregadoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        // Asignar datos iniciales al ViewHolder
        holder.tvFechaHora.setText(pedido.getFechaHora());
        holder.tvCantidadProductos.setText(pedido.getProductos().size() + " productos");
        holder.tvTotal.setText("S/. " + pedido.getPagoTotal());

        // Repartidor
        holder.tvRepartidor.setText(pedido.getNombreRepartidor());
        if (!pedido.isRepartidorAsignado()) {
            holder.tvRepartidor.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            holder.tvRepartidor.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        }

        // Obtener el nombre del cliente desde Firestore si no está disponible
        if (pedido.getNombreCliente() == null || pedido.getNombreCliente().isEmpty()) {
            db.collection("clientes").document(pedido.getIdCliente())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreCliente = documentSnapshot.getString("Nombre");
                            if (nombreCliente != null) {
                                holder.tvCliente.setText(nombreCliente);
                                pedido.setNombreCliente(nombreCliente); // Actualizar el pedido para evitar futuras consultas
                            } else {
                                holder.tvCliente.setText("Cliente desconocido");
                            }
                        } else {
                            holder.tvCliente.setText("Cliente no encontrado");
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.tvCliente.setText("Error al obtener cliente");
                    });
        } else {
            // Si el nombre del cliente ya está disponible, simplemente lo mostramos
            holder.tvCliente.setText(pedido.getNombreCliente());
        }
    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public static class PedidoEntregadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaHora, tvCliente, tvCantidadProductos, tvTotal, tvRepartidor;

        public PedidoEntregadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaHora = itemView.findViewById(R.id.fecha_hora);
            tvCliente = itemView.findViewById(R.id.cliente);
            tvCantidadProductos = itemView.findViewById(R.id.cant_productos);
            tvTotal = itemView.findViewById(R.id.total);
            tvRepartidor = itemView.findViewById(R.id.repartidor);
        }
    }
}
