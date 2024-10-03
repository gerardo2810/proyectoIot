package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.MasDetallesPedidoActivity;
import com.example.proyecto_iot.admin_restaurante.PedidoDetallesActivity;

import java.util.List;

public class PedidoPreparadoAdapter extends RecyclerView.Adapter<PedidoPreparadoAdapter.PedidoPreparadoViewHolder>{

    private List<Pedido> pedidoList;
    private Context context;

    public PedidoPreparadoAdapter(List<Pedido> pedidoList, Context context) {
        this.pedidoList = pedidoList;
        this.context = context;
    }

    @NonNull
    @Override
    public PedidoPreparadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_item_en_preparacion, parent, false);
        return new PedidoPreparadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoPreparadoAdapter.PedidoPreparadoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);
        holder.tvPedidoName.setText(pedido.getOrderId());
        holder.tvPedidoCantidad.setText(pedido.getCantidad());
        holder.time_remaining.setText(pedido.getTiempo());
        // Evento al hacer clic en la categoría

        // Acción del botón restaurante
        holder.tvVerMas.setOnClickListener(v -> {
            Intent intent = new Intent(context, MasDetallesPedidoActivity.class);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public static class PedidoPreparadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPedidoName;
        TextView tvPedidoCantidad;
        TextView tvVerMas;
        TextView time_remaining;

        public PedidoPreparadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPedidoName = itemView.findViewById(R.id.id_new_pedido);
            tvPedidoCantidad = itemView.findViewById(R.id.cant_productos);
            tvVerMas = itemView.findViewById(R.id.vermas);
            time_remaining = itemView.findViewById(R.id.time_remaining);
        }
    }
}
