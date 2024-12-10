package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.PedidoDetallesActivity;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Pedido> pedidoList;
    private Context context;

    // Constructor
    public PedidoAdapter(List<Pedido> pedidoList, Context context) {
        this.pedidoList = pedidoList;
        this.context = context;
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

        // Bind data
        holder.tvPedidoCliente.setText(pedido.getIdCliente());
        holder.tvPedidoCantidad.setText(pedido.getProductos().size() + " productos");
        holder.tvPedidoPrecio.setText(String.format("S/%.2f", pedido.getPagoTotal()));

        // Handle click event
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, PedidoDetallesActivity.class);
            intent.putExtra("pedidoId", pedido.getIdCliente());
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
        TextView tvPedidoCantidad;
        TextView tvPedidoPrecio;
        LinearLayout linearLayout;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPedidoCliente = itemView.findViewById(R.id.cliente);
            tvPedidoCantidad = itemView.findViewById(R.id.cant_productos);
            tvPedidoPrecio = itemView.findViewById(R.id.total);
            linearLayout = itemView.findViewById(R.id.newpedido1);
        }
    }
}
