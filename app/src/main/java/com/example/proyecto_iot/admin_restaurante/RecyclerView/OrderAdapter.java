package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.DetalleOrdenActivity;
import com.example.proyecto_iot.admin_restaurante.InfoPedidoHistorialActivity;
import com.example.proyecto_iot.admin_restaurante.PedidoDetallesActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Pedido> orderList;
    private Context context;
    private FirebaseFirestore db; // Firestore para consultas

    public OrderAdapter(List<Pedido> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance(); // Inicializar Firestore
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurante_activity_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Pedido pedido = orderList.get(position);

        // Asignar datos al ViewHolder
        holder.tvOrderAddress.setText(pedido.getDireccion());
        holder.tvOrderPrice.setText("S/. " + pedido.getPagoTotal());

        // Obtener el nombre del cliente desde Firestore si no está disponible
        if (pedido.getNombreCliente() == null || pedido.getNombreCliente().isEmpty()) {
            db.collection("clientes").document(pedido.getIdCliente())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreCliente = documentSnapshot.getString("Nombre");
                            if (nombreCliente != null) {
                                holder.tvOrderCliente.setText(nombreCliente);
                                pedido.setNombreCliente(nombreCliente); // Actualizar el pedido para evitar futuras consultas
                            } else {
                                holder.tvOrderCliente.setText("Cliente desconocido");
                            }
                        } else {
                            holder.tvOrderCliente.setText("Cliente no encontrado");
                        }
                    })
                    .addOnFailureListener(e -> holder.tvOrderCliente.setText("Error al obtener cliente"));
        } else {
            // Si el nombre del cliente ya está disponible, simplemente lo mostramos
            holder.tvOrderCliente.setText(pedido.getNombreCliente());
        }

        // Estado del pedido
        String estadoText = "";
        int estadoColor = R.color.default_order_background;

        switch (pedido.getEstado()) {
            case 0:
                estadoText = "POR ACEPTAR";
                estadoColor = R.color.blue_light;
                break;
            case 1:
                estadoText = "EN PREPARACIÓN";
                estadoColor = R.color.order_in_preparation;
                break;
            case 2:
                estadoText = "EN TIENDA";
                estadoColor = R.color.order_in_store;
                break;
            case 7:
                estadoText = "EN CAMINO";
                estadoColor = R.color.order_on_the_way;
                break;
            case 4:
                estadoText = "ENTREGADO";
                estadoColor = R.color.order_delivered;
                break;
            case 5:
                estadoText = "RECHAZADO";
                estadoColor = R.color.order_rejected;
                break;
        }
        holder.tvOrderStatus.setText(estadoText);
        holder.tvOrderStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, estadoColor)));

        // Evento clic para abrir los detalles del pedido
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InfoPedidoHistorialActivity.class);
            intent.putExtra("pedidoId", pedido.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCliente, tvOrderAddress, tvOrderPrice, tvOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCliente = itemView.findViewById(R.id.tv_order_cliente);
            tvOrderAddress = itemView.findViewById(R.id.tv_order_address);
            tvOrderPrice = itemView.findViewById(R.id.tv_order_price);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
        }
    }
}