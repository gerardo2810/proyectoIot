package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;


    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_activity_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText(order.getOrderId());
        holder.tvOrderAddress.setText(order.getDireccion());
        holder.tvOrderPrice.setText(order.getPrecio());
        holder.tvOrderStatus.setText(order.getEstado());
        holder.tvOrderCliente.setText(order.getCliente());

        // Cambiar el color de fondo dependiendo del estado de la orden
        switch (order.getEstado()) {
            case "ENTREGADO":
                holder.tvOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.order_delivered));
                break;
            case "EN PREPARACIÓN":
                holder.tvOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.order_in_preparation));
                break;
            case "EN CAMINO":
                holder.tvOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.order_on_the_way));
                break;
            case "EN TIENDA":
                holder.tvOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.order_in_store));
                break;
            default:
                holder.tvOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.default_order_background));
                break;
        }

        // Evento de clic para abrir los detalles del plato
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleOrdenActivity.class);
            intent.putExtra("estado", order.getEstado());
            intent.putExtra("orderId", order.getOrderId());
            intent.putExtra("date", order.getDate());
            intent.putExtra("cliente", order.getCliente());
            intent.putExtra("direccion", order.getDireccion());
            intent.putExtra("precio", order.getPrecio());
            intent.putExtra("repartidor", order.getRepartidor());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderAddress, tvOrderPrice, tvOrderStatus, tvOrderCliente;
        ImageView ivOrderDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderAddress = itemView.findViewById(R.id.tv_order_address);
            tvOrderPrice = itemView.findViewById(R.id.tv_order_price);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            ivOrderDetails = itemView.findViewById(R.id.iv_order_details);
            tvOrderCliente = itemView.findViewById(R.id.tv_order_cliente);
        }

    }
}