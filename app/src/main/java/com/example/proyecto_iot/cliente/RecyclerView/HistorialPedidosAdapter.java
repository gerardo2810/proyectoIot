package com.example.proyecto_iot.cliente.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.DetallesPedidoActivity;

import java.util.List;

public class HistorialPedidosAdapter extends RecyclerView.Adapter<HistorialPedidosAdapter.PedidoViewHolder> {

    private List<Pedido> pedidos;
    private Context context;  // Para iniciar la nueva actividad

    public HistorialPedidosAdapter(List<Pedido> pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;  // Guardar el contexto
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido_historial, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.textRestaurantName.setText(pedido.getNombreRestaurante());
        holder.textOrderStatus.setText(pedido.getEstado() + " - " + pedido.getFecha());

        // Configurar el click listener para la flecha
        holder.forwardArrow.setOnClickListener(v -> {
            // Crear el Intent para navegar a DetallesPedidoActivity
            Intent intent = new Intent(context, DetallesPedidoActivity.class);
            // Pasar información del pedido si es necesario
            intent.putExtra("pedido_id", pedido.getPedidoId());
            intent.putExtra("nombre_restaurante", pedido.getNombreRestaurante());
            // Iniciar la actividad
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView textRestaurantName, textOrderStatus;
        ImageView forwardArrow;  // Referencia a la flecha

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textRestaurantName = itemView.findViewById(R.id.text_restaurant_name);
            textOrderStatus = itemView.findViewById(R.id.text_order_status);
            forwardArrow = itemView.findViewById(R.id.forward_arrow);  // Inicializar la flecha
        }
    }
}
