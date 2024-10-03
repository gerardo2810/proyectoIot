package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.EditarProductoActivity;

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
        holder.tvProductName.setText(product.getName());
        holder.img_product.setImageResource(product.getImageResId());
        holder.tvProductStock.setText("stock: " + product.getStock() + " unidades");

        // Configurar el switch
        holder.switchProduct.setChecked(product.isActive());
        holder.switchProduct.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setActive(isChecked);
            // Aquí puedes guardar el estado del producto si es necesario
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

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductStock;
        ImageView img_product;
        Switch switchProduct;
        Button btnEditProduct;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductStock = itemView.findViewById(R.id.tv_product_stock);
            img_product = itemView.findViewById(R.id.img_product);
            switchProduct = itemView.findViewById(R.id.switch_product);
            btnEditProduct = itemView.findViewById(R.id.btn_edit_product);
        }
    }
}
