package com.example.proyecto_iot.cliente.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.PerfilRestauranteActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestauranteAdapter extends RecyclerView.Adapter<RestauranteAdapter.BestOptionViewHolder> {

    private Context context;
    private List<Restaurante> bestOptionList;

    // Método para actualizar la lista filtrada
    public void updateList(List<Restaurante> newList) {
        bestOptionList = newList;
        notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
    }

    public RestauranteAdapter(Context context, List<Restaurante> bestOptionList) {
        this.context = context;
        this.bestOptionList = bestOptionList;
    }

    @NonNull
    @Override
    public BestOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_best_option, parent, false);
        return new BestOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestOptionViewHolder holder, int position) {
        Restaurante option = bestOptionList.get(position);

        // Asignar datos al ViewHolder
        holder.productName.setText(option.getNombre());
        holder.productPrice.setText("S/. " + option.getPrecioDelivery());
        holder.productCategory.setText(option.getTipoDeComida());
        holder.productLocation.setText(option.getUbicacion());

        // Cargar fotoLogo en productImage1 usando Glide
        Glide.with(context)
                .load(option.getFotoLogo())
                .placeholder(R.drawable.placeholder) // Ícono por defecto
                .into(holder.productImage1);

        // Cargar fotoPortada en productImage usando Glide
        Glide.with(context)
                .load(option.getFotoPortada())
                .placeholder(R.drawable.placeholder) // Ícono por defecto
                .into(holder.productImage);

        // Acción al hacer clic en un restaurante
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, PerfilRestauranteActivity.class);

            // Pasar los datos del restaurante seleccionado
            intent.putExtra("nombre_restaurante", option.getNombre());
            intent.putExtra("categoria_restaurante", option.getTipoDeComida());
            intent.putExtra("precio_delivery", option.getPrecioDelivery());
            intent.putExtra("direccion_restaurante", option.getUbicacion());
            intent.putExtra("foto_logo", option.getFotoLogo());
            intent.putExtra("foto_portada", option.getFotoPortada());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bestOptionList.size();
    }

    public static class BestOptionViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage, productImage1;
        TextView productName, productPrice, productCategory, productLocation;
        LinearLayout linearLayout;

        public BestOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image); // Foto portada
            productImage1 = itemView.findViewById(R.id.product_image1); // Foto logo
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productCategory = itemView.findViewById(R.id.product_category);
            productLocation = itemView.findViewById(R.id.product_location);
            linearLayout = itemView.findViewById(R.id.item_opcion_restaurante);
        }
    }
}
