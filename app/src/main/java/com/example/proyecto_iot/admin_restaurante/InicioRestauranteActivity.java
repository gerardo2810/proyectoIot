package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InicioRestauranteActivity extends AppCompatActivity {
    private final HomeRestauranteFragment homeRestauranteFragment = new HomeRestauranteFragment();
    private final OrdenesRestauranteFragment ordenesRestauranteFragment = new OrdenesRestauranteFragment();
    private final CartaRestauranteFragment cartaRestauranteFragment = new CartaRestauranteFragment();
    private final ReportesRestauranteFragment reportesRestauranteFragment = new ReportesRestauranteFragment();
    private final PerfilRestauranteFragment perfilRestauranteFragment = new PerfilRestauranteFragment();

    private String idRestaurante; // Variable para almacenar el ID del restaurante
    private RestauranteViewModel restauranteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_inicio);

        verificarEstadoUsuario();

        // Inicializar ViewModel
        restauranteViewModel = new ViewModelProvider(this).get(RestauranteViewModel.class);

        // Recibir el idRestaurante del intent
        Intent intent = getIntent();
        idRestaurante = intent.getStringExtra("idRestaurante");

        if (idRestaurante != null) {
            restauranteViewModel.setIdRestaurante(idRestaurante);
        } else {
            Toast.makeText(this, "Error: no se recibió el ID del restaurante", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Inicializa el BottomNavigationView
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);

        // Listener para manejar las selecciones en el BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Cargar fragmento inicial cuando la actividad se crea (home por defecto)
        if (savedInstanceState == null) {
            loadFragment(homeRestauranteFragment);  // Home inicial por defecto
        }
    }

    // Listener para manejar las selecciones en el menú de navegación
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            item -> {
                Fragment selectedFragment = null;

                Bundle bundle = new Bundle();
                bundle.putString("idRestaurante", idRestaurante);

                if (item.getItemId() == R.id.navigation_home) {
                    homeRestauranteFragment.setArguments(bundle);
                    selectedFragment = homeRestauranteFragment;
                } else if (item.getItemId() == R.id.navigation_ordenes) {
                    ordenesRestauranteFragment.setArguments(bundle);
                    selectedFragment = ordenesRestauranteFragment;
                } else if (item.getItemId() == R.id.navigation_carta) {
                    cartaRestauranteFragment.setArguments(bundle);
                    selectedFragment = cartaRestauranteFragment;
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    reportesRestauranteFragment.setArguments(bundle);
                    selectedFragment = reportesRestauranteFragment;
                } else if (item.getItemId() == R.id.navigation_perfil) {
                    perfilRestauranteFragment.setArguments(bundle);
                    selectedFragment = perfilRestauranteFragment;
                }

                // Cargar el fragmento seleccionado
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }

                return true;
            };

    // Método para cargar los fragments del BottomNavigationView
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_navigation_container, fragment)  // Contenedor principal de BottomNavigationView
                .commit();
    }

    private void verificarEstadoUsuario() {
        // Obtener instancia de FirebaseAuth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser usuarioActual = firebaseAuth.getCurrentUser();

        if (usuarioActual != null) {
            String uid = usuarioActual.getUid();

            // Referencia a Firestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Buscar el documento del usuario por UID
            firestore.collection("administradores").document(uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Boolean habilitado = document.getBoolean("habilitado");
                                if (!habilitado) {
                                    // Desloguear al usuario y redirigir al LoginActivity
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    Toast.makeText(this, "Su cuenta está inhabilitada. Contáctese con soporte.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(this, "El documento no existe.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Error al verificar el estado del usuario.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Si no hay un usuario logueado, redirigir al LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

}

