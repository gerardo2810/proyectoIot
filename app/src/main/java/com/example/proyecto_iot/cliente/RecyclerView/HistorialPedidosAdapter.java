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

import java.util.ArrayList;
import java.util.HashMap;
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

        // Asignar datos al ViewHolder
        holder.textRestaurantName.setText(pedido.getNombreRestaurante());
        holder.textOrderStatus.setText(obtenerEstadoPedido(pedido.getEstado()) + " - " + pedido.getFechaHora());
        holder.productImage.setImageResource(R.drawable.lalucha_inicio); // Asignar imagen por defecto o dinámica

        // Configurar click listener
        holder.itemView.setOnClickListener(v -> {
            // Crear Intent y pasar todos los datos
            Intent intent = new Intent(context, DetallesPedidoActivity.class);
            intent.putExtra("idPedido", pedido.getIdPedido());
            intent.putExtra("nombreRestaurante", pedido.getNombreRestaurante());
            intent.putExtra("estado", pedido.getEstado());
            intent.putExtra("fechaHora", pedido.getFechaHora());
            intent.putExtra("direccion", pedido.getDireccion());
            intent.putExtra("pagoTotal", pedido.getPagoTotal());
            intent.putExtra("idRestaurante", pedido.getIdRestaurante());

            // Convertir lista de productos a ArrayList serializable
            ArrayList<HashMap<String, Object>> productosData = new ArrayList<>();
            for (Producto producto : pedido.getProductos()) {
                HashMap<String, Object> productoMap = new HashMap<>();
                productoMap.put("nombre", producto.getNombre());
                productoMap.put("cantidad", producto.getCantidad());
                productoMap.put("imageUrl", producto.getImageUrl());
                productoMap.put("precio",producto.getPrecio());
                productosData.add(productoMap);
            }
            intent.putExtra("productos", productosData);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView textRestaurantName, textOrderStatus;
        ImageView forwardArrow,productImage;  // Referencia a la flecha

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textRestaurantName = itemView.findViewById(R.id.text_restaurant_name);
            textOrderStatus = itemView.findViewById(R.id.text_order_status);
            forwardArrow = itemView.findViewById(R.id.forward_arrow);  // Inicializar la flecha
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
    private String obtenerEstadoPedido(int estado) {
        switch (estado) {
            case 1: return "Recibido";
            case 2: return "En preparación";
            case 3: return "En camino";
            case 4: return "Entregado";
            default: return "Desconocido";
        }
    }
}
