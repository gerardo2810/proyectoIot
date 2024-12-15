package com.example.proyecto_iot.admin_restaurante;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
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
 * Use the {@link ReportesRestauranteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportesRestauranteFragment extends Fragment {

    private RestauranteViewModel restauranteViewModel;
    private FirebaseFirestore db;

    private TabLayout tabLayout;
    private Fragment selectedFragment;
    private BarChart barChart;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ReportesRestauranteFragment() {
        // Required empty public constructor
    }

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes_restaurante, container, false);

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        // Obtén el ViewModel compartido
        restauranteViewModel = new ViewModelProvider(requireActivity()).get(RestauranteViewModel.class);

        // Referencia al BarChart
        barChart = view.findViewById(R.id.barChart);

        // Inicializa el TabLayout
        tabLayout = view.findViewById(R.id.tabLayout);

        // Listener para las Tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new VentasPorPlatoFragent();
                        break;
                    case 1:
                        selectedFragment = new VentasPorUsuarioFragment();
                        break;
                }

                if (selectedFragment != null) {
                    replaceTabFragment(selectedFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Sin acción
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Sin acción
            }
        });

        // Fragment inicial
        if (savedInstanceState == null) {
            replaceTabFragment(new VentasPorPlatoFragent());
        }

        // Cargar datos del gráfico
        cargarDatosGrafico();

        return view;
    }

    private void replaceTabFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void cargarDatosGrafico() {
        // Observamos el idRestaurante del ViewModel
        restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
            db.collection("pedidos")
                    .whereEqualTo("idRestaurante", idRestaurante)
                    .whereEqualTo("estado", 4)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Map<Integer, Float> ventasPorMes = new HashMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Object fechaObj = doc.get("fechaHora");
                            Date fechaDate = null;

                            if (fechaObj instanceof com.google.firebase.Timestamp) {
                                // Si por alguna razón fuera timestamp
                                com.google.firebase.Timestamp ts = (com.google.firebase.Timestamp) fechaObj;
                                fechaDate = ts.toDate();
                            } else if (fechaObj instanceof String) {
                                // Parsear desde String
                                String fechaStr = (String) fechaObj;

                                // Ajustar reemplazos según el formato exacto almacenado
                                // Suponiendo que la fecha tiene "p. m." o "a. m."
                                fechaStr = fechaStr.replace("p. m.", "PM");
                                fechaStr = fechaStr.replace("a. m.", "AM");

                                // Ajustar el patrón del formato:
                                // Por ejemplo: "15 de diciembre de 2024, 15:36:53 p. m."
                                // Formato: dd 'de' MMMM 'de' yyyy, HH:mm:ss a
                                SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm:ss a", new Locale("es", "ES"));
                                try {
                                    fechaDate = sdf.parse(fechaStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            Double pagoTotal = doc.getDouble("pagoTotal");

                            if (fechaDate != null && pagoTotal != null) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(fechaDate);
                                int mes = cal.get(Calendar.MONTH) + 1; // 0-based, se suma 1

                                float montoActual = ventasPorMes.getOrDefault(mes, 0f);
                                ventasPorMes.put(mes, montoActual + pagoTotal.floatValue());
                            }
                        }

                        mostrarGrafico(ventasPorMes);
                    })
                    .addOnFailureListener(e -> {
                        // Manejo de error
                        e.printStackTrace();
                    });
        });
    }

    private void mostrarGrafico(Map<Integer, Float> ventasPorMes) {
        // Se crean las entradas del gráfico. Ejemplo: 12 barras (enero a diciembre)
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            float valor = ventasPorMes.getOrDefault(i, 0f);
            entries.add(new BarEntry(i, valor));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Ventas por Mes");
        dataSet.setColor(Color.MAGENTA);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);

        // Eje X con nombres de meses
        String[] meses = new String[]{"Jan", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(meses));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.invalidate(); // refrescar gráfico
    }
}
