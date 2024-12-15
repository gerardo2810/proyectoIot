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

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.DetalleOrdenActivity;
import com.example.proyecto_iot.admin_restaurante.InfoPedidoHistorialActivity;
import com.example.proyecto_iot.admin_restaurante.PedidoDetallesActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Pedido> orderList;
    private Context context;
    private FirebaseFirestore db;

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

        // Asignar el código del pedido
        holder.tvOrderCodigo.setText(pedido.getCodigo());

        // Mostrar la cantidad de productos
        holder.tvCantPedidos.setText(pedido.getProductos().size() + " productos");

        holder.tvOrderCliente.setText(pedido.getNombreCliente() + " " + pedido.getApellidoCliente());

        // Configurar el estado del pedido
        configurarEstadoPedido(holder.tvOrderStatus, pedido.getEstado());

        // Obtener el nombre del cliente y foto desde Firestore
        if (pedido.getIdCliente() != null) {
            db.collection("clientes").document(pedido.getIdCliente())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreCliente = documentSnapshot.getString("Nombre");
                            String fotoURL = documentSnapshot.getString("FotoURL");

                            // Cargar la imagen usando Glide o Picasso
                            if (fotoURL != null && !fotoURL.isEmpty()) {
                                Glide.with(context)
                                        .load(fotoURL)
                                        .placeholder(R.drawable.placeholder) // Imagen por defecto
                                        .into(holder.clienteImage);
                            } else {
                                holder.clienteImage.setImageResource(R.drawable.placeholder);
                            }
                        } else {
                            holder.tvOrderCliente.setText("Cliente no encontrado");
                            holder.clienteImage.setImageResource(R.drawable.placeholder);
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.tvOrderCliente.setText("Error al obtener cliente");
                        holder.clienteImage.setImageResource(R.drawable.placeholder);
                    });
        }

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

    /**
     * Configura el estado del pedido en el TextView con su color y texto correspondiente.
     */
    private void configurarEstadoPedido(TextView tvOrderStatus, int estado) {
        String estadoText = "";
        int estadoColor = R.color.black;

        switch (estado) {
            case 0:
                estadoText = "Por Aceptar";
                estadoColor = R.color.poraceptar;
                break;
            case 1:
                estadoText = "En preparación";
                estadoColor = R.color.order_in_preparation;
                break;
            case 2:
                estadoText = "En el restaurante";
                estadoColor = R.color.blue;
                break;
            case 3:
                estadoText = "En el restaurante";
                estadoColor = R.color.blue;
                break;
            case 7:
                estadoText = "En Camino";
                estadoColor = R.color.amarillo_estado;
                break;
            case 4:
                estadoText = "Entregado";
                estadoColor = R.color.green;
                break;
            case 5:
                estadoText = "Rechazado";
                estadoColor = R.color.sin_repartidor;
                break;
            case 6:
                estadoText = "Cancelado";
                estadoColor = R.color.md_theme_error_highContrast;
                break;
            case 8:
                estadoText = "En camino";
                estadoColor = R.color.amarillo_estado;
                break;
            default:
                estadoText = "Desconocido";
                estadoColor = R.color.black;
                break;
        }

        tvOrderStatus.setText(estadoText);
        tvOrderStatus.setTextColor(ContextCompat.getColor(context, estadoColor));
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView clienteImage;
        TextView tvOrderCodigo, tvOrderCliente, tvOrderStatus, tvCantPedidos;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            clienteImage = itemView.findViewById(R.id.cliente_image);
            tvOrderCodigo = itemView.findViewById(R.id.tv_order_codigo);
            tvOrderCliente = itemView.findViewById(R.id.tv_order_cliente);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvCantPedidos = itemView.findViewById(R.id.cant_pedidos);
        }
    }
}
