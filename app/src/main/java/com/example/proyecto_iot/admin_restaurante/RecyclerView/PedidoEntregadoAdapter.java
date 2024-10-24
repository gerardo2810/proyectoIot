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

public class PedidoEntregadoAdapter extends RecyclerView.Adapter<PedidoEntregadoAdapter.PedidoEntregadoViewHolder>{

    private List<Pedido> pedidoList;
    private Context context;

    public PedidoEntregadoAdapter(List<Pedido> pedidoList, Context context) {
        this.pedidoList = pedidoList;
        this.context = context;
    }

    @NonNull
    @Override
    public PedidoEntregadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_item_por_entregar, parent, false);
        return new PedidoEntregadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoEntregadoAdapter.PedidoEntregadoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);
        holder.tvPedidoName.setText(pedido.getOrderId());
        holder.tvPedidoCantidad.setText(pedido.getCantidad());
        holder.tvRepartidor.setText(pedido.getRepartidor());
        holder.tvPedidoCliente.setText(pedido.getCliente());
        // Evento al hacer clic en la categoría


        // Si el estado del repartidor es "Repartidor Asignado", habilitar el botón
        if (pedido.getRepartidor().equalsIgnoreCase("Repartidor Asignado")) {
            holder.btnReadyToDeliver.setEnabled(true);
            holder.btnReadyToDeliver.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorHabilitado)));
        } else {
            holder.btnReadyToDeliver.setEnabled(false);
            holder.btnReadyToDeliver.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorInhabilitado)));
        }

        // Configurar la acción del botón "Pedido Entregado"
        holder.btnReadyToDeliver.setOnClickListener(v -> {
            // Eliminar el pedido de la lista y notificar al adaptador
            pedidoList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, pedidoList.size());

            // Mostrar un mensaje
            Toast.makeText(context, "Pedido entregado", Toast.LENGTH_SHORT).show();
        });



        switch (pedido.getRepartidor()) {
            case "Repartidor Asignado":
                holder.tvRepartidor.setTextColor(ContextCompat.getColor(context, R.color.con_repartidor));
                break;
            case "Sin Repartidor":
                holder.tvRepartidor.setTextColor(ContextCompat.getColor(context, R.color.sin_repartidor));
                break;
            default:
                holder.tvRepartidor.setTextColor(ContextCompat.getColor(context, R.color.black));
                break;
        }


        // Acción del botón restaurante
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, MasDetallesPedidoActivity.class);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public static class PedidoEntregadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPedidoName;
        TextView tvPedidoCantidad;
        TextView tvRepartidor;
        LinearLayout linearLayout;
        TextView tvPedidoCliente;
        Button btnReadyToDeliver;

        public PedidoEntregadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPedidoName = itemView.findViewById(R.id.id_new_pedido);
            tvPedidoCantidad = itemView.findViewById(R.id.cant_productos);
            tvRepartidor = itemView.findViewById(R.id.repartidor);
            tvPedidoCliente = itemView.findViewById(R.id.cliente);
            linearLayout = itemView.findViewById(R.id.newpedido2);
            btnReadyToDeliver = itemView.findViewById(R.id.btn_ready_to_deliver);
        }
    }
}
