package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.Administrador;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteSA;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class registro_admin_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Spinner spinnerTipoAdmin;
    private EditText editTextNombre, editTextApellido, editTextCorreo, editTextPasswd, editTextDNI, editTextEdad, editTextDireccion, editTextTelefono;
    private String nombre, apellido, correo, passwd, dni, edad, direccion, telefono;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgPreview;
    private Button btnSelectPhoto, btnUploadPhoto;
    private Uri imageUri; // Uri de la imagen seleccionada
    private StorageReference storageReference;

    Button btnRegistrar;

    Administrador administrador;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_registro_admin);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        imgPreview = findViewById(R.id.imgPreview);
        btnSelectPhoto = findViewById(R.id.buttonUploadImage);
        storageReference = FirebaseStorage.getInstance().getReference("fotosAdministradores");
        // Botón para seleccionar una foto
        btnSelectPhoto.setOnClickListener(v -> selectImage());

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(registro_admin_superadmin.this, gestion_usuarios_superadmin.class);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion del Formulario
        spinnerTipoAdmin = findViewById(R.id.spinnerTipoAdmin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_tipoRegistro, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAdmin.setAdapter(adapter);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextPasswd = findViewById(R.id.editTextPasswd);
        editTextDNI = findViewById(R.id.editTextDNI);
        editTextEdad = findViewById(R.id.editTextEdad);
        editTextDireccion = findViewById(R.id.editTextDireccion);
        editTextTelefono = findViewById(R.id.editTextTelefono);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(v -> registrarAdministrador());
        //----------------------------------------------------------------------------


        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_usuarios);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_usuarios) {
                    intent = new Intent(registro_admin_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(registro_admin_superadmin.this, gestion_reportes_superadmin.class);
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

    private void registrarAdministrador() {

        administrador = new Administrador();

        nombre = editTextNombre.getText().toString();
        apellido = editTextApellido.getText().toString();
        correo = editTextCorreo.getText().toString();
        passwd = editTextPasswd.getText().toString();
        dni = editTextDNI.getText().toString();
        edad = editTextEdad.getText().toString();
        direccion = editTextDireccion.getText().toString();
        telefono = editTextTelefono.getText().toString();

        if(verificaDatos()){
            String tipoSeleccionado = spinnerTipoAdmin.getSelectedItem().toString();

            if (tipoSeleccionado.equals("-Seleccionar-")) {
                Toast.makeText(this, "Debe seleccionar una opción en Restaurante", Toast.LENGTH_SHORT).show();
            } else if (tipoSeleccionado.equals("Existente")) {

                existeCorreo(correo, new OnCorreoExistenteListener() {
                    @Override
                    public void onCorreoExistente(boolean existe) {
                        if (existe) {
                            Toast.makeText(registro_admin_superadmin.this, "El correo ya ha sido registrado en la app.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Subir la imagen primero
                            String imageFileName = UUID.randomUUID().toString();
                            StorageReference fileRef = storageReference.child(imageFileName);
                            Toast.makeText(registro_admin_superadmin.this, "Espere un momento...", Toast.LENGTH_SHORT).show();
                            fileRef.putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Obtener la URL de descarga
                                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            String downloadUrl = uri.toString(); // URL de la imagen
                                            administrador.setFoto(downloadUrl);
                                            administrador.setNombre(nombre);
                                            administrador.setApellido(apellido);
                                            administrador.setCorreo(correo);
                                            administrador.setContraseña(passwd);
                                            administrador.setDni(dni);
                                            administrador.setEdad(edad);
                                            administrador.setDireccion(direccion);
                                            administrador.setTelefono(telefono);

                                            mostrarDialogRestaurante();
                                        });
                                    });

                        }
                    }
                });

            } else if (tipoSeleccionado.equals("Nuevo")) {

                existeCorreo(correo, new OnCorreoExistenteListener() {
                    @Override
                    public void onCorreoExistente(boolean existe) {
                        if (existe) {
                            Toast.makeText(registro_admin_superadmin.this, "El correo ya ha sido registrado en la app.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Subir la imagen primero
                            String imageFileName = UUID.randomUUID().toString();
                            StorageReference fileRef = storageReference.child(imageFileName);
                            Toast.makeText(registro_admin_superadmin.this, "Espere un momento...", Toast.LENGTH_SHORT).show();
                            fileRef.putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Obtener la URL de descarga
                                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            String downloadUrl = uri.toString(); // URL de la imagen
                                            administrador.setFoto(downloadUrl);
                                            administrador.setNombre(nombre);
                                            administrador.setApellido(apellido);
                                            administrador.setCorreo(correo);
                                            administrador.setContraseña(passwd);
                                            administrador.setDni(dni);
                                            administrador.setEdad(edad);
                                            administrador.setDireccion(direccion);
                                            administrador.setTelefono(telefono);

                                            Intent intent = new Intent(registro_admin_superadmin.this, registro_restaurante_superadmin.class);
                                            intent.putExtra("administrador", administrador);
                                            startActivity(intent);
                                        });
                                    });

                        }
                    }
                });
            }
        }

    }

    private boolean verificaDatos(){

        if(nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || direccion.isEmpty() || passwd.isEmpty() || dni.isEmpty() || edad.isEmpty() || telefono.isEmpty()){
            Toast.makeText(this, "Debe rellenar todos los datos", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwd.length() < 5 || !passwd.matches(".*[!@#$%^&*(),.?\":{}|<>].*") || !passwd.matches(".*[A-Z].*") || !passwd.matches(".*\\d.*")) {
            Toast.makeText(this, "La contraseña debe: Tener al menos 5 caracteres, 1 carácter especial, 1 mayúscula y 1 número", Toast.LENGTH_LONG).show();
            return false;
        }
        if(dni.length() != 8){
            Toast.makeText(this, "El dni debe tener 8 digitos", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(telefono.length() != 9){
            Toast.makeText(this, "El telefono debe tener 9 digitos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Debe seleccionar una imagen", Toast.LENGTH_LONG).show();
            return false;
        }

        String patronCorreo = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!correo.matches(patronCorreo)) {
            Toast.makeText(this, "El correo no tiene un formato válido. Ejemplo: ejemplo@dominio.com", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void existeCorreo(final String correo, final OnCorreoExistenteListener listener) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(correo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().getSignInMethods().isEmpty()) {
                            listener.onCorreoExistente(true); // El correo ya está registrado
                        } else {
                            listener.onCorreoExistente(false); // El correo no está registrado
                        }
                    } else {
                        Toast.makeText(this, "Error al verificar el correo", Toast.LENGTH_SHORT).show();
                        listener.onCorreoExistente(false); // Error en la verificación
                    }
                });
    }

    // Interfaz de Callback
    public interface OnCorreoExistenteListener {
        void onCorreoExistente(boolean existe);
    }

    private void mostrarDialogRestaurante() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elija el restaurante al que pertenecerá el administrador:");

        ArrayList<String> listaRestaurantes = new ArrayList<>();
        HashMap<String, String> mapaRestaurantes = new HashMap<>();

        // Crear el spinner para el diálogo
        Spinner spinnerRestaurante = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaRestaurantes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRestaurante.setAdapter(adapter);

        db.collection("restaurantes")
                .whereEqualTo("idAdministrador", "no tiene")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Limpiar la lista antes de llenarla
                        listaRestaurantes.clear();

                        // Agregar la opción predeterminada al spinner
                        listaRestaurantes.add("-Seleccionar-");

                        // Iterar sobre los documentos obtenidos
                        for (DocumentSnapshot document : task.getResult()) {
                            // Obtener el nombre del restaurante
                            String nombreRestaurante = document.getString("nombre");
                            String idRestaurante = document.getId();

                            if (nombreRestaurante != null) {
                                listaRestaurantes.add(nombreRestaurante);
                                mapaRestaurantes.put(nombreRestaurante, idRestaurante); // Guardar el ID del restaurante
                            }
                        }

                        // Notificar al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();

                        // Verificar si se encontraron restaurantes
                        if (listaRestaurantes.isEmpty()) {
                            // Si no hay restaurantes disponibles, mostrar un Toast y no mostrar el diálogo
                            Toast.makeText(this, "No hay restaurantes disponibles", Toast.LENGTH_SHORT).show();
                        } else {
                            // Si hay restaurantes, agregar el spinner al diálogo y mostrarlo
                            builder.setView(spinnerRestaurante);
                            builder.setPositiveButton("OK", (dialog, which) -> {
                                String restauranteSeleccionado = spinnerRestaurante.getSelectedItem().toString();
                                if (restauranteSeleccionado.equals("-Seleccionar-")) {
                                    Toast.makeText(this, "Debe elegir un restaurante", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Obtener el ID del restaurante seleccionado
                                    String idRestauranteSeleccionado = mapaRestaurantes.get(restauranteSeleccionado);

                                    registrarAdmin(administrador.getCorreo(), administrador.getContraseña(), restauranteSeleccionado, idRestauranteSeleccionado);

                                }
                            });

                            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    } else {
                        Toast.makeText(this, "Error al cargar los restaurantes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrarAdmin(String email, String password, String nombrerestaurante, String idrestauranteSeleccionado) {
        Toast.makeText(this, "Espere un momento...", Toast.LENGTH_SHORT).show();
        administrador.setRestaurante(nombrerestaurante);
        db.collection("administradores")
                .add(administrador)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idAdministrador = task.getResult().getId();

                        db.collection("restaurantes").document(idrestauranteSeleccionado)
                                .update("idAdministrador", idAdministrador)
                                .addOnCompleteListener(aVoid -> {
                                    Toast.makeText(this, "Administrador nuevo asignado a " + idrestauranteSeleccionado, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(registro_admin_superadmin.this, gestion_usuarios_superadmin.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar el restaurante", Toast.LENGTH_SHORT).show());

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
}