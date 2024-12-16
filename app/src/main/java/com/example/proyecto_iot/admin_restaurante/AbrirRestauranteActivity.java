package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AbrirRestauranteActivity extends AppCompatActivity {

    private Button buttonOpen;
    private TextView restaurantName, restauranteSlogan, openRestaurantMessage;
    private ImageView restaurantImage, restaurantPortada;
    private FirebaseFirestore db;
    private RestauranteViewModel restauranteViewModel;
    private String idRestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_abrir_restaurante);

        db = FirebaseFirestore.getInstance();

        restauranteViewModel = new ViewModelProvider(this).get(RestauranteViewModel.class);

        buttonOpen = findViewById(R.id.open_button);
        restaurantName = findViewById(R.id.restaurant_name);
        restauranteSlogan = findViewById(R.id.cuisine_type);
        restaurantPortada = findViewById(R.id.imageView7);
        restaurantImage = findViewById(R.id.imageView6);

        // Recibir el idRestaurante del intent
        Intent intent = getIntent();
        idRestaurante = intent.getStringExtra("idRestaurante");

        Log.d("AbrirRestauranteActivity", "ID del restaurante: " + idRestaurante);

        if (idRestaurante == null || idRestaurante.isEmpty()) {
            Toast.makeText(this, "No se recibió el ID del restaurante.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchRestaurantData(idRestaurante);

        buttonOpen.setOnClickListener(v -> showConfirmDialog());
    }

    private void fetchRestaurantData(String idRestaurante) {
        db.collection("restaurantes").document(idRestaurante)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("nombre");
                        String eslogan = documentSnapshot.getString("eslogan");
                        String imageLogo = documentSnapshot.getString("fotoLogo");
                        String imagePortada = documentSnapshot.getString("fotoPortada");

                        restaurantName.setText(name != null ? name : "Nombre no disponible");
                        restauranteSlogan.setText(eslogan != null ? eslogan : "Eslogan no disponible");

                        if (imageLogo != null && !imageLogo.isEmpty()) {
                            Glide.with(this)
                                    .load(imageLogo)
                                    .placeholder(R.drawable.placeholder)
                                    .into(restaurantImage);
                        }

                        if (imagePortada != null && !imagePortada.isEmpty()) {
                            Glide.with(this)
                                    .load(imagePortada)
                                    .placeholder(R.drawable.placeholder)
                                    .into(restaurantPortada);
                        }
                    } else {
                        Toast.makeText(this, "No se encontraron datos para este restaurante.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener los datos del restaurante: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AbrirRestauranteActivity.this, R.style.CustomAlertDialog);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_open, null);
        builder.setView(customLayout);

        Button btnConfirmar = customLayout.findViewById(R.id.btn_confirmar);
        Button btnCancelar = customLayout.findViewById(R.id.btn_cancelar);

        AlertDialog dialog = builder.create();

        btnConfirmar.setOnClickListener(v -> {
            if (idRestaurante != null && !idRestaurante.isEmpty()) {
                db.collection("restaurantes").document(idRestaurante)
                        .update("open", true)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AbrirRestauranteActivity.this, "Restaurante abierto exitosamente.", Toast.LENGTH_SHORT).show();

                            guardarLog("El administrador acaba de abrir su restaurante", "Administrador");

                            Intent inicioIntent = new Intent(AbrirRestauranteActivity.this, InicioRestauranteActivity.class);
                            inicioIntent.putExtra("idRestaurante", idRestaurante);
                            startActivity(inicioIntent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AbrirRestauranteActivity.this, "Error al abrir el restaurante: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(AbrirRestauranteActivity.this, "ID del restaurante no es válido.", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void guardarLog(String mensaje, String rol) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener el UID del usuario logueado
        String usuarioUID = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "Usuario desconocido";

        // Obtener fecha y hora actuales
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String horaActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Crear un mapa para guardar el log
        HashMap<String, Object> logData = new HashMap<>();
        logData.put("mensaje", mensaje);
        logData.put("usuarioUID", usuarioUID);
        logData.put("rol", rol);
        logData.put("fecha", fechaActual);
        logData.put("hora", horaActual);

        // Guardar el log en Firestore
        db.collection("logs")
                .add(logData)
                .addOnSuccessListener(documentReference -> {
                    // Éxito al guardar el log
                    System.out.println("Log guardado con éxito. ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Error al guardar el log
                    System.err.println("Error al guardar el log: " + e.getMessage());
                });
    }

}
