package com.example.proyecto_iot.cliente.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.EditarProductoActivity;
import com.example.proyecto_iot.cliente.ListaRestaurantesCategoriasClienteActivity;
import com.example.proyecto_iot.cliente.PerfilRestauranteActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productos;
    private Context context;

    public ProductoAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_perfil_restaurante, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.productTitle.setText(producto.getNombre());
        holder.productDescription.setText(producto.getDescripcion());
        holder.productPrice.setText("S/ " + producto.getPrecio());
        holder.productImage.setImageResource(producto.getImageResourceId());

        // Lógica para los botones de aumentar y disminuir cantidad
        holder.buttonIncrease.setOnClickListener(view -> {
            producto.incrementarCantidad();
            holder.textQuantity.setText(String.valueOf(producto.getCantidad()));
        });

        holder.buttonDecrease.setOnClickListener(view -> {
            if (producto.getCantidad() > 0) {
                producto.disminuirCantidad();
                holder.textQuantity.setText(String.valueOf(producto.getCantidad()));
            }
        });

        // Acción del botón restaurante
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, PerfilRestauranteActivity.class);
            context.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productDescription, productPrice, textQuantity;
        Button  buttonAddProduct;
        ImageView buttonIncrease, buttonDecrease, productImage;
        LinearLayout linearLayout;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            textQuantity = itemView.findViewById(R.id.quantity);
            buttonIncrease =itemView.findViewById(R.id.increase_quantity);
            buttonDecrease = itemView.findViewById(R.id.decrease_quantity);
            buttonAddProduct = itemView.findViewById(R.id.add_button);
            productImage = itemView.findViewById(R.id.product_image);
            linearLayout = itemView.findViewById(R.id.item_opcion_restaurante);// Referencia a la ImageView
        }
    }
}

