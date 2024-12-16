package com.example.proyecto_iot.cliente.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.SeguimientoPedidoActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Pedido> ordersList;

    public OrderAdapter(Context context, List<Pedido> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Pedido order = ordersList.get(position);

        holder.restaurantName.setText(order.getNombreRestaurante());

        // Asignar texto dependiendo del estado del pedido
        switch (order.getEstado()) {
            case 1:
                holder.orderStatus.setText("Recibido");
                break;
            case 2:
                holder.orderStatus.setText("En preparación");
                break;
            case 3:
                holder.orderStatus.setText("En camino");
                break;
            case 4:
                holder.orderStatus.setText("Entregado");
                break;
            case 7:
                holder.orderStatus.setText("En camino");
            case 8:
                holder.orderStatus.setText("Validado");
            case 5:
                holder.orderStatus.setText("Rechazado");
            case 6:
                holder.orderStatus.setText("Cancelado" );
            default:
                holder.orderStatus.setText("Estado desconocido");
                break;
        }
        // Configurar OnClickListener para enviar todos los datos al abrir la actividad
// Configurar OnClickListener para enviar datos al abrir la actividad
        holder.itemView.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("restaurantes").document(order.getIdRestaurante()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            double precioDelivery = document.contains("precioDelivery") ? document.getDouble("precioDelivery") : 0.0;

                            // Crear el Intent con los datos
                            Intent intent = new Intent(context, SeguimientoPedidoActivity.class);
                            intent.putExtra("pedidoId", order.getIdPedido());
                            intent.putExtra("direccion", order.getDireccion());
                            intent.putExtra("fechaHora", order.getFechaHora());
                            intent.putExtra("precioTotal", order.getPagoTotal());
                            intent.putExtra("precioDelivery", precioDelivery);
                            intent.putExtra("nombreRestaurante", order.getNombreRestaurante());
                            intent.putExtra("estado", order.getEstado());
                            intent.putExtra("idRestaurante", order.getIdRestaurante());
                            intent.putExtra("idRepartidor", order.getIdRepartidor());

                            // Convertir productos a un ArrayList serializable y enviarlo
                            ArrayList<HashMap<String, Object>> productos = new ArrayList<>();
                            if (order.getProductos() != null) {
                                for (Producto producto : order.getProductos()) {
                                    HashMap<String, Object> productoMap = new HashMap<>();
                                    productoMap.put("id", producto.getId());
                                    productoMap.put("descripcion", producto.getDescripcion());
                                    productoMap.put("cantidad", producto.getCantidad());
                                    productos.add(productoMap);
                                }
                            }
                            intent.putExtra("productos", productos);

                            // Agregar log para depurar datos
                            Log.d("OrderAdapter", "Intent Data: " + intent.getExtras().toString());

                            // Iniciar la actividad
                            context.startActivity(intent);
                        } else {
                            Log.e("Firestore", "Error al obtener precioDelivery: ", task.getException());
                        }
                    });
        });    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantName, orderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            orderStatus = itemView.findViewById(R.id.tv_order_status);
        }
    }
}
