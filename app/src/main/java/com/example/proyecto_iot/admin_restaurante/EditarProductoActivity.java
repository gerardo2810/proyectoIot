package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Producto;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditarProductoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etProductName, etProductDescription, etProductPrice, etProductStock, etTimePreparation;
    private Button btnUploadImage, btnSaveProduct;
    private ImageView imageView5;
    private Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String productId;
    private Producto producto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_editar_producto);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("product_images");

        // Obtener productId de la Intent
        productId = getIntent().getStringExtra("productId");
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "Error: No se recibió el ID del producto.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        etProductName = findViewById(R.id.et_product_name);
        etProductDescription = findViewById(R.id.et_product_description);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductStock = findViewById(R.id.et_product_stock);
        etTimePreparation = findViewById(R.id.et_time_preparation);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnSaveProduct = findViewById(R.id.btn_save_product);
        imageView5 = findViewById(R.id.imageView5);

        // Cargar datos del producto desde Firestore
        loadProductData();

        // Configurar botón para subir imagen
        btnUploadImage.setOnClickListener(v -> openImageSelector());

        // Configurar botón para guardar cambios
        btnSaveProduct.setOnClickListener(v -> saveProductChanges());

        // Configurar botón de retroceso
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadProductData() {
        db.collection("platos").document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        producto = documentSnapshot.toObject(Producto.class);
                        if (producto != null) {
                            etProductName.setText(producto.getNombre());
                            etProductDescription.setText(producto.getDescripcion());
                            etProductPrice.setText(String.valueOf(producto.getPrecio()));
                            etProductStock.setText(String.valueOf(producto.getStock()));
                            etTimePreparation.setText(String.valueOf(producto.getTiempoPreparacion()));

                            Glide.with(this)
                                    .load(producto.getImagen())
                                    .placeholder(R.drawable.placeholder)
                                    .into(imageView5);
                        }
                    } else {
                        Toast.makeText(this, "Producto no encontrado.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar el producto.", Toast.LENGTH_SHORT).show());
    }

    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView5.setImageURI(imageUri);
        }
    }

    private void saveProductChanges() {
        String name = etProductName.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String priceText = etProductPrice.getText().toString().trim();
        String stockText = etProductStock.getText().toString().trim();
        String preparationTimeText = etTimePreparation.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || stockText.isEmpty() || preparationTimeText.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceText);
        int stock = Integer.parseInt(stockText);
        int preparationTime = Integer.parseInt(preparationTimeText);

        if (imageUri != null) {
            // Subir imagen actualizada a Firebase Storage
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> updateProductInFirestore(name, description, price, stock, preparationTime, uri.toString()))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen.", Toast.LENGTH_SHORT).show()));
        } else {
            // Si no se cambia la imagen, actualiza solo los datos
            updateProductInFirestore(name, description, price, stock, preparationTime, producto.getImagen());
        }
    }

    private void updateProductInFirestore(String name, String description, double price, int stock, int preparationTime, String imageUrl) {
        Map<String, Object> updatedProduct = new HashMap<>();
        updatedProduct.put("Nombre", name);
        updatedProduct.put("Descripcion", description);
        updatedProduct.put("Precio", price);
        updatedProduct.put("Stock", stock);
        updatedProduct.put("TiempoPreparacion", preparationTime);
        updatedProduct.put("Imagen", imageUrl);

        db.collection("platos").document(productId).update(updatedProduct)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Producto actualizado exitosamente.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar el producto.", Toast.LENGTH_SHORT).show());
    }

    private void showDeleteConfirmationDialog() {
        // Crear diálogo personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null);

        Button btnConfirmDelete = customLayout.findViewById(R.id.btn_confirm_delete);
        Button btnCancelDelete = customLayout.findViewById(R.id.btn_cancel_delete);
        TextView tvDeleteMessage = customLayout.findViewById(R.id.tv_delete_message);

        // Personalizar mensaje
        tvDeleteMessage.setText("¿Estás seguro de desactivar este producto?");

        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        btnConfirmDelete.setOnClickListener(v -> {
            deactivateProductInFirestore(); // Actualiza el campo isActive
            dialog.dismiss();
        });

        btnCancelDelete.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void deactivateProductInFirestore() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("isActive", false); // Actualizar campo isActive a false

        db.collection("platos").document(productId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Producto desactivado correctamente.", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la actividad
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al desactivar el producto: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
