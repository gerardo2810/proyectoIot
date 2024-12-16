package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetalleUsuarioActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ImageButton backButton;
    private ImageView ivImagenUsuario;
    private TextView etNombre;
    private TextView etEdad;
    private TextView etDni;
    private TextView etCorreo;
    private TextView etTelefono;
    private TextView etTotalGastado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_detalle_usuario);

        db = FirebaseFirestore.getInstance();

        // Referencias a las vistas del layout
        backButton = findViewById(R.id.back_button);
        ivImagenUsuario = findViewById(R.id.iv_imagen_usuario);
        etNombre = findViewById(R.id.et_nombre);
        etEdad = findViewById(R.id.et_Edad);
        etDni = findViewById(R.id.et_dni);
        etCorreo = findViewById(R.id.et_correo);
        etTelefono = findViewById(R.id.et_telefono);
        etTotalGastado = findViewById(R.id.et_total_gastado);

        // Obtener idCliente y gastado del intent
        String idCliente = getIntent().getStringExtra("idCliente");
        String gastado = getIntent().getStringExtra("gastado");

        // Asignar el valor de gastado proveniente del Intent
        if (gastado != null && !gastado.isEmpty()) {
            etTotalGastado.setText(gastado);
        } else {
            etTotalGastado.setText("N/A");
        }

        // Consulta a la colección "clientes" para obtener los datos del cliente
        db.collection("clientes").document(idCliente).get()
                .addOnSuccessListener(docSnapshot -> {
                    if (docSnapshot.exists()) {
                        String nombre = docSnapshot.getString("Nombre");
                        String apellido = docSnapshot.getString("Apellido");
                        String dni = docSnapshot.getString("DNI");
                        String direccion = docSnapshot.getString("Direccion");
                        String email = docSnapshot.getString("Email");
                        String fotoURL = docSnapshot.getString("FotoURL");
                        String nacimiento = docSnapshot.getString("Nacimiento");
                        String telefono = docSnapshot.getString("Telefono");
                        Boolean habilitado = docSnapshot.getBoolean("habilitado");

                        // favoritos es un array de Strings
                        List<String> favoritos = (List<String>) docSnapshot.get("favoritos");

                        // Asigna los valores a tus TextViews
                        if (nombre != null && apellido != null) {
                            etNombre.setText(nombre + " " + apellido);
                        }
                        etEdad.setText("N/A"); // Si quisieras calcularla a partir de nacimiento podrías hacerlo
                        if (dni != null) etDni.setText(dni);
                        if (email != null) etCorreo.setText(email);
                        if (telefono != null) etTelefono.setText(telefono);

                        // Cargar la foto del usuario con Glide
                        if (fotoURL != null && !fotoURL.isEmpty()) {
                            Glide.with(this)
                                    .load(fotoURL)
                                    .placeholder(R.drawable.user)
                                    .into(ivImagenUsuario);
                        }

                    } else {
                        Toast.makeText(this, "No se encontraron datos del cliente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos del cliente", Toast.LENGTH_SHORT).show();
                });

        // Botón atrás
        backButton.setOnClickListener(v -> finish());
    }
}
