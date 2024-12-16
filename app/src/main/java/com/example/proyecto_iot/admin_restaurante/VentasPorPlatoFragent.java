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
 * Use the {@link VentasPorPlatoFragent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VentasPorPlatoFragent extends Fragment {

    private RecyclerView recyclerPlatos;
    private PlatoAdapter platoAdapter;
    private List<Plato> listaPlatos;
    private Spinner spinnerMeses;

    private FirebaseFirestore db;
    private RestauranteViewModel restauranteViewModel;
    private String idRestauranteActual; // Guardaremos el id aquí cuando lo obtengamos
    private boolean restauranteIdCargado = false;

    // Formato de fecha (ajustar según el formato real)
    private SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm:ss a", new Locale("es", "ES"));

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public VentasPorPlatoFragent() {
        // Required empty public constructor
    }

    public static VentasPorPlatoFragent newInstance(String param1, String param2) {
        VentasPorPlatoFragent fragment = new VentasPorPlatoFragent();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ventas_por_plato_fragent, container, false);

        // Inicializar RecyclerView
        recyclerPlatos = view.findViewById(R.id.recycler_platos);
        listaPlatos = new ArrayList<>();

        // Inicializar el adapter vacío al principio
        platoAdapter = new PlatoAdapter(listaPlatos, getContext());
        recyclerPlatos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPlatos.setAdapter(platoAdapter);

        // Inicializar Spinner
        spinnerMeses = view.findViewById(R.id.spinnerRol);

        // Lista de meses
        final String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        // Configurar el adaptador del Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, meses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeses.setAdapter(adapter);

        // Establecer el spinner en el mes actual del sistema
        Calendar calendar = Calendar.getInstance();
        int mesActual = calendar.get(Calendar.MONTH); // Enero=0, Febrero=1...
        spinnerMeses.setSelection(mesActual); // Setear el mes actual en el spinner

        // Observamos el idRestaurante del ViewModel
        restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
            idRestauranteActual = idRestaurante;
            restauranteIdCargado = true;
        });

        // Listener para cuando se seleccione un mes
        spinnerMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (restauranteIdCargado) {
                    int mesNumero = position + 1; // Enero=1, Febrero=2, etc.
                    cargarPlatosMasVendidos(mesNumero);
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

    private void cargarPlatosMasVendidos(int mesSeleccionado) {
        // Limpia la lista actual
        listaPlatos.clear();
        platoAdapter.notifyDataSetChanged();

        // Consulta a la colección "pedidos" del restaurante actual
        db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestauranteActual)
                .whereEqualTo("estado", 8) // Validación: solo pedidos con estado = 8
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Map para acumular cantidad total vendida por producto
                    Map<String, Integer> acumuladoPorProducto = new HashMap<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String fechaStr = doc.getString("fechaHora");
                        if (fechaStr == null) continue;

                        // Reemplazo AM/PM si es necesario (ajusta según tu formato real)
                        fechaStr = fechaStr.replace("p. m.", "PM");
                        fechaStr = fechaStr.replace("a. m.", "AM");

                        Date fechaDate = null;
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
                                // Extraer los productos de este pedido
                                List<Map<String, Object>> productos = (List<Map<String, Object>>) doc.get("productos");
                                if (productos != null) {
                                    for (Map<String, Object> prod : productos) {
                                        String idProducto = (String) prod.get("id");
                                        Long cantidadLong = prod.get("cantidad") instanceof Long ? (Long) prod.get("cantidad") : null;

                                        if (idProducto != null && cantidadLong != null) {
                                            int cantidad = cantidadLong.intValue();
                                            int cantidadActual = acumuladoPorProducto.getOrDefault(idProducto, 0);
                                            acumuladoPorProducto.put(idProducto, cantidadActual + cantidad);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Ahora ordenamos los productos por la cantidad vendida (descendente)
                    List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(acumuladoPorProducto.entrySet());
                    listaOrdenada.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

                    // Tomar top 10
                    List<Map.Entry<String, Integer>> top10 = listaOrdenada.size() > 10 ? listaOrdenada.subList(0, 10) : listaOrdenada;

                    for (Map.Entry<String, Integer> entry : top10) {
                        String idProducto = entry.getKey();
                        int cantidadVendida = entry.getValue();
                        Plato p = new Plato(idProducto, cantidadVendida + " unidades");
                        listaPlatos.add(p);
                    }

                    platoAdapter.notifyDataSetChanged();

                    if (listaPlatos.isEmpty()) {
                        Toast.makeText(getContext(), "No se encontraron resultados para el mes seleccionado.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al cargar datos.", Toast.LENGTH_SHORT).show();
                });
    }

}

