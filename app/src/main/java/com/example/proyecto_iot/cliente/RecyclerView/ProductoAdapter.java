package com.example.proyecto_iot.cliente.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.proyecto_iot.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productos;

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
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productDescription, productPrice, textQuantity;
        Button  buttonAddProduct;
        ImageView buttonIncrease, buttonDecrease;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            textQuantity = itemView.findViewById(R.id.quantity);
            buttonIncrease =itemView.findViewById(R.id.increase_quantity);
            buttonDecrease = itemView.findViewById(R.id.decrease_quantity);
            buttonAddProduct = itemView.findViewById(R.id.add_button);
        }
    }
}

