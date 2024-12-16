package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetallePlatoActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_detalle_plato);

        db = FirebaseFirestore.getInstance();

        ImageButton backButton = findViewById(R.id.back_button);
        ImageView imageView5 = findViewById(R.id.imageView5);
        TextView etProductNombre = findViewById(R.id.et_product_nombre);
        TextView etProductCategoria = findViewById(R.id.et_product_categoría);
        TextView etProductDescription = findViewById(R.id.et_product_description);
        TextView etProductPrice = findViewById(R.id.et_product_price);
        TextView etCantPedidos = findViewById(R.id.et_product_stock);

        String cantpedidos = getIntent().getStringExtra("cantVendida");

        // Asignar el valor de gastado proveniente del Intent
        if (cantpedidos != null && !cantpedidos.isEmpty()) {
            etCantPedidos.setText(cantpedidos);
        } else {
            etCantPedidos.setText("-");
        }

        // Obtener idProducto del intent
        Intent intent = getIntent();
        String idProducto = intent.getStringExtra("idProducto");

        // Si se requiere, puedes mostrar un indicador de carga mientras se obtienen los datos.
        // Realizar la consulta a la colección "platos" con el idProducto
        db.collection("platos").document(idProducto).get()
                .addOnSuccessListener(platoDoc -> {
                    if (platoDoc.exists()) {
                        String nombre = platoDoc.getString("Nombre");
                        String descripcion = platoDoc.getString("Descripcion");
                        Double precio = platoDoc.getDouble("Precio");
                        Long stockLong = platoDoc.getLong("Stock");
                        String imagenUrl = platoDoc.getString("Imagen");
                        String idCategoria = platoDoc.getString("idCategoria");

                        // Asignar los campos obtenidos
                        if (nombre != null) etProductNombre.setText(nombre);
                        if (descripcion != null) etProductDescription.setText(descripcion);
                        if (precio != null) etProductPrice.setText("S/" + precio);

                        // Cargar la imagen con Glide si hay URL
                        if (imagenUrl != null && !imagenUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imagenUrl)
                                    .placeholder(R.drawable.placeholder)
                                    .into(imageView5);
                        }

                        // Ahora obtener el nombre de la categoría
                        if (idCategoria != null && !idCategoria.isEmpty()) {
                            db.collection("categorias").document(idCategoria).get()
                                    .addOnSuccessListener(catDoc -> {
                                        if (catDoc.exists()) {
                                            String nombreCat = catDoc.getString("nombre");
                                            if (nombreCat != null) {
                                                etProductCategoria.setText(nombreCat);
                                            } else {
                                                etProductCategoria.setText("Sin categoría");
                                            }
                                        } else {
                                            etProductCategoria.setText("Categoría no encontrada");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        etProductCategoria.setText("Error al cargar categoría");
                                    });
                        } else {
                            etProductCategoria.setText("Sin categoría");
                        }

                    } else {
                        Toast.makeText(this, "No se encontró el producto", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos del producto", Toast.LENGTH_SHORT).show();
                });

        backButton.setOnClickListener(v -> finish());
    }
}
