package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeRestauranteFragment extends Fragment {
    private RestauranteViewModel restauranteViewModel;
    private TextView restaurantNameTextView, cuisineTypeTextView, closedMessage;
    private Button statusButton;
    private TabLayout tabLayout;
    private FirebaseFirestore db;
    private String restaurantId; // ID del restaurante
    private boolean isRestaurantOpen = true; // Estado inicial (puede actualizarse desde Firebase)
    private Fragment selectedFragment;
    private int lastSelectedTab = -1; // Guardar la última pestaña seleccionada para evitar recargas innecesarias

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeRestauranteFragment", "Fragment creado en onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HomeRestauranteFragment", "Fragment inflado en onCreateView");
        View view = inflater.inflate(R.layout.fragment_home_restaurante, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        restaurantNameTextView = view.findViewById(R.id.restaurant_name);
        cuisineTypeTextView = view.findViewById(R.id.cuisine_type);
        statusButton = view.findViewById(R.id.status_button);
        closedMessage = view.findViewById(R.id.closed_message);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Configurar ViewModel para obtener el ID del restaurante
        restauranteViewModel = new ViewModelProvider(requireActivity()).get(RestauranteViewModel.class);

        // Observamos cambios en el ID del restaurante
        restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
            if (idRestaurante != null) {
                Log.d("HomeRestauranteFragment", "ID del restaurante recibido: " + idRestaurante);
                restaurantId = idRestaurante;

                // Actualizamos los datos del restaurante y la UI
                fetchRestaurantData(idRestaurante);
            } else {
                Log.e("HomeRestauranteFragment", "ID del restaurante no recibido");
            }
        });

        // Configurar el listener del botón
        statusButton.setOnClickListener(v -> showCloseConfirmationDialog());

        // Configurar el TabLayout para manejar los fragmentos
        setupTabLayout();

        return view;
    }

    private void fetchRestaurantData(String idRestaurante) {
        Log.d("HomeRestauranteFragment", "Obteniendo datos del restaurante con ID: " + idRestaurante);
        db.collection("restaurantes").document(idRestaurante)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String restaurantName = documentSnapshot.getString("nombre");
                        String slogan = documentSnapshot.getString("eslogan");
                        isRestaurantOpen = documentSnapshot.getBoolean("open"); // Obtener estado actual

                        Log.d("HomeRestauranteFragment", "Datos obtenidos: nombre=" + restaurantName + ", eslogan=" + slogan);

                        // Actualizar la UI
                        restaurantNameTextView.setText(restaurantName != null ? restaurantName : "Nombre no disponible");
                        cuisineTypeTextView.setText(slogan != null ? slogan : "Eslogan no disponible");
                        updateUI(true); // Actualizar elementos visuales según el estado, forzar carga inicial
                    } else {
                        Log.e("HomeRestauranteFragment", "No se encontraron datos para el restaurante.");
                        Toast.makeText(getContext(), "Datos del restaurante no encontrados.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeRestauranteFragment", "Error al obtener datos: " + e.getMessage());
                    Toast.makeText(getContext(), "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI(boolean forceLoadInitial) {
        if (restaurantId == null) {
            Log.e("HomeRestauranteFragment", "updateUI: restaurantId es nulo. No se puede actualizar la UI.");
            return;
        }

        if (isRestaurantOpen) {
            Log.d("HomeRestauranteFragment", "Restaurante está abierto. Mostrando pestañas y fragmentos.");
            statusButton.setText("RESTAURANTE ABIERTO");
            statusButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green));
            closedMessage.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);

            // Cargar la pestaña activa o la predeterminada (PorAceptarFragment) si aún no se ha cargado o si es la inicialización forzada
            if (forceLoadInitial || selectedFragment == null || !(selectedFragment instanceof PorAceptarFragment)) {
                lastSelectedTab = 0; // Establecer la primera pestaña como seleccionada
                selectedFragment = new PorAceptarFragment();
                loadFragment(selectedFragment);
            }
        } else {
            Log.d("HomeRestauranteFragment", "Restaurante está cerrado. Ocultando pestañas y mostrando mensaje.");
            statusButton.setText("RESTAURANTE CERRADO");
            statusButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_dark));
            closedMessage.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);

            // Eliminar cualquier fragmento cargado
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_tab_container, new Fragment()) // Fragmento vacío
                    .commit();
        }
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isRestaurantOpen) {
                    Log.d("HomeRestauranteFragment", "Restaurante cerrado. No se cargarán pestañas.");
                    return;
                }

                int currentPosition = tab.getPosition();
                if (lastSelectedTab == currentPosition) {
                    // Evitar recargar el fragmento si ya está seleccionado
                    Log.d("HomeRestauranteFragment", "Evitar recargar el fragmento ya seleccionado: " + currentPosition);
                    return;
                }

                Log.d("HomeRestauranteFragment", "Pestaña seleccionada: " + currentPosition);
                lastSelectedTab = currentPosition;

                switch (currentPosition) {
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
                    loadFragment(selectedFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("HomeRestauranteFragment", "Pestaña deseleccionada: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("HomeRestauranteFragment", "Pestaña reseleccionada: " + tab.getPosition());
                onTabSelected(tab);
            }
        });

        // Seleccionar la primera pestaña por defecto si el restaurante está abierto
        if (isRestaurantOpen) {
            tabLayout.selectTab(tabLayout.getTabAt(0));
        }
    }

    private void loadFragment(Fragment fragment) {
        if (restaurantId == null) {
            Log.e("HomeRestauranteFragment", "loadFragment: restaurantId es nulo. No se puede cargar el fragmento.");
            return;
        }

        // Pasar el restaurantId al fragmento
        Bundle args = new Bundle();
        args.putString("idRestaurante", restaurantId);
        fragment.setArguments(args);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_tab_container, fragment)
                .commit();
    }

    private void showCloseConfirmationDialog() {
        String message = isRestaurantOpen ? "¿Estás seguro de que deseas cerrar el restaurante?" : "¿Estás seguro de que deseas abrir el restaurante?";
        String action = isRestaurantOpen ? "Cerrar" : "Abrir";

        new AlertDialog.Builder(requireContext())
                .setTitle(action + " restaurante")
                .setMessage(message)
                .setPositiveButton("Aceptar", (dialog, which) -> toggleRestaurantStatus())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void toggleRestaurantStatus() {
        if (restaurantId == null) {
            Log.e("HomeRestauranteFragment", "ID del restaurante no disponible al intentar cambiar el estado.");
            Toast.makeText(getContext(), "ID del restaurante no disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        isRestaurantOpen = !isRestaurantOpen; // Cambiar el estado local
        Log.d("HomeRestauranteFragment", "Actualizando estado del restaurante a: " + (isRestaurantOpen ? "Abierto" : "Cerrado"));

        // Actualizar el campo "open" en Firebase
        db.collection("restaurantes").document(restaurantId)
                .update("open", isRestaurantOpen)
                .addOnSuccessListener(aVoid -> {
                    Log.d("HomeRestauranteFragment", "Estado del restaurante actualizado correctamente.");
                    Toast.makeText(getContext(), isRestaurantOpen ? "Restaurante abierto." : "Restaurante cerrado.", Toast.LENGTH_SHORT).show();
                    updateUI(true); // Actualizar la interfaz visual y cargar nuevamente el fragmento inicial si es necesario
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeRestauranteFragment", "Error al actualizar el estado: " + e.getMessage());
                    Toast.makeText(getContext(), "Error al actualizar el estado.", Toast.LENGTH_SHORT).show();
                });
    }
}
