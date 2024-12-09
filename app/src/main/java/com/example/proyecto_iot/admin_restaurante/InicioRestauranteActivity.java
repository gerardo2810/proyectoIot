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

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

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
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                }
            };

    // Método para cargar los fragments del BottomNavigationView
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_navigation_container, fragment)  // Contenedor principal de BottomNavigationView
                .commit();
    }
}
