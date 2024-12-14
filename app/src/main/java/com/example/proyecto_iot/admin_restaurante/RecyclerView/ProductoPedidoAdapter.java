package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;

import java.util.List;

public class ProductoPedidoAdapter extends RecyclerView.Adapter<ProductoPedidoAdapter.ProductoViewHolder> {

    private List<ProductoPedido> productos; // Lista de productos del pedido
    private Context context;

    // Constructor
    public ProductoPedidoAdapter(List<ProductoPedido> productos, Context context) {
        this.productos = productos;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurante_item_producto_aceptar, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        ProductoPedido producto = productos.get(position);

        // Configurar los datos en las vistas
        holder.tvNombreProducto.setText(producto.getNombre());
        holder.tvCantidadProducto.setText("x" + producto.getCantidad());
        holder.tvPrecioProducto.setText(String.format("S/ %.2f", producto.getPrecio()));

        // Cargar la imagen usando Glide (o similar)
        if (producto.getImageUrl() != null && !producto.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(producto.getImageUrl())
                    .into(holder.ivImagenProducto);
        } else {
            holder.ivImagenProducto.setImageResource(R.drawable.placeholder); // Imagen por defecto
        }
    }

    @Override
    public int getItemCount() {
        return productos != null ? productos.size() : 0;
    }

    // ViewHolder para manejar las vistas individuales de cada producto
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreProducto, tvCantidadProducto, tvPrecioProducto;
        ImageView ivImagenProducto;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProducto = itemView.findViewById(R.id.tv_nombre_producto);
            tvCantidadProducto = itemView.findViewById(R.id.tv_cantidad_producto);
            tvPrecioProducto = itemView.findViewById(R.id.tv_precio_producto);
            ivImagenProducto = itemView.findViewById(R.id.iv_imagen_producto);
        }
    }
}