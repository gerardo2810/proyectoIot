package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuClienteActivity extends AppCompatActivity {
    private TextView headerTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_cliente);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Encuentra el ImageView por su ID
        ImageView backArrow = findViewById(R.id.back_arrow);

        // Configura un listener para el clic
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirige a InicioClienteActivity
                Intent intent = new Intent(MenuClienteActivity.this, InicioClienteActivity.class);
                startActivity(intent);

                // (Opcional) Finaliza la actividad actual si no quieres que el usuario regrese aquí
                finish();
            }
        });

        // Manejar evento "Cerrar Sesión"
        LinearLayout cerrarSesionLayout = findViewById(R.id.cerrar_sesion_layout); // Asegúrate de asignar un ID al LinearLayout en tu XML
        cerrarSesionLayout.setOnClickListener(v -> {
            // Cerrar sesión en Firebase
            FirebaseAuth.getInstance().signOut();

            // Ir a la vista de inicio de sesión o pantalla inicial
            Intent intent = new Intent(MenuClienteActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el stack de actividades
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        });

        // Manejar evento "Editar Datos Personales"
        View editarDatosPersonalesLayout = findViewById(R.id.edit_personal_info);
        editarDatosPersonalesLayout.setOnClickListener(v -> {
            // Ir a la vista PerfilClienteActivity
            Intent intent = new Intent(MenuClienteActivity.this, PerfilClienteActivity.class);
            startActivity(intent);
        });

        // Vincular el TextView del encabezado
        headerTitle = findViewById(R.id.header_title);

        // Obtener el ID del usuario autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Acceder a Firebase Firestore para obtener los datos del usuario
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clientes")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtener el nombre del usuario desde Firestore
                        String nombre = documentSnapshot.getString("Nombre");
                        String apellido = documentSnapshot.getString("Apellido");


                        // Actualizar el texto del encabezado
                        if (nombre != null && !nombre.isEmpty()) {
                            headerTitle.setText(" Hola " + nombre + " " + apellido);
                        } else {
                            headerTitle.setText("Hola Usuario");
                        }
                    } else {
                        headerTitle.setText("Hola Usuario");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar errores
                    headerTitle.setText(" Hola Usuario");
                    e.printStackTrace();
                });

    }
}
