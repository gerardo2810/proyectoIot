package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ViewRestaurantScheduleActivity extends AppCompatActivity {
    private EditText etLunes, etMartes, etMiercoles, etJueves, etViernes, etSabado, etDomingo;
    private Button btnGuardar;
    private TextView btnEditar;
    private FirebaseFirestore db;
    private String idRestaurante;
    private TextView tvRestaurantName, tvCuisineType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_horarios);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Recuperar idRestaurante del intent
        idRestaurante = getIntent().getStringExtra("idRestaurante");

        // Inicializar vistas
        tvRestaurantName = findViewById(R.id.restaurant_name);
        tvCuisineType = findViewById(R.id.cuisine_type);
        etLunes = findViewById(R.id.et_lunes);
        etMartes = findViewById(R.id.et_martes);
        etMiercoles = findViewById(R.id.et_miercoles);
        etJueves = findViewById(R.id.et_jueves);
        etViernes = findViewById(R.id.et_viernes);
        etSabado = findViewById(R.id.et_sabado);
        etDomingo = findViewById(R.id.et_domingo);
        btnEditar = findViewById(R.id.btn_editar_horarios);
        btnGuardar = findViewById(R.id.btn_guardar_horarios);

        // Bloquear campos inicialmente
        toggleFields(false);

        // Cargar horarios
        if (idRestaurante != null) {
            fetchRestaurantData(idRestaurante);
            fetchScheduleData(idRestaurante);
        } else {
            Toast.makeText(this, "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Habilitar campos para edición
        btnEditar.setOnClickListener(v -> toggleFields(true));

        // Guardar cambios
        btnGuardar.setOnClickListener(v -> saveScheduleData());

        // Botón de retroceso
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());


        Button btnCloseRestaurant = findViewById(R.id.btn_close_restaurant);
        btnCloseRestaurant.setOnClickListener(v -> {
            // Lógica para cerrar el restaurante (puedes mostrar un dialogo de confirmación)
            Toast.makeText(this, "Restaurante cerrado", Toast.LENGTH_SHORT).show();
        });

        btnCloseRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el diálogo de confirmación
                showConfirmDialog();
            }
        });

    }

    private void fetchRestaurantData(String idRestaurante) {
        db.collection("restaurantes").document(idRestaurante).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Llenar datos del restaurante
                        String restaurantName = documentSnapshot.getString("nombre");
                        String cuisineType = documentSnapshot.getString("eslogan");
                        tvRestaurantName.setText(restaurantName != null ? restaurantName : "Nombre no disponible");
                        tvCuisineType.setText(cuisineType != null ? cuisineType : "Eslogan no disponible");

                    } else {
                        Toast.makeText(this, "Datos del restaurante no encontrados.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener datos del restaurante: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchScheduleData(String idRestaurante) {
        db.collection("horarios").whereEqualTo("idRestaurante", idRestaurante).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Llenar los campos con los datos del horario
                        etLunes.setText(document.getString("Lunes"));
                        etMartes.setText(document.getString("Martes"));
                        etMiercoles.setText(document.getString("Miercoles"));
                        etJueves.setText(document.getString("Jueves"));
                        etViernes.setText(document.getString("Viernes"));
                        etSabado.setText(document.getString("Sabado"));
                        etDomingo.setText(document.getString("Domingo"));
                    } else {
                        Toast.makeText(this, "No se encontraron horarios. Puedes crearlos.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al obtener horarios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveScheduleData() {
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("idRestaurante", idRestaurante);
        scheduleData.put("Lunes", etLunes.getText().toString());
        scheduleData.put("Martes", etMartes.getText().toString());
        scheduleData.put("Miercoles", etMiercoles.getText().toString());
        scheduleData.put("Jueves", etJueves.getText().toString());
        scheduleData.put("Viernes", etViernes.getText().toString());
        scheduleData.put("Sabado", etSabado.getText().toString());
        scheduleData.put("Domingo", etDomingo.getText().toString());

        db.collection("horarios").whereEqualTo("idRestaurante", idRestaurante).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Actualizar documento existente
                        String documentId = querySnapshot.getDocuments().get(0).getId();
                        db.collection("horarios").document(documentId).set(scheduleData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Horarios actualizados exitosamente.", Toast.LENGTH_SHORT).show();
                                    toggleFields(false);
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar horarios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        // Crear nuevo documento
                        db.collection("horarios").add(scheduleData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Horarios guardados exitosamente.", Toast.LENGTH_SHORT).show();
                                    toggleFields(false);
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar horarios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al verificar horarios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void toggleFields(boolean enable) {
        etLunes.setEnabled(enable);
        etMartes.setEnabled(enable);
        etMiercoles.setEnabled(enable);
        etJueves.setEnabled(enable);
        etViernes.setEnabled(enable);
        etSabado.setEnabled(enable);
        etDomingo.setEnabled(enable);
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewRestaurantScheduleActivity.this, R.style.CustomAlertDialog);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_close, null);
        builder.setView(customLayout);

        // Botones dentro del diálogo
        Button btnConfirmar = customLayout.findViewById(R.id.btn_confirmar);
        Button btnCancelar = customLayout.findViewById(R.id.btn_cancelar);

        AlertDialog dialog = builder.create();

        // Acciones para el botón Confirmar
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualizar el campo "open" a false en la base de datos
                if (idRestaurante != null) {
                    db.collection("restaurantes").document(idRestaurante)
                            .update("open", false)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ViewRestaurantScheduleActivity.this, "Restaurante cerrado exitosamente.", Toast.LENGTH_SHORT).show();

                                // Redirigir a la actividad AbrirRestauranteActivity
                                Intent intent = new Intent(ViewRestaurantScheduleActivity.this, AbrirRestauranteActivity.class);
                                intent.putExtra("idRestaurante", idRestaurante);
                                startActivity(intent);

                                // Finalizar la actividad actual
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ViewRestaurantScheduleActivity.this, "Error al cerrar el restaurante: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(ViewRestaurantScheduleActivity.this, "ID del restaurante no encontrado.", Toast.LENGTH_SHORT).show();
                }

                // Cierra el diálogo
                dialog.dismiss();
            }
        });

        // Acciones para el botón Cancelar
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra el diálogo
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        dialog.show();
    }

}

