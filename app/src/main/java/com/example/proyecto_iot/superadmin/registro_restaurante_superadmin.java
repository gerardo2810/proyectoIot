package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.Administrador;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteSA;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class registro_restaurante_superadmin extends AppCompatActivity {

    Administrador administrador;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private EditText textFieldNombre, textFieldUbicacion, textFieldDescripccion;
    private Spinner spinnerTipoComida;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgPreview;
    private Button btnSelectPhoto, btnUploadPhoto;
    private Uri imageUri; // Uri de la imagen seleccionada
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_registro_restaurante);

        imgPreview = findViewById(R.id.imgPreview);
        btnSelectPhoto = findViewById(R.id.buttonUploadImage);
        storageReference = FirebaseStorage.getInstance().getReference("iconosRestaurantes");
        // Botón para seleccionar una foto
        btnSelectPhoto.setOnClickListener(v -> selectImage());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        administrador = (Administrador) getIntent().getSerializableExtra("administrador");


        Button btnGuardar = findViewById(R.id.buttonRegistrarRest);

        textFieldNombre = findViewById(R.id.editTextNombreRest);
        textFieldUbicacion = findViewById(R.id.editTextUbicacionRest);
        textFieldDescripccion = findViewById(R.id.editTextDescripcionRest);

        //Gestion de spinner tipo comida
        spinnerTipoComida = findViewById(R.id.spinnerTipoComida);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.tipo_de_comidas, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoComida.setAdapter(adapter1);
        //----------------------------------------------------------------------------

        btnGuardar.setOnClickListener(view -> {
            String nombre = textFieldNombre.getText().toString();
            String ubicacion = textFieldUbicacion.getText().toString();
            String tipoComida = spinnerTipoComida.getSelectedItem().toString();
            String descripcion = textFieldDescripccion.getText().toString();

            if (nombre.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty() || tipoComida.equals("-Seleccionar-")) {
                Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
            } else if (imageUri == null) {
                Toast.makeText(this, "Debe seleccionar una imagen", Toast.LENGTH_LONG).show();
            } else {
                btnGuardar.setEnabled(false);

                registrarAdmin(administrador.getCorreo(), administrador.getContraseña());

            }
        });

    }

    private void registrarAdmin(String email, String password) {
        Toast.makeText(this, "Espere un momento...", Toast.LENGTH_SHORT).show();
        administrador.setRestaurante(textFieldNombre.getText().toString());
        db.collection("administradores")
                .add(administrador)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idAdministrador = task.getResult().getId();

                        RestauranteSA restaurante = new RestauranteSA();
                        restaurante.setIdAdministrador(idAdministrador); // Establecer el ID del administrador

                        String imageFileName = UUID.randomUUID().toString();
                        StorageReference fileRef = storageReference.child(imageFileName);
                        fileRef.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Obtener la URL de descarga de la imagen
                                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        String downloadUrl = uri.toString(); // URL de la imagen
                                        restaurante.setFotoLogo(downloadUrl);
                                        restaurante.setNombre(textFieldNombre.getText().toString());
                                        restaurante.setUbicacion(textFieldUbicacion.getText().toString());
                                        restaurante.setTipoDeComida(spinnerTipoComida.getSelectedItem().toString());
                                        restaurante.setDescripción(textFieldDescripccion.getText().toString());

                                        // Finalmente, agregamos el restaurante a Firestore
                                        db.collection("restaurantes")
                                                .add(restaurante)
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(this, "Administrador y restaurante registrados exitosamente", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(this, gestion_usuarios_superadmin.class);
                                                    startActivity(intent);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, "Algo ocurrió al intentar registrar el restaurante", Toast.LENGTH_SHORT).show();
                                                });
                                    });
                                });

                    }
                });

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // Error al registrar
                        Toast.makeText(this, "Error registro admin: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, gestion_usuarios_superadmin.class);
                        startActivity(intent);
                    }
                });
    }

    // Método para seleccionar una imagen desde la galería
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    // Método para manejar el resultado de la selección de la imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Uri de la imagen seleccionada
            imgPreview.setImageURI(imageUri); // Mostrar la imagen seleccionada en el ImageView
        }
    }

}