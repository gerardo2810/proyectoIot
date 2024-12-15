package com.example.proyecto_iot;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.cliente.EnviarCorreo;
import com.example.proyecto_iot.cliente.PreloaderActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    private EditText etNombre;
    private EditText etApellido;
    private EditText etDNI;
    private EditText etNacimiento;
    private EditText etDireccion;
    private EditText etTelefono;
    private EditText etEmail;
    private TextInputLayout txtPassword;
    private Button btnRegister;
    private TextView lblLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicialización de vistas
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etDNI = findViewById(R.id.etDNI);
        etNacimiento = findViewById(R.id.etNacimiento);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        etNacimiento.setOnClickListener(v -> mostrarDatePicker());


        etEmail = findViewById(R.id.etEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        lblLogin = findViewById(R.id.lblLogin);



        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Botón de registro
        btnRegister.setOnClickListener(view -> {


            // Obtener datos del formulario
            String nombre = etNombre.getText().toString();
            String apellido = etApellido.getText().toString();
            String dni = etDNI.getText().toString();
            String nacimiento = etNacimiento.getText().toString();
            String direccion = etDireccion.getText().toString();
            String telefono = etTelefono.getText().toString();
            String email = etEmail.getText().toString();
            String password = txtPassword.getEditText().getText().toString();

            // Generar un código aleatorio de 4 dígitos
            String codigoVerificacion = generarCodigoVerificacion();
            System.out.println("Registro " + codigoVerificacion);

            // Enviar el correo con el código de verificación
            EnviarCorreo enviarCorreo = new EnviarCorreo(
                    email,
                    "Código de Verificación",
                    "Tu código de verificación es: " + codigoVerificacion
            );
            enviarCorreo.execute();

            // Validar campos
            if (!validarCampos(nombre, apellido, dni, nacimiento, direccion, telefono, email, password)) return;

            // Ir a PreloaderActivity
            Intent intent = new Intent(this, PreloaderActivity.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            intent.putExtra("dni", dni);
            intent.putExtra("nacimiento", nacimiento);
            intent.putExtra("direccion", direccion);
            intent.putExtra("telefono", telefono);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            intent.putExtra("codigo", codigoVerificacion);

            startActivity(intent);
        });


        // Redirección al login
        lblLogin.setOnClickListener(view -> openLoginActivity());
    }

    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        boolean registroCorrecto = getIntent().getBooleanExtra("registroCorrecto", false);
        if (registroCorrecto) {
            // Extraer datos del intent
            String nombre = getIntent().getStringExtra("nombre");
            String apellido = getIntent().getStringExtra("apellido");
            String dni = getIntent().getStringExtra("dni");
            String nacimiento = getIntent().getStringExtra("nacimiento");
            String direccion = getIntent().getStringExtra("direccion");
            String telefono = getIntent().getStringExtra("telefono");
            String email = getIntent().getStringExtra("email");
            String password = getIntent().getStringExtra("password");

            // Crear el cliente
            createCliente(nombre, apellido, dni, nacimiento, direccion, telefono, email, password);
        }
    }

    private void mostrarDatePicker() {
        // Obtener la fecha actual
        final Calendar calendario = Calendar.getInstance();

        // Configurar el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Formatear la fecha seleccionada
                    String fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    etNacimiento.setText(fechaSeleccionada);
                },
                calendario.get(Calendar.YEAR),  // Año inicial
                calendario.get(Calendar.MONTH), // Mes inicial
                calendario.get(Calendar.DAY_OF_MONTH) // Día inicial
        );

        // Restringir la fecha máxima (hoy) para que no se puedan seleccionar fechas futuras
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Mostrar el diálogo
        datePickerDialog.show();
    }


    public void createCliente(String nombre, String apellido, String dni, String nacimiento,
                              String direccion, String telefono, String email, String password) {

        // Creación de usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Usuario creado en Firebase Authentication
                        String userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("clientes").document(userID);

                        String defaultPhotoUrl = "https://cdn-icons-png.flaticon.com/256/3849/3849119.png";

                        // Estructura del documento
                        Map<String, Object> cliente = new HashMap<>();
                        cliente.put("Nombre", nombre);
                        cliente.put("Apellido", apellido);
                        cliente.put("DNI", dni);
                        cliente.put("Nacimiento", nacimiento);
                        cliente.put("Direccion", direccion);
                        cliente.put("Telefono", telefono);
                        cliente.put("Email", email);
                        cliente.put("Contraseña", password);
                        cliente.put("FotoURL", defaultPhotoUrl);
                        cliente.put("habilitado",true);
                        cliente.put("favoritos", new ArrayList<String>());
                        cliente.put("historialPedidos", new ArrayList<String>());


                        // Guardar en Firestore
                        documentReference.set(cliente)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                                    // Cerrar sesión del usuario para evitar problemas
                                    mAuth.signOut();

                                    // Redirigir al login
                                    openLoginActivity();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegisterActivity.this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    System.out.println("Error Firestore: " + e.getMessage());
                                });
                    } else {
                        // Error en Firebase Authentication
                        Toast.makeText(RegisterActivity.this, "Error al crear usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Error Authentication: " + task.getException().getMessage());
                    }
                });
    }


    private String generarCodigoVerificacion() {
        Random random = new Random();
        int numero = 1000 + random.nextInt(9000); // Genera un número entre 1000 y 9999
        return String.valueOf(numero);
    }
    private boolean validarCampos(String nombre, String apellido, String dni, String nacimiento,
                                  String direccion, String telefono, String email, String password) {

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingrese un Nombre");
            etNombre.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError("Ingrese un Apellido");
            etApellido.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dni)) {
            etDNI.setError("Ingrese un DNI");
            etDNI.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(nacimiento)) {
            etNacimiento.setError("Ingrese una Fecha de Nacimiento");
            etNacimiento.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(direccion)) {
            etDireccion.setError("Ingrese una Dirección");
            etDireccion.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError("Ingrese un Teléfono");
            etTelefono.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese un Correo");
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Ingrese una Contraseña");
            txtPassword.requestFocus();
            return false;
        }

        return true;
    }


}
