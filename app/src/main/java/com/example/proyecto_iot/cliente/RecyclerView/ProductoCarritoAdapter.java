package com.example.proyecto_iot.cliente.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class ProductoCarritoAdapter extends RecyclerView.Adapter<ProductoCarritoAdapter.ProductoViewHolder> {

    private List<Producto> productos;

    public ProductoCarritoAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_carrito, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.nombreProducto.setText(producto.getNombre());
        holder.descripcionProducto.setText(producto.getDescripcion());
        holder.precioProducto.setText("S/. " + String.valueOf(producto.getPrecio()));
        holder.textQuantity.setText(String.valueOf(producto.getCantidad()));

        // Botón de aumentar cantidad
        holder.increaseQuantity.setOnClickListener(v -> {
            producto.incrementarCantidad();
            holder.textQuantity.setText(String.valueOf(producto.getCantidad()));
            notifyItemChanged(position);
        });

        // Botón de disminuir cantidad
        holder.decreaseQuantity.setOnClickListener(v -> {
            if (producto.getCantidad() > 1) {
                producto.disminuirCantidad();
                holder.textQuantity.setText(String.valueOf(producto.getCantidad()));
                notifyItemChanged(position);
            }
        });

        // Botón de eliminar producto
        holder.deleteProduct.setOnClickListener(v -> {
            productos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, productos.size());
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreProducto, descripcionProducto, precioProducto, textQuantity;
        ImageView increaseQuantity, decreaseQuantity, deleteProduct;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.product_name);
            descripcionProducto = itemView.findViewById(R.id.product_description);
            precioProducto = itemView.findViewById(R.id.product_price);
            textQuantity = itemView.findViewById(R.id.quantity);
            increaseQuantity = itemView.findViewById(R.id.increase_quantity);
            decreaseQuantity = itemView.findViewById(R.id.decrease_quantity);
            deleteProduct = itemView.findViewById(R.id.delete_product);
        }
    }
}
