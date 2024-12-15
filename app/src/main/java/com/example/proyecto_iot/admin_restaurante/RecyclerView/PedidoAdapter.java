package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.PedidoDetallesActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Pedido> pedidoList;
    private Context context;
    private FirebaseFirestore db;

    // Constructor
    public PedidoAdapter(List<Pedido> pedidoList, Context context) {
        this.pedidoList = pedidoList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance(); // Inicializar Firestore
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurante_item_por_aceptar, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        if (pedido == null) {
            Log.e("PedidoAdapter", "El pedido en la posición " + position + " es nulo.");
            return;
        }

        if (pedido.getId() == null) {
            Log.e("PedidoAdapter", "El pedido en la posición " + position + " no tiene ID.");
        } else {
            Log.d("PedidoAdapter", "El ID del pedido en la posición " + position + " es: " + pedido.getId());
        }

        holder.tvPedidoCodigo.setText(pedido.getCodigo());
        holder.tvPedidoCantidad.setText(pedido.getProductos() != null ? pedido.getProductos().size() + " productos" : "0 productos");
        holder.tvPedidoPrecio.setText(String.format("S/%.2f", pedido.getPagoTotal()));
        holder.tvPedidoCliente.setText(pedido.getNombreCliente() + " " + pedido.getApellidoCliente()!= null ? pedido.getNombreCliente() + " " + pedido.getApellidoCliente() : "Cliente no disponible");


        // Manejar el clic para enviar el ID del pedido
        holder.linearLayout.setOnClickListener(v -> {
            if (pedido.getId() == null) {
                Log.e("PedidoAdapter", "No se puede enviar el pedido porque el ID es nulo.");
                Toast.makeText(context, "Error: El ID del pedido no está disponible.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, PedidoDetallesActivity.class);
            intent.putExtra("pedidoId", pedido.getId()); // Enviar el ID del pedido
            Log.d("PedidoAdapter", "Se envía el pedido con ID: " + pedido.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    // ViewHolder Class
    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPedidoCliente;
        TextView tvPedidoCodigo;
        TextView tvPedidoCantidad;
        TextView tvPedidoPrecio;
        LinearLayout linearLayout;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPedidoCodigo = itemView.findViewById(R.id.codID);
            tvPedidoCliente = itemView.findViewById(R.id.cliente);
            tvPedidoCantidad = itemView.findViewById(R.id.cant_productos);
            tvPedidoPrecio = itemView.findViewById(R.id.total);
            linearLayout = itemView.findViewById(R.id.newpedido1);
        }
    }
}

