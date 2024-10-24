package com.example.proyecto_iot.cliente.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
        holder.productName.setText(option.getNameTitlte());
        holder.productPrice.setText("S/. " + option.getProductPrice());
        holder.productCategory.setText(option.getCategory());
        holder.productLocation.setText(option.getLocation());
        // Aquí puedes cargar la imagen con Glide o Picasso si es necesario


        // Asignar la imagen correspondiente
        holder.productImage.setImageResource(option.getImageResourceId());

        // Acción del botón restaurante
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, PerfilRestauranteActivity.class);

            // Pasar los datos del restaurante seleccionado
            intent.putExtra("nombre_restaurante", option.getNameTitlte());
            intent.putExtra("categoria_restaurante", option.getCategory());
            intent.putExtra("precio_delivery", option.getProductPrice());
            intent.putExtra("direccion_restaurante", option.getLocation());

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return bestOptionList.size();
    }

    public static class BestOptionViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productPrice, productCategory, productLocation;
        LinearLayout linearLayout;

        public BestOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productCategory = itemView.findViewById(R.id.product_category);
            productLocation = itemView.findViewById(R.id.product_location);
            linearLayout = itemView.findViewById(R.id.item_opcion_restaurante);
        }
    }
}
