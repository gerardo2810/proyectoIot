package com.example.proyecto_iot.cliente.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class ProductoCarritoAdapter extends RecyclerView.Adapter<ProductoCarritoAdapter.ProductoViewHolder> {

    private List<Producto> productos;
    private OnProductUpdateListener listener;

    public ProductoCarritoAdapter(List<Producto> productos, OnProductUpdateListener listener) {
        this.productos = productos;
        this.listener = listener;
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

        holder.textProductName.setText(producto.getNombre());
        holder.textProductDescription.setText(producto.getDescripcion());
        holder.textProductPrice.setText("S/ " + producto.getPrecio());
        holder.textQuantity.setText(String.valueOf(producto.getCantidad()));
        holder.btnAdd.setText("Pagar S/ " + producto.getTotal());

        // Asignar la imagen correspondiente
        holder.productImage.setImageResource(producto.getImageResourceId());

        // Aumentar la cantidad
        holder.increaseQuantity.setOnClickListener(v -> {
            producto.setCantidad(producto.getCantidad() + 1);
            notifyItemChanged(position);
            listener.onProductUpdated();
        });

        // Disminuir la cantidad
        holder.decreaseQuantity.setOnClickListener(v -> {
            if (producto.getCantidad() > 1) {
                producto.setCantidad(producto.getCantidad() - 1);
                notifyItemChanged(position);
                listener.onProductUpdated();
            }
        });

        // Eliminar producto
        holder.deleteProduct.setOnClickListener(v -> {
            productos.remove(position);
            notifyItemRemoved(position);
            listener.onProductUpdated();
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView textProductName, textProductDescription, textProductPrice, textQuantity;
        ImageView increaseQuantity, decreaseQuantity, deleteProduct, productImage; // Agregar ImageView
        Button btnAdd;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            textProductName = itemView.findViewById(R.id.product_name);
            textProductDescription = itemView.findViewById(R.id.product_description);
            textProductPrice = itemView.findViewById(R.id.product_price);
            textQuantity = itemView.findViewById(R.id.quantity);
            increaseQuantity = itemView.findViewById(R.id.increase_quantity);
            decreaseQuantity = itemView.findViewById(R.id.decrease_quantity);
            deleteProduct = itemView.findViewById(R.id.delete_product);
            btnAdd = itemView.findViewById(R.id.add_button);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }

    // Listener para notificar cuando se actualiza el producto
    public interface OnProductUpdateListener {
        void onProductUpdated();
    }
}
