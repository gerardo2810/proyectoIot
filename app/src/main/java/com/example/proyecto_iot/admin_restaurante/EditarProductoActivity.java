package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class EditarProductoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private EditText etProductName, etProductDescription, etProductPrice, etProductStock;
    private Button btnUploadImage, btnSaveProduct;
    private ImageView imageView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_editar_producto);

        // Inicializar vistas
        etProductName = findViewById(R.id.et_product_name);
        etProductDescription = findViewById(R.id.et_product_description);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductStock = findViewById(R.id.et_product_stock);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnSaveProduct = findViewById(R.id.btn_save_product);
        imageView5 = findViewById(R.id.imageView5);

        // Configurar botón para subir imagen
        btnUploadImage.setOnClickListener(v -> openImageSelector());

        // Guardar producto
        btnSaveProduct.setOnClickListener(v -> {
            // Guardar el producto en la base de datos o lista (según sea necesario)
            // Podrías agregar validaciones aquí para asegurarte de que los datos sean correctos.
            Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show();
            finish();
        });


        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Termina esta actividad para volver a la anterior
                finish();
            }
        });
    }

    // Método para abrir el selector de imágenes
    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK); // Abrir selector de imágenes
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    // Manejar el resultado del selector de imágenes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView5.setImageURI(imageUri); // Mostrar la imagen seleccionada en el ImageView
        }
    }
}
