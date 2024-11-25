package com.example.proyecto_iot.cliente.RecyclerView;

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

public class ProductoDetallesAdapter extends RecyclerView.Adapter<ProductoDetallesAdapter.ProductoViewHolder> {

    private List<Producto> productos;
    private Context context;

    public ProductoDetallesAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_detalles, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.productName.setText(producto.getNombre());
        holder.productDescription.setText(producto.getDescripcion());
        holder.productPriceUnit.setText("S/. " + producto.getPrecio());
        holder.productPriceTotal.setText("S/. " + producto.getTotal());

        // Usar Glide para cargar la imagen desde una URL
        Glide.with(context)
                .load(producto.getImageUrl()) // Cambiado para usar URLs en lugar de recursos locales
                .placeholder(R.drawable.placeholder) // Placeholder mientras se carga la imagen
                .error(R.drawable.placeholder) // Imagen de error si no se puede cargar
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productPriceUnit, productPriceTotal;
        ImageView productImage;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPriceUnit = itemView.findViewById(R.id.product_price);
            productPriceTotal = itemView.findViewById(R.id.product_price_total);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
