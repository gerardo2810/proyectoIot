package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;

public class AgregarProductoActivity extends AppCompatActivity {
    private EditText etProductName, etProductDescription, etProductPrice, etProductStock;
    private Button btnUploadImage, btnSaveProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_agregar_producto);

        etProductName = findViewById(R.id.et_product_name);
        etProductDescription = findViewById(R.id.et_product_description);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductStock = findViewById(R.id.et_product_stock);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnSaveProduct = findViewById(R.id.btn_save_product);

        btnUploadImage.setOnClickListener(v -> {
            // Lógica para subir imagen (opcional)
        });

        btnSaveProduct.setOnClickListener(v -> {
            // Guardar el producto en la base de datos o lista (según sea necesario)
            // Podrías agregar validaciones aquí para asegurarte de que los datos sean correctos.
            Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

}
