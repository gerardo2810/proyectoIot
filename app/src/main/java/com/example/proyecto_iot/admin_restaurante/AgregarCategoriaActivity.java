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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AgregarCategoriaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etCategoryName;
    private Button btnSaveCategory, btnUploadImage;
    private ImageView categoryImageView;
    private FirebaseFirestore db;
    private String idRestaurante, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_crear_category);

        db = FirebaseFirestore.getInstance();

        etCategoryName = findViewById(R.id.et_category_name);
        btnSaveCategory = findViewById(R.id.btn_save_category);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        categoryImageView = findViewById(R.id.imageView5);

        idRestaurante = getIntent().getStringExtra("idRestaurante");
        if (idRestaurante == null || idRestaurante.isEmpty()) {
            Toast.makeText(this, "ID del restaurante no recibido.", Toast.LENGTH_SHORT).show();
            finish();
        }


        btnUploadImage.setOnClickListener(v -> uploadImage());

        btnSaveCategory.setOnClickListener(v -> saveCategory());

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
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
                    guardarLog("El administrador ha creado una nueva categoria de comidas llamada " + categoryName, "Administrador");
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al crear categoría: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

