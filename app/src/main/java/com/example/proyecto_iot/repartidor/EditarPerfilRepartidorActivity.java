package com.example.proyecto_iot.repartidor;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.editar_perfil_superadmin;
import com.example.proyecto_iot.superadmin.gestion_reportes_superadmin;
import com.example.proyecto_iot.superadmin.gestion_usuarios_superadmin;
import com.example.proyecto_iot.superadmin.perfil_superadmin;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilRepartidorActivity extends AppCompatActivity {

    private StorageReference storageReference;
    BottomNavigationView bottomNavigationView;
    EditText etNombre, etApellido, etFecha, etDireccion, etCorreo, etTelefono, etDni;
    Button btnUploadImage, btnGuardarDatos;
    private Uri imageUriSelected; // URI de la imagen seleccionada
    ImageView ivFoto;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_repartidor);

        // Inicializar vistas
        etNombre = findViewById(R.id.et_name);
        etApellido = findViewById(R.id.et_lastname);
        etDni = findViewById(R.id.et_dni);
        etFecha = findViewById(R.id.et_fecha);
        etCorreo = findViewById(R.id.et_correo);
        etTelefono = findViewById(R.id.et_telefono);
        etDireccion = findViewById(R.id.et_domicilio);
        ivFoto = findViewById(R.id.imageViewSelected);
        btnUploadImage = findViewById(R.id.buttonUploadImage);
        btnGuardarDatos = findViewById(R.id.btn_save_personal_info);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        btnUploadImage.setOnClickListener(v -> openImageChooser());

        cargarDatosUsuario();

        // Guardar datos editados
        btnGuardarDatos.setOnClickListener(v -> guardarDatosRepartidor());

        //Volver una pantalla atras
        LinearLayout regresar = findViewById(R.id.header_layout);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresa a PerfilRepartidorActivity
                finish();
            }
        });
        //----------------------------------------------------------------------------
        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_perfil);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_home) {
                    intent = new Intent(EditarPerfilRepartidorActivity.this, InicioRepartidorActivity.class);
                } else if (item.getItemId() == R.id.navigation_historial) {
                    intent = new Intent(EditarPerfilRepartidorActivity.this, HistorialRepartidorActivity.class);
                } else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(EditarPerfilRepartidorActivity.this, PerfilRepartidorActivity.class);
                }
                if (intent != null) {
                    intent.putExtra("SELECTED_ITEM_ID", item.getItemId());
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        //----------------------------------------------------------------------------




    }

    public void cargarDatosUsuario() {
        String userId = auth.getCurrentUser().getUid(); // Obtén el UID del usuario actual

        DocumentReference userDoc = db.collection("repartidores").document(userId);
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Asignar datos a los campos de texto
                etNombre.setText(documentSnapshot.getString("nombre"));
                etApellido.setText(documentSnapshot.getString("apellido"));
                etDni.setText(documentSnapshot.getString("dni"));
                etFecha.setText(documentSnapshot.getString("nacimiento"));
                etCorreo.setText(documentSnapshot.getString("correo"));
                etTelefono.setText(documentSnapshot.getString("telefono"));
                etDireccion.setText(documentSnapshot.getString("direccion"));
                // Cargar imagen de perfil
                String fotoUrl = documentSnapshot.getString("foto");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(fotoUrl)
                            .placeholder(R.drawable.placeholder)
                            .into(ivFoto);
                }
            } else {
                Toast.makeText(this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void guardarDatosRepartidor() {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", etNombre.getText().toString());
        userData.put("apellido", etApellido.getText().toString());
        userData.put("dni", etDni.getText().toString());
        userData.put("nacimiento", etFecha.getText().toString());
        userData.put("correo", etCorreo.getText().toString());
        userData.put("telefono", etTelefono.getText().toString());
        userData.put("direccion", etDireccion.getText().toString());

        if (imageUriSelected != null) {
            StorageReference fileRef = storageReference.child("fotosRepartidores/" + userId + ".jpg");
            UploadTask uploadTask = fileRef.putFile(imageUriSelected);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fotoUrl = uri.toString();

                    userData.put("foto", fotoUrl);

                    /// Guardar los datos en Firestore
                    db.collection("repartidores").document(userId)
                            .update(userData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show();
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                            });
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Si no se seleccionó una nueva imagen, guardar otros datos

            db.collection("repartidores").document(userId)
                    .update(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null && isImageFile(imageUri)) {
                imageUriSelected = imageUri; // Asigna el URI seleccionado
                ivFoto.setImageURI(imageUriSelected);
                ivFoto.setBackground(null);
            } else {
                Toast.makeText(this, "Solo se permiten imágenes JPEG, PNG, JPG o AVIF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isImageFile(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        String type = contentResolver.getType(uri);
        Log.d("Image File Type", "MIME Type: " + type);

        if (type != null) {
            boolean isValidImage = type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/jpg") || type.equals("image/avif");
            Log.d("Image File Validation", "Is Valid Image: " + isValidImage);
            return isValidImage;
        }
        return false;
    }

}
