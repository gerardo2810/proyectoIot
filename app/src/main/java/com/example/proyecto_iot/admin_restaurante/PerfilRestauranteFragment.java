package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilRestauranteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilRestauranteFragment extends Fragment {

    private RestauranteViewModel restauranteViewModel;
    private TextView restaurantNameTextView;
    private TextView cuisineTypeTextView;
    private FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilRestauranteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilRestauranteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilRestauranteFragment newInstance(String param1, String param2) {
        PerfilRestauranteFragment fragment = new PerfilRestauranteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_restaurante, container, false);

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializa vistas
        restaurantNameTextView = view.findViewById(R.id.restaurant_name);
        cuisineTypeTextView = view.findViewById(R.id.cuisine_type);

        // Obtén el ViewModel compartido
        restauranteViewModel = new ViewModelProvider(requireActivity()).get(RestauranteViewModel.class);

        // Observa los cambios en el idRestaurante
        restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
            if (idRestaurante != null) {
                fetchRestaurantData(idRestaurante);
            }
        });

        // Enlazar los botones para editar datos personales y del restaurante
        LinearLayout personalInfoLayout = view.findViewById(R.id.edit_personal_info);
        LinearLayout restaurantInfoLayout = view.findViewById(R.id.edit_restaurant_info);
        LinearLayout scheduleLayout = view.findViewById(R.id.view_schedule);

        personalInfoLayout.setOnClickListener(v -> {
            restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
                if (idRestaurante != null) {
                    Intent intent = new Intent(getContext(), EditPersonalInfoActivity.class);
                    intent.putExtra("idRestaurante", idRestaurante);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        restaurantInfoLayout.setOnClickListener(v -> {
            restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
                if (idRestaurante != null) {
                    Intent intent = new Intent(getContext(), EditRestaurantInfoActivity.class);
                    intent.putExtra("idRestaurante", idRestaurante);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        scheduleLayout.setOnClickListener(v -> {
            restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
                if (idRestaurante != null) {
                    Intent intent = new Intent(getContext(), ViewRestaurantScheduleActivity.class);
                    intent.putExtra("idRestaurante", idRestaurante);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Listener para cerrar sesión
        LinearLayout logoutLayout = view.findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Cierra la sesión
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish(); // Finaliza la actividad actual
        });


        return view;
    }

    private void fetchRestaurantData(String idRestaurante) {
        db.collection("restaurantes").document(idRestaurante)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Recuperar los datos del documento
                        String restaurantName = documentSnapshot.getString("nombre");
                        String slogan = documentSnapshot.getString("eslogan");

                        // Actualizar la UI
                        restaurantNameTextView.setText(restaurantName != null ? restaurantName : "Nombre no disponible");
                        cuisineTypeTextView.setText(slogan != null ? slogan : "Eslogan no disponible");
                    } else {
                        Toast.makeText(getContext(), "Datos del restaurante no encontrados.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}