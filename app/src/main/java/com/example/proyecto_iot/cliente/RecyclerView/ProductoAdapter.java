package com.example.proyecto_iot.cliente.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productos;
    private List<Producto> productosListFull;  // Lista completa para restaurar productos
    private Context context;
    private OnProductoAñadidoListener listener; // Listener para notificar a la actividad

    // Constructor actualizado para recibir el listener
    public ProductoAdapter(Context context, List<Producto> productos, OnProductoAñadidoListener listener) {
        this.context = context;
        this.productos = productos;
        this.productosListFull = new ArrayList<>(productos); // Copia de la lista original
        this.listener = listener; // Inicializamos el listener
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto_perfil_restaurante, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.productTitle.setText(producto.getNombre());
        holder.productDescription.setText(producto.getDescripcion());
        holder.productPrice.setText("S/ " + producto.getPrecio());
        holder.textQuantity.setText(String.valueOf(producto.getCantidad()));

        // Cargar la imagen usando Glide
        Glide.with(context)
                .load(producto.getImageUrl()) // URL de la imagen desde Firebase
                .placeholder(R.drawable.placeholder) // Imagen por defecto
                .into(holder.productImage);

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

        // Lógica para añadir el producto al carrito
        holder.buttonAddProduct.setOnClickListener(view -> {
            int cantidadSeleccionada = producto.getCantidad(); // Obtener la cantidad seleccionada
            listener.onProductoAñadido(producto, cantidadSeleccionada); // Notificar a la actividad
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    // Método para filtrar la lista de productos
    public void filterList(String query) {
        if (query.isEmpty()) {
            productos = new ArrayList<>(productosListFull); // Restaurar la lista completa
        } else {
            List<Producto> filteredList = new ArrayList<>();
            for (Producto producto : productosListFull) {
                if (producto.getNombre().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(producto); // Agregar productos que coincidan con la búsqueda
                }
            }
            productos = filteredList;
        }
        notifyDataSetChanged(); // Actualizar la vista del RecyclerView
    }

    // ViewHolder que mantiene las referencias a los elementos de cada item de producto
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productDescription, productPrice, textQuantity;
        Button buttonAddProduct;
        ImageView buttonIncrease, buttonDecrease, productImage;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            textQuantity = itemView.findViewById(R.id.quantity);
            buttonIncrease = itemView.findViewById(R.id.increase_quantity);
            buttonDecrease = itemView.findViewById(R.id.decrease_quantity);
            buttonAddProduct = itemView.findViewById(R.id.add_button);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }

    // Definir la interfaz del listener para manejar la acción de añadir productos al carrito
    public interface OnProductoAñadidoListener {
        void onProductoAñadido(Producto producto, int cantidad);
    }
}
