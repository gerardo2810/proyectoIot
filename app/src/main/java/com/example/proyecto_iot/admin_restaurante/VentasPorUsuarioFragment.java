package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Usuario;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.UsuarioAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private FirebaseFirestore db;
    private RestauranteViewModel restauranteViewModel;
    private String idRestauranteActual;
    private boolean restauranteIdCargado = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm:ss a", new Locale("es","ES"));

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public VentasPorUsuarioFragment() {
        // Required empty public constructor
    }

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

        db = FirebaseFirestore.getInstance();
        restauranteViewModel = new ViewModelProvider(requireActivity()).get(RestauranteViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ventas_por_usuario, container, false);

        recyclerUser = view.findViewById(R.id.recycler_user);
        listaUser = new ArrayList<>();

        usuarioAdapter = new UsuarioAdapter(listaUser, getContext());
        recyclerUser.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUser.setAdapter(usuarioAdapter);

        spinnerMeses = view.findViewById(R.id.spinnerRol);
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, meses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeses.setAdapter(adapter);

        // Spinner inicia en el mes actual
        Calendar calendario = Calendar.getInstance();
        int mesActual = calendario.get(Calendar.MONTH);
        spinnerMeses.setSelection(mesActual);

        // Observar el idRestaurante
        restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
            idRestauranteActual = idRestaurante;
            restauranteIdCargado = true;
        });

        spinnerMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                if (restauranteIdCargado) {
                    int mesSeleccionado = position + 1; // Enero=1
                    cargarClientesMasFrecuentes(mesSeleccionado);
                } else {
                    Toast.makeText(getContext(), "Cargando ID de restaurante, espere...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Sin acción
            }
        });

        return view;
    }

    private void cargarClientesMasFrecuentes(int mesSeleccionado) {
        listaUser.clear();
        usuarioAdapter.notifyDataSetChanged();

        db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestauranteActual)
                .whereEqualTo("estado", 8) // Solo pedidos completados
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Integer> cantidadPorCliente = new HashMap<>();
                    Map<String, Double> totalGastadoPorCliente = new HashMap<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String fechaStr = doc.getString("fechaHora");
                        if (fechaStr == null) continue;

                        // Ajustar formato AM/PM si necesario
                        fechaStr = fechaStr.replace("p. m.", "PM");
                        fechaStr = fechaStr.replace("a. m.", "AM");

                        Date fechaDate;
                        try {
                            fechaDate = sdf.parse(fechaStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            continue;
                        }

                        if (fechaDate != null) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(fechaDate);
                            int mesPedido = cal.get(Calendar.MONTH) + 1;
                            if (mesPedido == mesSeleccionado) {
                                // Contar pedidos y sumar pagoTotal
                                String idCliente = doc.getString("idCliente");
                                double pagoTotal = doc.getDouble("pagoTotal") != null ? doc.getDouble("pagoTotal") : 0.0;

                                if (idCliente != null && !idCliente.isEmpty()) {
                                    int cantidadActual = cantidadPorCliente.getOrDefault(idCliente, 0);
                                    cantidadPorCliente.put(idCliente, cantidadActual + 1);

                                    double totalActual = totalGastadoPorCliente.getOrDefault(idCliente, 0.0);
                                    totalGastadoPorCliente.put(idCliente, totalActual + pagoTotal);
                                }
                            }
                        }
                    }

                    // Ordenar por cantidad desc
                    List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(cantidadPorCliente.entrySet());
                    listaOrdenada.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

                    // Top 10
                    List<Map.Entry<String, Integer>> top10 = listaOrdenada.size() > 10 ? listaOrdenada.subList(0, 10) : listaOrdenada;

                    for (Map.Entry<String, Integer> entry : top10) {
                        String idCliente = entry.getKey();
                        int cantPedidos = entry.getValue();
                        double totalGastado = totalGastadoPorCliente.getOrDefault(idCliente, 0.0);

                        // Crear objeto Usuario
                        Usuario u = new Usuario(idCliente, "", "", "", "",
                                "Pedidos: " + cantPedidos, "Total: $" + String.format("%.2f", totalGastado));
                        listaUser.add(u);
                    }

                    usuarioAdapter.notifyDataSetChanged();

                    if (listaUser.isEmpty()) {
                        Toast.makeText(getContext(), "No se encontraron clientes para el mes seleccionado.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al cargar datos.", Toast.LENGTH_SHORT).show();
                });
    }
}
