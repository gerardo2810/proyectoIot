package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.EditarProductoActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {
    private List<Producto> productList;
    private Context context;

    public ProductoAdapter(List<Producto> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto product = productList.get(position);

        // Configurar datos del producto
        holder.tvProductName.setText(product.getNombre());
        holder.tvProductStock.setText("Stock: " + product.getStock() + " unidades");

        // Cargar imagen del producto
        Glide.with(context)
                .load(product.getImagen())
                .placeholder(R.drawable.sinfoto)
                .into(holder.imgProduct);

        // Configurar el estado inicial del Switch
        holder.switchProduct.setOnCheckedChangeListener(null); // Evitar el llamado inicial
        holder.switchProduct.setChecked(product.isActive());

        // Listener del Switch para cambiar estado
        holder.switchProduct.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mostrarDialogoConfirmacion(holder, product, position, isChecked);
        });

        // Acción del botón editar
        holder.btnEditProduct.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarProductoActivity.class);
            intent.putExtra("productId", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Método para actualizar la lista de productos
    public void updateData(List<Producto> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    // Mostrar diálogo de confirmación
    private void mostrarDialogoConfirmacion(ProductoViewHolder holder, Producto product, int position, boolean nuevoEstado) {
        String mensaje = nuevoEstado
                ? "¿Está seguro de que desea activar este producto?"
                : "¿Está seguro de que desea desactivar este producto?";

        new AlertDialog.Builder(context)
                .setTitle("Confirmación")
                .setMessage(mensaje)
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Actualizar estado del producto
                    product.setActive(nuevoEstado);
                    actualizarEstadoProducto(product);

                    // Actualizar la interfaz
                    holder.switchProduct.setChecked(nuevoEstado);
                    notifyItemChanged(position);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Revertir el estado del Switch
                    holder.switchProduct.setChecked(!nuevoEstado);
                })
                .show();
    }

    // Actualizar estado en la base de datos
    private void actualizarEstadoProducto(Producto product) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("platos")
                .document(product.getId())
                .update("isActive", product.isActive())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Estado del producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                });
    }


    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductStock;
        ImageView imgProduct;
        Switch switchProduct;
        Button btnEditProduct;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductStock = itemView.findViewById(R.id.tv_product_stock);
            imgProduct = itemView.findViewById(R.id.img_product);
            switchProduct = itemView.findViewById(R.id.switch_product);
            btnEditProduct = itemView.findViewById(R.id.btn_edit_product);
        }
    }
}
