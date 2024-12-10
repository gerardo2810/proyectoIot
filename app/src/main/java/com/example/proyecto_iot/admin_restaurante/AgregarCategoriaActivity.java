package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AgregarCategoriaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etCategoryName;
    private Button btnSaveCategory, btnUploadImage;
    private ImageView categoryImageView;
    private FirebaseFirestore db;
    private String idRestaurante, imageUrl;
    private TextView tvRestaurantName, tvCuisineType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_crear_category);

        db = FirebaseFirestore.getInstance();

        tvRestaurantName = findViewById(R.id.restaurant_name);
        tvCuisineType = findViewById(R.id.cuisine_type);

        etCategoryName = findViewById(R.id.et_category_name);
        btnSaveCategory = findViewById(R.id.btn_save_category);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        categoryImageView = findViewById(R.id.imageView5);

        idRestaurante = getIntent().getStringExtra("idRestaurante");
        if (idRestaurante == null || idRestaurante.isEmpty()) {
            Toast.makeText(this, "ID del restaurante no recibido.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (idRestaurante != null) {
            fetchRestaurantData1(idRestaurante);
        } else {
            Toast.makeText(this, "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnUploadImage.setOnClickListener(v -> uploadImage());

        btnSaveCategory.setOnClickListener(v -> saveCategory());

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchRestaurantData1(String idRestaurante) {
        db.collection("restaurantes").document(idRestaurante).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String restaurantName = documentSnapshot.getString("nombre");
                        String cuisineType = documentSnapshot.getString("eslogan");
                        tvRestaurantName.setText(restaurantName != null ? restaurantName : "Nombre no disponible");
                        tvCuisineType.setText(cuisineType != null ? cuisineType : "Eslogan no disponible");
                    } else {
                        Toast.makeText(this, "Datos del restaurante no encontrados.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener datos del restaurante: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Muestra la imagen seleccionada en el ImageView
            Glide.with(this).load(imageUri).into(categoryImageView);

            // Sube la imagen a Firebase Storage
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri == null) return;

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("categorias/" + System.currentTimeMillis() + ".jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        Toast.makeText(this, "Imagen subida correctamente.", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveCategory() {
        String categoryName = etCategoryName.getText().toString().trim();

        if (categoryName.isEmpty() || imageUrl == null || imageUrl.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("Nombre", categoryName);
        categoryData.put("iconFoto", imageUrl);
        categoryData.put("idRestaurante", idRestaurante);

        db.collection("categorias")
                .add(categoryData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Categoría creada exitosamente.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al crear categoría: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

