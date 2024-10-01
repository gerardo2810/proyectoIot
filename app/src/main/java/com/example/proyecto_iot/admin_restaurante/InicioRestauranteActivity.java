package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class InicioRestauranteActivity extends AppCompatActivity {
    // Declaración de los fragments
    private final HomeRestauranteFragment homeRestauranteFragment = new HomeRestauranteFragment();
    private final OrdenesRestauranteFragment ordenesRestauranteFragment = new OrdenesRestauranteFragment();
    private final CartaRestauranteFragment cartaRestauranteFragment = new CartaRestauranteFragment();
    private final ReportesRestauranteFragment reportesRestauranteFragment = new ReportesRestauranteFragment();
    private final PerfilRestauranteFragment perfilRestauranteFragment = new PerfilRestauranteFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_inicio);

        // Inicializa el BottomNavigationView
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        // Usa setOnNavigationItemSelectedListener en lugar de setOnNavigationItemReselectedListener
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;

                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new PorAceptarFragment();
                        break;
                    case 1:
                        selectedFragment = new EnPreparacionFragment();
                        break;
                    case 2:
                        selectedFragment = new PorEntregarFragment();
                        break;
                }

                if (selectedFragment != null) {
                    replaceFragment(selectedFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });


    }

    // Listener para manejar las selecciones en el menú de navegación
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.navigation_home) {
                        loadFragment(homeRestauranteFragment);
                        return true;
                    } else if (itemId == R.id.navigation_ordenes) {
                        loadFragment(ordenesRestauranteFragment);
                        return true;
                    } else if (itemId == R.id.navigation_carta) {
                        loadFragment(cartaRestauranteFragment);
                        return true;
                    } else if (itemId == R.id.navigation_reportes) {
                        loadFragment(reportesRestauranteFragment);
                        return true;
                    } else if (itemId == R.id.navigation_perfil) {
                        loadFragment(perfilRestauranteFragment);
                        return true;
                    }
                    return false;
                }
            };

    // Método para cargar el fragmento seleccionado
    public void loadFragment(Fragment fragment) {
        // Reemplaza el fragmento en el contenedor y asegura que no se sobrepongan
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}

