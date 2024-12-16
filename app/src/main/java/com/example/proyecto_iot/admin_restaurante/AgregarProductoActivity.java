package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AgregarProductoActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private EditText etProductName, etProductDescription, etProductPrice, etProductStock, etTimePreparation;
    private Button btnUploadImage, btnSaveProduct;
    private String idCategoria;
    private ImageView imageView5;
    private Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_agregar_producto);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("product_images");

        idCategoria = getIntent().getStringExtra("idCategoria");
        if (idCategoria == null || idCategoria.isEmpty()) {
            Toast.makeText(this, "Error: No se recibió el ID de categoría.", Toast.LENGTH_SHORT).show();
            finish(); // Finalizar la actividad si no hay un idCategoria válido
            return;
        }

        // Inicialización de vistas
        etProductName = findViewById(R.id.et_product_name);
        etProductDescription = findViewById(R.id.et_product_description);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductStock = findViewById(R.id.et_product_stock);
        etTimePreparation = findViewById(R.id.et_time_preparation);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnSaveProduct = findViewById(R.id.btn_save_product);
        imageView5 = findViewById(R.id.imageView5);

        btnUploadImage.setOnClickListener(v -> openImageSelector());
        btnSaveProduct.setOnClickListener(v -> saveProduct());

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView5.setImageURI(imageUri); // Mostrar la imagen seleccionada
        }
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String priceText = etProductPrice.getText().toString().trim();
        String stockText = etProductStock.getText().toString().trim();
        String preparationTimeText = etTimePreparation.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || stockText.isEmpty() || preparationTimeText.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Por favor completa todos los campos e incluye una imagen.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceText);
        int stock = Integer.parseInt(stockText);
        int preparationTime = Integer.parseInt(preparationTimeText);

        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Map<String, Object> product = new HashMap<>();
                            product.put("Nombre", name);
                            product.put("Descripcion", description);
                            product.put("Precio", price);
                            product.put("Stock", stock);
                            product.put("TiempoPreparacion", preparationTime);
                            product.put("Imagen", uri.toString());
                            product.put("idCategoria", idCategoria);
                            product.put("isActive", true);
                            product.put("cantidadDeVentas", 0);

                            db.collection("platos").add(product)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(AgregarProductoActivity.this, "Producto guardado exitosamente", Toast.LENGTH_SHORT).show();
                                        guardarLog("El administrador ha agregado un nuevo producto llamado " + name, "Administrador");
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(AgregarProductoActivity.this, "Error al guardar el producto", Toast.LENGTH_SHORT).show());
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
    }

    public void guardarLog(String mensaje, String rol) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener el UID del usuario logueado
        String usuarioUID = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "Usuario desconocido";

        // Obtener fecha y hora actuales
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String horaActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Crear un mapa para guardar el log
        HashMap<String, Object> logData = new HashMap<>();
        logData.put("mensaje", mensaje);
        logData.put("usuarioUID", usuarioUID);
        logData.put("rol", rol);
        logData.put("fecha", fechaActual);
        logData.put("hora", horaActual);

        // Guardar el log en Firestore
        db.collection("logs")
                .add(logData)
                .addOnSuccessListener(documentReference -> {
                    // Éxito al guardar el log
                    System.out.println("Log guardado con éxito. ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Error al guardar el log
                    System.err.println("Error al guardar el log: " + e.getMessage());
                });
    }

}
