package com.example.proyecto_iot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
        etEmail = findViewById(R.id.etEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        lblLogin = findViewById(R.id.lblLogin);

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Botón de registro
        btnRegister.setOnClickListener(view -> createCliente());

        // Redirección al login
        lblLogin.setOnClickListener(view -> openLoginActivity());
    }

    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void createCliente() {
        // Obtención de datos del formulario
        String nombre = etNombre.getText().toString();
        String apellido = etApellido.getText().toString();
        String dni = etDNI.getText().toString();
        String nacimiento = etNacimiento.getText().toString();
        String direccion = etDireccion.getText().toString();
        String telefono = etTelefono.getText().toString();
        String email = etEmail.getText().toString();
        String password = txtPassword.getEditText().getText().toString();

        // Validación de campos
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingrese un Nombre");
            etNombre.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError("Ingrese un Apellido");
            etApellido.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dni)) {
            etDNI.setError("Ingrese un DNI");
            etDNI.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nacimiento)) {
            etNacimiento.setError("Ingrese una Fecha de Nacimiento");
            etNacimiento.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(direccion)) {
            etDireccion.setError("Ingrese una Dirección");
            etDireccion.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError("Ingrese un Teléfono");
            etTelefono.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese un Correo");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Ingrese una Contraseña");
            txtPassword.requestFocus();
            return;
        }

        // Creación de usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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

                        // Guardar en Firestore
                        documentReference.set(cliente).addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            openLoginActivity();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(RegisterActivity.this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
