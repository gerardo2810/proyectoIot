package com.example.proyecto_iot.superadmin;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class editar_perfil_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView imageViewSelected;
    private Uri imageUriSelected; // URI de la imagen seleccionada
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;

    // Declaración de los EditText
    private EditText etNombre, etApellido, etDni, etTelefono, etDomicilio;
    private Button btnSavePersonalInfo, buttonUploadImage;
    private TextView et_fecha, et_correo;

    // Firebase Auth y Firestore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // SharedPreferences para almacenar la información de manera persistente
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_editar_perfil);

        imageViewSelected = findViewById(R.id.imageViewSelected);

        // Inicializar Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();
        buttonUploadImage = findViewById(R.id.buttonUploadImage);

        buttonUploadImage.setOnClickListener(v -> openImageChooser());

        // Inicializar los EditText y el botón
        etNombre = findViewById(R.id.et_nombre);
        etApellido = findViewById(R.id.et_apellido);
        etDni = findViewById(R.id.et_dni);
        et_fecha = findViewById(R.id.et_fecha);
        et_correo = findViewById(R.id.et_correo);
        etTelefono = findViewById(R.id.et_telefono);
        etDomicilio = findViewById(R.id.et_domicilio);
        btnSavePersonalInfo = findViewById(R.id.btn_save_personal_info);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Cargar datos existentes (si hay) desde SharedPreferences
        loadUserData();

        // Configurar el evento de clic para guardar la información
        btnSavePersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        //Volver una pantalla atras
        LinearLayout regresar = findViewById(R.id.header_layout);

        regresar.setOnClickListener(v -> {
            Intent intent = new Intent(this, perfil_superadmin.class);
            startActivity(intent);
            finish();
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
                if (item.getItemId() == R.id.navigation_usuarios) {
                    intent = new Intent(editar_perfil_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(editar_perfil_superadmin.this, gestion_reportes_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(editar_perfil_superadmin.this, perfil_superadmin.class);
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
                imageViewSelected.setImageURI(imageUriSelected);
                imageViewSelected.setBackground(null);
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

    private void loadUserData() {
        String currentUserId = mAuth.getCurrentUser().getUid();  // Obtener el UID del usuario logueado

        // Consultar Firestore para obtener los datos del superadmin con el UID correspondiente
        db.collection("superadmin").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        // Obtener los datos del documento
                        String nombre = documentSnapshot.getString("nombre");
                        String apellido = documentSnapshot.getString("apellido");
                        String dni = documentSnapshot.getString("dni");
                        String fechaNacimiento = documentSnapshot.getString("fechaNacimiento");
                        String correo = documentSnapshot.getString("correo");
                        String telefono = documentSnapshot.getString("telefono");
                        String domicilio = documentSnapshot.getString("direccion");
                        String foto = documentSnapshot.getString("foto");

                        // Cargar los datos en los EditText
                        etNombre.setText(nombre);
                        etApellido.setText(apellido);
                        etDni.setText(dni);
                        et_fecha.setText(fechaNacimiento);
                        et_correo.setText(correo);
                        etTelefono.setText(telefono);
                        etDomicilio.setText(domicilio);

                        if (foto == null || foto.isEmpty() || foto.equals("no tiene")) {
                            // Mostrar el ícono predeterminado si no hay una foto válida
                            imageViewSelected.setImageResource(R.drawable.superadmin_icon);
                        } else {
                            // Mostrar la foto almacenada usando Glide
                            Glide.with(this)
                                    .load(foto) // Carga directa desde la URL de la foto
                                    .placeholder(R.drawable.load) // Placeholder mientras se carga la imagen
                                    .error(R.drawable.superadmin_icon) // En caso de error, mostrar el ícono predeterminado
                                    .into(imageViewSelected);
                        }

                    } else {
                        // Si no se encuentra ningún documento o hay un error
                        Toast.makeText(this, "No se pudo cargar la información del perfil", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para guardar los datos ingresados
    private void saveUserData() {
        // Obtener los valores ingresados en los EditText
        String nombre = etNombre.getText().toString();
        String apellido = etApellido.getText().toString();
        String dni = etDni.getText().toString();
        String telefono = etTelefono.getText().toString();
        String domicilio = etDomicilio.getText().toString();

        // Validar que los campos no estén vacíos (puedes agregar más validaciones si es necesario)
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || telefono.isEmpty() || domicilio.isEmpty()) {
            Toast.makeText(this, "Todos los campos deben ser completados", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación del nombre y apellido
        if (nombre.length() < 3 || apellido.length() < 3) {
            Toast.makeText(this, "Nombre y apellido deben tener al menos 3 letras", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación del DNI
        if (dni.length() != 8 || !dni.matches("\\d+")) {
            Toast.makeText(this, "El DNI debe tener 8 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación del teléfono
        if (telefono.length() != 9 || !telefono.startsWith("9") || !telefono.matches("\\d+")) {
            Toast.makeText(this, "El teléfono debe tener 9 dígitos y comenzar con 9", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación del domicilio
        if (domicilio.length() < 5) {
            Toast.makeText(this, "El domicilio debe tener al menos 5 letras", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar los datos en SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("et_nombre", nombre);
        editor.putString("et_apellido", apellido);
        editor.putString("et_dni", dni);
        editor.putString("et_telefono", telefono);
        editor.putString("et_domicilio", domicilio);
        editor.apply();

        // También, podrías actualizar estos datos en Firestore si lo deseas:
        String currentUserId = mAuth.getCurrentUser().getUid();

        if (imageUriSelected != null) {
            StorageReference fileRef = storageReference.child("fotosSuperadmin/" + currentUserId + ".jpg");
            UploadTask uploadTask = fileRef.putFile(imageUriSelected);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String fotoUrl = uri.toString();

                    // Guardar datos en Firestore, incluyendo la URL de la foto
                    db.collection("superadmin").document(currentUserId)
                            .update("foto", fotoUrl, "nombre", nombre, "apellido", apellido, "dni", dni, "telefono", telefono, "direccion", domicilio)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                            });
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Si no se seleccionó una nueva imagen, guardar otros datos
            db.collection("superadmin").document(currentUserId)
                    .update("nombre", nombre, "apellido", apellido, "dni", dni, "telefono", telefono, "direccion", domicilio)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
