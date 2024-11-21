package com.example.proyecto_iot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.cliente.InicioClienteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CompleteRegisterActivity extends AppCompatActivity {
    private EditText etNombre, etApellido, etDNI, etNacimiento, etDireccion, etTelefono;
    private Button btnRegister;
    private String userID, email;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_register);

        // Referencias a los elementos del layout
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etDNI = findViewById(R.id.etDNI);
        etNacimiento = findViewById(R.id.etNacimiento);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        btnRegister = findViewById(R.id.btnRegister);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        email = getIntent().getStringExtra("email"); // Obtener email de Google

        btnRegister.setOnClickListener(view -> createUser());
    }

    private void createUser() {
        String name = etNombre.getText().toString().trim();
        String lastname = etApellido.getText().toString().trim();
        String dni = etDNI.getText().toString().trim();
        String birthdate = etNacimiento.getText().toString().trim();
        String address = etDireccion.getText().toString().trim();
        String phone = etTelefono.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etNombre.setError("Ingrese un Nombre");
            etNombre.requestFocus();
        } else if (TextUtils.isEmpty(lastname)) {
            etApellido.setError("Ingrese un Apellido");
            etApellido.requestFocus();
        } else if (TextUtils.isEmpty(dni)) {
            etDNI.setError("Ingrese un DNI");
            etDNI.requestFocus();
        } else if (TextUtils.isEmpty(birthdate)) {
            etNacimiento.setError("Ingrese su Fecha de Nacimiento");
            etNacimiento.requestFocus();
        } else if (TextUtils.isEmpty(address)) {
            etDireccion.setError("Ingrese su Dirección");
            etDireccion.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            etTelefono.setError("Ingrese su Teléfono");
            etTelefono.requestFocus();
        } else {
            userID = mAuth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("clientes").document(userID);

            // Datos predeterminados
            String defaultPhotoUrl = "https://cdn-icons-png.flaticon.com/256/3849/3849119.png";

            // Crear mapa de datos
            Map<String, Object> cliente = new HashMap<>();
            cliente.put("Nombre", name);
            cliente.put("Apellido", lastname);
            cliente.put("DNI", dni);
            cliente.put("Nacimiento", birthdate);
            cliente.put("Direccion", address);
            cliente.put("Telefono", phone);
            cliente.put("Email", email);
            cliente.put("FotoURL", defaultPhotoUrl);

            // Guardar datos en Firestore
            documentReference.set(cliente).addOnSuccessListener(unused -> {
                Log.d("TAG", "Datos registrados con éxito: " + userID);
                Toast.makeText(CompleteRegisterActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CompleteRegisterActivity.this, InicioClienteActivity.class));
                finish();
            }).addOnFailureListener(e -> Log.e("TAG", "Error al registrar: " + e.getMessage()));
        }
    }
}
