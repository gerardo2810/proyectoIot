package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.AgregarCategoriaActivity;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private List<Categoria> categoryList;
    private Context context;
    private OnCategoryClickListener listener;

    // Interfaz para manejar clics en las categorías
    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoriaAdapter(List<Categoria> categorias, Context context, OnCategoryClickListener listener) {
        this.categoryList = categorias;
        this.context = context;
        this.listener = listener;
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

        holder.nombreCategoria.setText(category.getNombre());

        Glide.with(context)
                .load(category.getIconFoto())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.sinfoto)
                .into(holder.imageCategoria);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        ImageView imageCategoria;
        TextView nombreCategoria;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCategoria = itemView.findViewById(R.id.img_category);
            nombreCategoria = itemView.findViewById(R.id.tv_category_name);
        }
    }
}