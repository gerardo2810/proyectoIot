package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.MasDetallesPedidoActivity;
import com.example.proyecto_iot.admin_restaurante.PedidoDetallesActivity;

import java.util.List;

public class PedidoEntregadoAdapter extends RecyclerView.Adapter<PedidoEntregadoAdapter.PedidoEntregadoViewHolder> {

    private List<Pedido> pedidoList;
    private Context context;

    public PedidoEntregadoAdapter(List<Pedido> pedidoList, Context context) {
        this.pedidoList = pedidoList;
        this.context = context;
    }

    @NonNull
    @Override
    public PedidoEntregadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurante_item_por_entregar, parent, false);
        return new PedidoEntregadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoEntregadoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        // Bind data to views
        holder.tvPedidoName.setText("#" + pedido.getIdCliente());
        holder.tvPedidoCantidad.setText(pedido.getProductos().size() + " productos");
        holder.tvRepartidor.setText(pedido.getIdRepartidor().isEmpty() ? "Sin Repartidor" : "Repartidor Asignado");
        holder.tvPedidoCliente.setText(pedido.getIdCliente());

        // Enable or disable the "Delivered" button based on repartidor status
        if (pedido.getIdRepartidor().isEmpty()) {
            holder.btnReadyToDeliver.setEnabled(false);
            holder.btnReadyToDeliver.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorInhabilitado)));
        } else {
            holder.btnReadyToDeliver.setEnabled(true);
            holder.btnReadyToDeliver.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorHabilitado)));
        }

        // Handle button click for marking order as delivered
        holder.btnReadyToDeliver.setOnClickListener(v -> {
            // Remove order from list and notify adapter
            pedidoList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, pedidoList.size());

            // Show confirmation message
            Toast.makeText(context, "Pedido entregado", Toast.LENGTH_SHORT).show();
        });

        // Open order details when clicked
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, MasDetallesPedidoActivity.class);
            intent.putExtra("pedidoId", pedido.getIdCliente());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public static class PedidoEntregadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPedidoName, tvPedidoCantidad, tvRepartidor, tvPedidoCliente;
        LinearLayout linearLayout;
        Button btnReadyToDeliver;

        public PedidoEntregadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPedidoCantidad = itemView.findViewById(R.id.cant_productos);
            tvRepartidor = itemView.findViewById(R.id.repartidor);
            tvPedidoCliente = itemView.findViewById(R.id.cliente);
            linearLayout = itemView.findViewById(R.id.newpedido2);
            btnReadyToDeliver = itemView.findViewById(R.id.btn_ready_to_deliver);
        }
    }
}

