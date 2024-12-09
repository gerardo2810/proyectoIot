package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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

public class EditPersonalInfoActivity extends AppCompatActivity {
    private EditText etNombre, etApellido, etEdad, etDireccion, etCorreo, etTelefono, etDni;
    private Button btnEditarDatos, btnGuardarDatos;
    private Button btnUploadImage;
    private ImageView ivFoto;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String idRestaurante, idAdministrador;
    private TextView tvRestaurantName, tvCuisineType;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_editar_info_personal);

        // Inicializar Firestore y Storage
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Recuperar idRestaurante del intent
        idRestaurante = getIntent().getStringExtra("idRestaurante");

        // Inicializar vistas
        tvRestaurantName = findViewById(R.id.restaurant_name);
        tvCuisineType = findViewById(R.id.cuisine_type);
        etNombre = findViewById(R.id.et_name);
        etApellido = findViewById(R.id.et_apellido);
        etEdad = findViewById(R.id.et_fecha);
        etDireccion = findViewById(R.id.et_direccion);
        etCorreo = findViewById(R.id.et_correo);
        etTelefono = findViewById(R.id.et_telefono);
        etDni = findViewById(R.id.et_dni);
        ivFoto = findViewById(R.id.imageView5);
        btnEditarDatos = findViewById(R.id.editar_datos);
        btnGuardarDatos = findViewById(R.id.btn_save_personal_info);
        btnUploadImage = findViewById(R.id.btn_upload_image);


        // Bloquear campos inicialmente
        toggleFields(false);

        // Cargar datos del administrador
        if (idRestaurante != null) {
            fetchRestaurantData(idRestaurante);
            fetchAdminData(idRestaurante);
        } else {
            Toast.makeText(this, "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Habilitar campos para edición
        btnEditarDatos.setOnClickListener(v -> toggleFields(true));

        // Guardar datos editados
        btnGuardarDatos.setOnClickListener(v -> saveAdminData());

        // Cambiar foto de perfil
        btnUploadImage.setOnClickListener(v -> selectImage());

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchRestaurantData(String idRestaurante) {
        db.collection("restaurantes").document(idRestaurante).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Llenar datos del restaurante
                        String restaurantName = documentSnapshot.getString("nombre");
                        String cuisineType = documentSnapshot.getString("eslogan");
                        idAdministrador = documentSnapshot.getString("idAdministrador");

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

    private void fetchAdminData(String idRestaurante) {
        db.collection("restaurantes").document(idRestaurante).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        idAdministrador = documentSnapshot.getString("idAdministrador");
                        if (idAdministrador != null) {
                            db.collection("administradores").document(idAdministrador).get()
                                    .addOnSuccessListener(adminSnapshot -> {
                                        if (adminSnapshot.exists()) {
                                            // Llenar campos con datos del administrador
                                            etNombre.setText(adminSnapshot.getString("nombre"));
                                            etApellido.setText(adminSnapshot.getString("apellido"));
                                            etEdad.setText(adminSnapshot.getString("edad"));
                                            etDireccion.setText(adminSnapshot.getString("direccion"));
                                            etCorreo.setText(adminSnapshot.getString("correo"));
                                            etTelefono.setText(adminSnapshot.getString("telefono"));
                                            etDni.setText(adminSnapshot.getString("dni"));

                                            // Cargar imagen de perfil
                                            String fotoUrl = adminSnapshot.getString("foto");
                                            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                                                Glide.with(this)
                                                        .load(fotoUrl)
                                                        .placeholder(R.drawable.placeholder)
                                                        .into(ivFoto);
                                            }
                                        } else {
                                            Toast.makeText(this, "Datos del administrador no encontrados.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "No se encontró el administrador del restaurante.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Datos del restaurante no encontrados.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveAdminData() {
        if (idAdministrador != null) {
            Map<String, Object> adminUpdates = new HashMap<>();
            adminUpdates.put("nombre", etNombre.getText().toString());
            adminUpdates.put("apellido", etApellido.getText().toString());
            adminUpdates.put("edad", etEdad.getText().toString());
            adminUpdates.put("direccion", etDireccion.getText().toString());
            adminUpdates.put("correo", etCorreo.getText().toString());
            adminUpdates.put("telefono", etTelefono.getText().toString());
            adminUpdates.put("dni", etDni.getText().toString());

            db.collection("administradores").document(idAdministrador)
                    .update(adminUpdates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Datos guardados exitosamente.", Toast.LENGTH_SHORT).show();
                        toggleFields(false);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No se encontró el ID del administrador.", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFields(boolean enable) {
        etNombre.setEnabled(enable);
        etApellido.setEnabled(enable);
        etEdad.setEnabled(enable);
        etDireccion.setEnabled(enable);
        etCorreo.setEnabled(enable);
        etTelefono.setEnabled(enable);
        etDni.setEnabled(enable);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Subir imagen a Firebase Storage
            StorageReference fileRef = storageReference.child("fotosAdministradores/" + idAdministrador + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Actualizar URL en Firestore
                                db.collection("administradores").document(idAdministrador)
                                        .update("foto", uri.toString())
                                        .addOnSuccessListener(aVoid -> {
                                            Glide.with(this).load(uri).into(ivFoto);
                                            Toast.makeText(this, "Foto actualizada exitosamente.", Toast.LENGTH_SHORT).show();
                                        });
                            }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
