package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Plato;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PlatoAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Usuario;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.UsuarioAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VentasPorUsuarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VentasPorUsuarioFragment extends Fragment {

    private RecyclerView recyclerUser;
    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> listaUser;
    private Spinner spinnerMeses;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VentasPorUsuarioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VentasPorUsuarioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VentasPorUsuarioFragment newInstance(String param1, String param2) {
        VentasPorUsuarioFragment fragment = new VentasPorUsuarioFragment();
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
        View view = inflater.inflate(R.layout.fragment_ventas_por_usuario, container, false);

        // Inicializar RecyclerView
        recyclerUser = view.findViewById(R.id.recycler_user);
        listaUser = new ArrayList<>();

        // Inicializar Spinner
        spinnerMeses = view.findViewById(R.id.spinnerRol);

        // Lista de meses
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        // Configurar el adaptador del Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, meses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeses.setAdapter(adapter);

        // Listener para cuando se seleccione un mes
        spinnerMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el mes seleccionado
                String mesSeleccionado = parent.getItemAtPosition(position).toString();
                // Realizar alguna acción con el mes seleccionado
                Toast.makeText(getContext(), "Mes seleccionado: " + mesSeleccionado, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Acciones cuando no se selecciona nada (opcional)
            }
        });


        // Agregar platos a la lista
        listaUser.add(new Usuario("Ana Armas Antuna", "29", "74859656", "anaarmas@gmail.com", "987654321", "10","S/280.00"));
        listaUser.add(new Usuario("Jose Hernandez", "30", "45658578", "johernandez@gmail.com", "962587845", "9","S/240.00"));
        listaUser.add(new Usuario("Maria Jose Ramirez", "45", "22485968", "majoramirez@gmail.com", "951236587", "8","S/180.00"));


        // Configurar el adaptador y el RecyclerView
        usuarioAdapter = new UsuarioAdapter(listaUser, getContext());
        recyclerUser.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUser.setAdapter(usuarioAdapter);

        return view;
    }
}