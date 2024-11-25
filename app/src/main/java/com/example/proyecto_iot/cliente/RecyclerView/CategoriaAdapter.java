package com.example.proyecto_iot.cliente.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.proyecto_iot.cliente.ListaRestaurantesCategoriasClienteActivity;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private List<Categoria> categoryList;
    private Context context;

    public CategoriaAdapter(List<Categoria> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getNombre());

        // Usar Glide para cargar la imagen desde la URL
        Glide.with(context)
                .load(category.getIconFoto())
                .placeholder(R.drawable.placeholder) // Ícono por defecto
                .into(holder.imgCategory);

        // Evento al hacer clic en la categoría
        holder.itemView.setOnClickListener(v -> {
            Log.d("CategoriaAdapter", "Categoría seleccionada: " + category.getNombre());

            // Pasar la categoría seleccionada a la nueva actividad
            Intent intent = new Intent(context, ListaRestaurantesCategoriasClienteActivity.class);
            intent.putExtra("selectedCategory", category.getNombre());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateData(List<Categoria> newCategories) {
        this.categoryList = newCategories;
        notifyDataSetChanged();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView tvCategoryName;
        LinearLayout linearLayout;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.img_category);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            linearLayout = itemView.findViewById(R.id.category_click);
        }
    }
}
