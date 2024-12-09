package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class AbrirRestauranteActivity extends AppCompatActivity {

    private Button buttonOpen;
    private TextView restaurantName, restauranteSlogan, openRestaurantMessage;
    private ImageView restaurantImage, restaurantPortada;
    private FirebaseFirestore db;
    private RestauranteViewModel restauranteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_abrir_restaurante);

        db = FirebaseFirestore.getInstance();

        // Inicializar ViewModel
        restauranteViewModel = new ViewModelProvider(this).get(RestauranteViewModel.class);

        // UI components
        buttonOpen = findViewById(R.id.open_button);
        restaurantName = findViewById(R.id.restaurant_name);
        restauranteSlogan = findViewById(R.id.cuisine_type);
        restaurantPortada = findViewById(R.id.imageView7);
        restaurantImage = findViewById(R.id.imageView6);

        // Recibir el idRestaurante del intent
        Intent intent = getIntent();
        String idRestaurante = intent.getStringExtra("idRestaurante");

        if (idRestaurante != null) {
            fetchRestaurantData(idRestaurante);
        } else {
            Toast.makeText(this, "No se recibió el ID del restaurante.", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonOpen.setOnClickListener(v -> {
            // Pasar el idRestaurante a InicioRestauranteActivity
            Intent inicioIntent = new Intent(AbrirRestauranteActivity.this, InicioRestauranteActivity.class);
            inicioIntent.putExtra("idRestaurante", idRestaurante); // Pasar el idRestaurante
            startActivity(inicioIntent);
            finish();
        });
    }

    private void fetchRestaurantData(String idRestaurante) {
        // Consultar Firestore para obtener los datos del restaurante
        db.collection("restaurantes").document(idRestaurante)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Actualizar UI con los datos del restaurante
                        String name = documentSnapshot.getString("nombre");
                        String eslogan = documentSnapshot.getString("eslogan");
                        String imageLogo = documentSnapshot.getString("fotoLogo");
                        String imagePortada = documentSnapshot.getString("fotoPortada");

                        restaurantName.setText(name != null ? name : "Nombre no disponible");
                        restauranteSlogan.setText(eslogan != null ? eslogan : "Eslogan no disponible");

                        // Cargar la imagen del restaurante
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
}
