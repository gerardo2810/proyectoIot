package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PerfilClienteActivity extends AppCompatActivity {

    private ImageView ivImagenUsuario;
    private TextView tvNombreUsuario, tvEdad, tvDni, tvCorreo, tvTelefono, cantPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Configura el listener para el botón
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia MenuClienteActivity
                Intent intent = new Intent(PerfilClienteActivity.this, MenuClienteActivity.class);
                startActivity(intent);

                // (Opcional) Finaliza la actividad actual si no quieres que el usuario regrese aquí
                finish();
            }
        });

        // Referencias a los elementos de la interfaz
        ivImagenUsuario = findViewById(R.id.iv_imagen_usuario);
        tvNombreUsuario = findViewById(R.id.tv_nombre_usuario);
        tvEdad = findViewById(R.id.tv_edad);
        tvDni = findViewById(R.id.tv_dni);
        tvCorreo = findViewById(R.id.tv_correo);
        tvTelefono = findViewById(R.id.tv_telefono);
        cantPedidos = findViewById(R.id.cant_pedidos);

        // Obtener el usuario logueado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Consultar los datos del usuario desde Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clientes").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extraer datos de Firestore
                        String nombre = documentSnapshot.getString("Nombre");
                        String apellido = documentSnapshot.getString("Apellido");
                        String fotoUrl = documentSnapshot.getString("FotoURL");
                        String dni = documentSnapshot.getString("DNI");
                        String correo = documentSnapshot.getString("Email");
                        String telefono = documentSnapshot.getString("Telefono");
                        String nacimiento = documentSnapshot.getString("Nacimiento");
                        List<?> historialPedidos = (List<?>) documentSnapshot.get("historialpedidos");

                        // Actualizar la imagen de usuario
                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(fotoUrl)
                                    .placeholder(R.drawable.user) // Imagen de placeholder mientras se carga
                                    .into(ivImagenUsuario);
                        } else {
                            ivImagenUsuario.setImageResource(R.drawable.user);
                        }

                        // Actualizar el nombre y apellidos
                        tvNombreUsuario.setText(nombre + " " + apellido);

                        // Calcular la edad a partir de la fecha de nacimiento
                        if (nacimiento != null && !nacimiento.isEmpty()) {
                            String edad = nacimiento;
                            tvEdad.setText(edad);
                        } else {
                            tvEdad.setText("N/A");
                        }

                        // Actualizar el DNI
                        tvDni.setText(dni != null ? dni : "N/A");

                        // Actualizar el correo
                        tvCorreo.setText(correo != null ? correo : "N/A");

                        // Actualizar el teléfono
                        tvTelefono.setText(telefono != null ? telefono : "N/A");

                        // Calcular y mostrar la cantidad de pedidos
                        if (historialPedidos != null) {
                            cantPedidos.setText(String.valueOf(historialPedidos.size()));
                        } else {
                            cantPedidos.setText("0");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar errores en la consulta
                    e.printStackTrace();
                });
    }

    // Método para calcular la edad a partir de la fecha de nacimiento
    private int calcularEdad(String fechaNacimiento) {
        try {
            // Formato esperado de la fecha en Firestore: "dd-MM-yy"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
            LocalDate fechaNacimientoLocalDate = LocalDate.parse(fechaNacimiento, formatter);
            LocalDate fechaActual = LocalDate.now();
            return Period.between(fechaNacimientoLocalDate, fechaActual).getYears();
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Retornar 0 si ocurre un error al calcular la edad
        }
    }
}
