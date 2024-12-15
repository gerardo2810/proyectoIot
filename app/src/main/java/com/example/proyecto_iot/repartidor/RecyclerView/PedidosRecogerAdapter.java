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

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.repartidor.InicioRepartidorActivity;
import com.example.proyecto_iot.repartidor.NuevoPedidoActivity;

import java.util.List;

public class PedidosRecogerAdapter extends RecyclerView.Adapter<PedidosRecogerAdapter.PedidoRecogerViewHolder>{
    private List<PedidoRecoger> listaPedidosRecoger;
    private Context context;
    private boolean elementosHabilitados;

    public PedidosRecogerAdapter(Context context, List<PedidoRecoger> listaPedidosRecoger, boolean elementosHabilitados) {
        this.listaPedidosRecoger = listaPedidosRecoger;
        this.context = context;
        this.elementosHabilitados = elementosHabilitados;
    }

    @NonNull
    @Override
    public PedidosRecogerAdapter.PedidoRecogerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pedidos_recoger, parent, false);
        return new PedidoRecogerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoRecogerViewHolder holder, int position) {
        PedidoRecoger pedidoRecoger = listaPedidosRecoger.get(position);
        holder.pedidoRecoger = pedidoRecoger;

        ImageView imageViewRestaurante = holder.itemView.findViewById(R.id.imagen_restaurante);
        Glide.with(context)
                .load(pedidoRecoger.getFotoLogo()) // URL del logo
                .placeholder(R.drawable.baseline_file_upload_24) // Imagen temporal mientras carga
                .into(imageViewRestaurante); // Tu ImageView

        TextView textViewRestaurante = holder.itemView.findViewById(R.id.nombre_restaurante);
        textViewRestaurante.setText(pedidoRecoger.getIdRestaurante());
        TextView textViewDireccion = holder.itemView.findViewById(R.id.direccion_pedido);
        textViewDireccion.setText(pedidoRecoger.getDireccion());

        // Deshabilitar clics y botones si los elementos no están habilitados
        holder.itemView.setEnabled(elementosHabilitados);
        holder.itemView.setAlpha(elementosHabilitados ? 1.0f : 0.5f); // Cambiar transparencia para indicar que están deshabilitados


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
            arrowIcon.setOnClickListener(elementosHabilitados ? v -> {
                context = itemView.getContext();
                Intent intent = new Intent(context, NuevoPedidoActivity.class);
                intent.putExtra("idPedido", pedidoRecoger.getIdPedido());
                intent.putExtra("direccion", pedidoRecoger.getDireccion()); // Dirección
                intent.putExtra("direccionRest", pedidoRecoger.getDireccionRest()); // Dirección
                context.startActivity(intent);

            }: null);
        }
    }

}
