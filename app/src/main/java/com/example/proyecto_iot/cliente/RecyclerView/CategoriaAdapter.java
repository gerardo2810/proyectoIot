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

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.AgregarCategoriaActivity;
import com.example.proyecto_iot.admin_restaurante.EditarProductoActivity;
import com.example.proyecto_iot.cliente.InicioClienteActivity;
import com.example.proyecto_iot.cliente.ListaRestaurantesCategoriasClienteActivity;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>{

    private List<com.example.proyecto_iot.cliente.RecyclerView.Categoria> categoryList;
    private Context context;

    public CategoriaAdapter(List<com.example.proyecto_iot.cliente.RecyclerView.Categoria> categoryList, Context context) {
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
        holder.tvCategoryName.setText(category.getName());
        holder.imgCategory.setImageResource(category.getImageResId());

        // Evento al hacer clic en la categoría
        holder.itemView.setOnClickListener(v -> {
            // Verificar el nombre de la categoría antes de pasarla
            Log.d("CategoriaAdapter", "Categoría seleccionada: " + category.getName());

            // Pasar la categoría seleccionada a la nueva actividad
            Intent intent = new Intent(context, ListaRestaurantesCategoriasClienteActivity.class);
            intent.putExtra("selectedCategory", category.getName()); // Pasar la categoría seleccionada
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return categoryList.size();
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
