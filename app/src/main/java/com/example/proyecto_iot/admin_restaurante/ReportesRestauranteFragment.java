package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Plato;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PlatoAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportesRestauranteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportesRestauranteFragment extends Fragment {

    private RestauranteViewModel restauranteViewModel;
    private FirebaseFirestore db;

    private Button btnVentasPorPlato, btnVentasPorUsuario;
    private TabLayout tabLayout;
    private Fragment selectedFragment;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportesRestauranteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportesRestauranteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportesRestauranteFragment newInstance(String param1, String param2) {
        ReportesRestauranteFragment fragment = new ReportesRestauranteFragment();
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
        View view = inflater.inflate(R.layout.fragment_reportes_restaurante, container, false);

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        // Obtén el ViewModel compartido
        restauranteViewModel = new ViewModelProvider(requireActivity()).get(RestauranteViewModel.class);

        // Inicializa el TabLayout
        tabLayout = view.findViewById(R.id.tabLayout);

        // Listener para manejar las selecciones de pestañas
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Cambia el fragmento en el TabLayout basado en la pestaña seleccionada
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new VentasPorPlatoFragent();
                        break;
                    case 1:
                        selectedFragment = new VentasPorUsuarioFragment();
                        break;
                }

                // Reemplazar el fragmento del TabLayout
                if (selectedFragment != null) {
                    replaceTabFragment(selectedFragment);
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

        // Cargar el fragmento inicial para el TabLayout (PorAceptarFragment)
        if (savedInstanceState == null) {
            replaceTabFragment(new VentasPorPlatoFragent());
        }

        return view;
    }

    // Método para reemplazar los fragments dentro del TabLayout
    private void replaceTabFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)  // Asegúrate de usar un contenedor de fragmentos dentro del fragmento
                .commit();
    }
}