package com.example.proyecto_iot.repartidor.RecyclerView;

import static androidx.core.content.ContextCompat.startActivity;

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
import com.example.proyecto_iot.repartidor.InicioRepartidorActivity;
import com.example.proyecto_iot.repartidor.NuevoPedidoActivity;

import java.util.List;

public class PedidosRecogerAdapter extends RecyclerView.Adapter<PedidosRecogerAdapter.PedidoRecogerViewHolder>{
    private List<PedidoRecoger> listaPedidosRecoger;
    private Context context;

    public PedidosRecogerAdapter(List<PedidoRecoger> listaPedidosRecoger) {
        this.listaPedidosRecoger = listaPedidosRecoger;
    }

    @NonNull
    @Override
    public PedidosRecogerAdapter.PedidoRecogerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedidos_recoger, parent, false);
        return new PedidoRecogerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoRecogerViewHolder holder, int position) {
        PedidoRecoger pedidoRecoger = listaPedidosRecoger.get(position);
        holder.pedidoRecoger = pedidoRecoger;

        ImageView imageViewRestaurante = holder.itemView.findViewById(R.id.imagen_restaurante);
        imageViewRestaurante.setImageResource(pedidoRecoger.getImageResourceId());
        TextView textViewRestaurante = holder.itemView.findViewById(R.id.nombre_restaurante);
        textViewRestaurante.setText(pedidoRecoger.getNombreRestaurante());
        TextView textViewCantidad = holder.itemView.findViewById(R.id.cantidad_pedido);
        textViewCantidad.setText(pedidoRecoger.getCantidad());
        TextView textViewDireccion = holder.itemView.findViewById(R.id.direccion_pedido);
        textViewDireccion.setText(pedidoRecoger.getDireccion());

    }

    @Override
    public int getItemCount(){
        return listaPedidosRecoger.size();
    }

    public class PedidoRecogerViewHolder extends RecyclerView.ViewHolder{
        PedidoRecoger pedidoRecoger;
        public PedidoRecogerViewHolder(@NonNull View itemView) {
            super(itemView);
            LinearLayout arrowIcon = itemView.findViewById(R.id.linealPedidos);
            arrowIcon.setOnClickListener(v -> {
                context = itemView.getContext();
                Intent intent = new Intent(context, NuevoPedidoActivity.class);
                context.startActivity(intent);

            });
        }
    }

}
