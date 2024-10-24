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

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>{

    private List<Pedido> pedidoList;
    private Context context;

    public PedidoAdapter(List<Pedido> pedidoList, Context context) {
        this.pedidoList = pedidoList;
        this.context = context;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_item_por_aceptar, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoAdapter.PedidoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);
        holder.tvPedidoName.setText(pedido.getOrderId());
        holder.tvPedidoCantidad.setText(pedido.getCantidad());
        holder.tvPedidoPrecio.setText(pedido.getPrecio());
        holder.tvPedidoCliente.setText(pedido.getCliente());
        // Evento al hacer clic en la categoría

        // Acción del botón restaurante
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, PedidoDetallesActivity.class);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView tvPedidoName;
        TextView tvPedidoCantidad;
        TextView tvPedidoPrecio;
        TextView tvPedidoCliente;
        LinearLayout linearLayout;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPedidoName = itemView.findViewById(R.id.id_new_pedido);
            tvPedidoCantidad = itemView.findViewById(R.id.cant_productos);
            tvPedidoPrecio = itemView.findViewById(R.id.total);
            tvPedidoCliente = itemView.findViewById(R.id.cliente);
            linearLayout = itemView.findViewById(R.id.newpedido1);
        }
    }

}
