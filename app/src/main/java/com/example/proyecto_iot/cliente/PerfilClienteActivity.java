package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PerfilClienteActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etDireccion, etCorreo, etTelefono,etDni,etEdad;
    private ImageView ivFoto;
    private Button btnEditar, btnGuardar, btnSubirFoto;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private String userId; // ID del usuario actual
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_cliente);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Obtener usuario actual
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "Sesión no iniciada.", Toast.LENGTH_SHORT).show();
            finish();
        }


        // Inicializar vistas
        etNombre = findViewById(R.id.et_name);
        etApellido = findViewById(R.id.et_apellido);
        etDireccion = findViewById(R.id.et_direccion);
        etCorreo = findViewById(R.id.et_correo);
        etTelefono = findViewById(R.id.et_telefono);
        ivFoto = findViewById(R.id.imageView5);
        etEdad = findViewById(R.id.et_fecha);
        etDni=findViewById(R.id.et_dni);


        btnEditar = findViewById(R.id.editar_datos);
        btnGuardar = findViewById(R.id.btn_save_personal_info);
        btnSubirFoto = findViewById(R.id.btn_upload_image);


        // Bloquear edición inicial
        toggleFields(false);
        // Cargar datos del cliente
        fetchClienteData();

        btnEditar.setOnClickListener(v -> toggleFields(true));

        btnGuardar.setOnClickListener(v -> saveClienteData());

        btnSubirFoto.setOnClickListener(v -> selectImage());
        // Encontrar el botón por su ID
        ImageButton backButton = findViewById(R.id.back_button);

        // Configurar el listener del botón
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para iniciar MenuActivity
                Intent intent = new Intent(PerfilClienteActivity.this, MenuClienteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchClienteData() {
        db.collection("clientes").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        etNombre.setText(documentSnapshot.getString("Nombre"));
                        etApellido.setText(documentSnapshot.getString("Apellido"));
                        etDireccion.setText(documentSnapshot.getString("Direccion"));
                        etCorreo.setText(documentSnapshot.getString("Email"));
                        etTelefono.setText(documentSnapshot.getString("Telefono"));
                        etDni.setText(documentSnapshot.getString("DNI"));
                        etEdad.setText(documentSnapshot.getString("Nacimiento"));// Mostrar el DNI
                        // Calcular edad a partir de la fecha de nacimiento


                        String fotoUrl = documentSnapshot.getString("FotoURL");
                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            Glide.with(this).load(fotoUrl).into(ivFoto);
                        }

                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos.", Toast.LENGTH_SHORT).show());
    }
    private int calcularEdad(String fechaNacimiento) {
        try {
            // Formato esperado: "yyyyMMdd"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate fechaNac = LocalDate.parse(fechaNacimiento, formatter);
            LocalDate fechaActual = LocalDate.now();

            // Calcular la diferencia en años
            Period periodo = Period.between(fechaNac, fechaActual);
            return Math.abs(periodo.getYears()); // Retorna un número entero positivo
        } catch (Exception e) {
            Toast.makeText(this, "Error al calcular la edad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return 0; // Si hay error, retorna 0
        }
    }


    private void saveClienteData() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("Nombre", etNombre.getText().toString());
        updates.put("Apellido", etApellido.getText().toString());
        updates.put("Direccion", etDireccion.getText().toString());
        updates.put("Email", etCorreo.getText().toString());
        updates.put("Telefono", etTelefono.getText().toString());

        db.collection("clientes").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Datos actualizados.", Toast.LENGTH_SHORT).show();
                    toggleFields(false);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar datos.", Toast.LENGTH_SHORT).show());
    }

    private void toggleFields(boolean enable) {
        etNombre.setEnabled(enable);
        etApellido.setEnabled(enable);
        etDireccion.setEnabled(enable);
        etCorreo.setEnabled(enable);
        etDni.setEnabled(false);
        etTelefono.setEnabled(enable);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        StorageReference fileRef = storageReference.child("fotosClientes/" + userId + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            db.collection("clientes").document(userId)
                                    .update("FotoURL", uri.toString())
                                    .addOnSuccessListener(aVoid -> {
                                        Glide.with(this).load(uri).into(ivFoto);
                                        Toast.makeText(this, "Foto actualizada.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar URL de foto.", Toast.LENGTH_SHORT).show());
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Error al subir imagen.", Toast.LENGTH_SHORT).show());
    }
}
