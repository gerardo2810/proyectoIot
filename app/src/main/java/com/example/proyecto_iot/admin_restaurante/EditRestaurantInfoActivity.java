package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class EditRestaurantInfoActivity extends AppCompatActivity {
    private EditText etRestaurantName, etRestaurantEslogan, etRestaurantUbicacion, etRestaurantDescripcion, etRestaurantDelivery;
    private Spinner spinnerTipoComida;
    private Button btnEditData, btnSaveData, btnUploadLogo, btnUploadPortada;
    private ImageView ivLogo, ivPortada;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String idRestaurante;
    private TextView tvRestaurantName, tvCuisineType;
    private static final int PICK_LOGO_REQUEST = 1;
    private static final int PICK_PORTADA_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_editar_info_restaurante);

        // Inicializar Firestore y Storage
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Recuperar idRestaurante del intent
        idRestaurante = getIntent().getStringExtra("idRestaurante");

        // Inicializar vistas
        tvRestaurantName = findViewById(R.id.restaurant_name);
        tvCuisineType = findViewById(R.id.cuisine_type);
        etRestaurantName = findViewById(R.id.et_restaurant_name);
        etRestaurantEslogan = findViewById(R.id.et_restaurant_eslogan);
        etRestaurantUbicacion = findViewById(R.id.et_restaurant_ubicacion);
        etRestaurantDescripcion = findViewById(R.id.et_restaurant_descripcion);
        etRestaurantDelivery = findViewById(R.id.et_restaurant_delivery);
        spinnerTipoComida = findViewById(R.id.spinnerTipoComida);
        ivLogo = findViewById(R.id.imageView5);
        ivPortada = findViewById(R.id.imgPreview);
        btnEditData = findViewById(R.id.editar_datos);
        btnSaveData = findViewById(R.id.btn_save_restaurant_info);
        btnUploadLogo = findViewById(R.id.btn_upload_image);
        btnUploadPortada = findViewById(R.id.buttonUploadImage);

        // Bloquear campos inicialmente
        toggleFields(false);

        // Cargar datos del restaurante
        if (idRestaurante != null) {
            fetchRestaurantData1(idRestaurante);
            fetchRestaurantData(idRestaurante);
        } else {
            Toast.makeText(this, "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Habilitar campos para edición
        btnEditData.setOnClickListener(v -> toggleFields(true));

        // Guardar datos del restaurante
        btnSaveData.setOnClickListener(v -> saveRestaurantData());

        // Subir logo
        btnUploadLogo.setOnClickListener(v -> selectImage(PICK_LOGO_REQUEST));

        // Subir portada
        btnUploadPortada.setOnClickListener(v -> selectImage(PICK_PORTADA_REQUEST));

        // Botón de retroceso
        ImageView backButton = findViewById(R.id.back_button2);
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchRestaurantData1(String idRestaurante) {
        db.collection("restaurantes").document(idRestaurante).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Llenar datos del restaurante
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


    private void fetchRestaurantData(String idRestaurante) {
        db.collection("restaurantes").document(idRestaurante).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Llenar campos con datos del restaurante
                        etRestaurantName.setText(documentSnapshot.getString("nombre"));
                        etRestaurantEslogan.setText(documentSnapshot.getString("eslogan"));
                        etRestaurantUbicacion.setText(documentSnapshot.getString("ubicacion"));
                        etRestaurantDescripcion.setText(documentSnapshot.getString("descripcion"));
                        etRestaurantDelivery.setText(String.valueOf(documentSnapshot.getDouble("precioDelivery")));
                        String tipoDeComida = documentSnapshot.getString("tipoDeComida");
                        if (tipoDeComida != null) {
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                    R.array.tipo_de_comidas, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerTipoComida.setAdapter(adapter);
                            spinnerTipoComida.setSelection(adapter.getPosition(tipoDeComida));
                        }

                        // Cargar logo
                        String fotoLogo = documentSnapshot.getString("fotoLogo");
                        if (fotoLogo != null && !fotoLogo.isEmpty()) {
                            Glide.with(this)
                                    .load(fotoLogo)
                                    .placeholder(R.drawable.placeholder)
                                    .into(ivLogo);
                        }

                        // Cargar portada
                        String fotoPortada = documentSnapshot.getString("fotoPortada");
                        if (fotoPortada != null && !fotoPortada.isEmpty()) {
                            Glide.with(this)
                                    .load(fotoPortada)
                                    .placeholder(R.drawable.placeholder)
                                    .into(ivPortada);
                        }
                    } else {
                        Toast.makeText(this, "Datos del restaurante no encontrados.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al obtener datos del restaurante: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveRestaurantData() {
        if (idRestaurante != null) {
            Map<String, Object> restaurantUpdates = new HashMap<>();
            restaurantUpdates.put("nombre", etRestaurantName.getText().toString());
            restaurantUpdates.put("eslogan", etRestaurantEslogan.getText().toString());
            restaurantUpdates.put("ubicacion", etRestaurantUbicacion.getText().toString());
            restaurantUpdates.put("descripcion", etRestaurantDescripcion.getText().toString());
            restaurantUpdates.put("precioDelivery", Double.parseDouble(etRestaurantDelivery.getText().toString()));
            restaurantUpdates.put("tipoDeComida", spinnerTipoComida.getSelectedItem().toString());

            db.collection("restaurantes").document(idRestaurante)
                    .update(restaurantUpdates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Datos del restaurante guardados exitosamente.", Toast.LENGTH_SHORT).show();
                        toggleFields(false);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No se encontró el ID del restaurante.", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            if (requestCode == PICK_LOGO_REQUEST) {
                uploadImage(imageUri, "fotoLogo", ivLogo);
            } else if (requestCode == PICK_PORTADA_REQUEST) {
                uploadImage(imageUri, "fotoPortada", ivPortada);
            }
        }
    }

    private void uploadImage(Uri imageUri, String fieldName, ImageView imageView) {
        StorageReference fileRef = storageReference.child("restaurantes/" + idRestaurante + "/" + fieldName + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Actualizar URL en Firestore
                            db.collection("restaurantes").document(idRestaurante)
                                    .update(fieldName, uri.toString())
                                    .addOnSuccessListener(aVoid -> {
                                        Glide.with(this).load(uri).into(imageView);
                                        Toast.makeText(this, "Imagen actualizada exitosamente.", Toast.LENGTH_SHORT).show();
                                    });
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void toggleFields(boolean enable) {
        etRestaurantName.setEnabled(enable);
        etRestaurantEslogan.setEnabled(enable);
        etRestaurantUbicacion.setEnabled(enable);
        etRestaurantDescripcion.setEnabled(enable);
        etRestaurantDelivery.setEnabled(enable);
        spinnerTipoComida.setEnabled(enable);
    }
}
