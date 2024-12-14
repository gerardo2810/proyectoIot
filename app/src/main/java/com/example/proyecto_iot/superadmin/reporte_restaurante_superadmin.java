package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.PedidoSA;
import com.example.proyecto_iot.superadmin.RecyclerView.ProductoResumen;
import com.example.proyecto_iot.superadmin.RecyclerView.ProductoSA;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class reporte_restaurante_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private BarChart barChart;
    private TextView tvVentasTotales, tvTotalPedidos;
    private Spinner spinnerMes;

    private LinearLayout linearLayoutContainer;
    private String restauranteUID;
    private ArrayList<PedidoSA> pedidosFiltrados = new ArrayList<>();
    private Map<String, ProductoResumen> resumenProductos = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_reporte_restaurante);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener referencia a vistas
        barChart = findViewById(R.id.barChartVentas);
        tvVentasTotales = findViewById(R.id.tvVentasTotales);
        tvTotalPedidos = findViewById(R.id.tvTotalPedidos);
        spinnerMes = findViewById(R.id.spinnerMes);
        linearLayoutContainer = findViewById(R.id.linearLayoutContainer);

        restauranteUID = getIntent().getStringExtra("restauranteUID");

        configurarSpinnerMes();

        obtenerPedidos();

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, gestion_reportes_superadmin.class);
            intent.putExtra("SELECTED_ITEM_ID", R.id.navigation_reportes);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_usuarios);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_usuarios) {
                    intent = new Intent(reporte_restaurante_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(reporte_restaurante_superadmin.this, gestion_reportes_superadmin.class);
                }
                if (intent != null) {
                    intent.putExtra("SELECTED_ITEM_ID", item.getItemId());
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        //----------------------------------------------------------------------------

    }

    private void configurarSpinnerMes() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meses_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapter);

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filtrarPedidosPorMes(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se seleccionó nada
            }
        });
    }

    private void obtenerPedidos() {
        CollectionReference pedidosRef = db.collection("pedidos");

        pedidosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                pedidosFiltrados.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    PedidoSA pedido = document.toObject(PedidoSA.class);

                    // Filtrar por restaurante y estado
                    if (pedido.getIdRestaurante().equals(restauranteUID) && pedido.getEstado() == 4) {
                        pedidosFiltrados.add(pedido);
                    }
                }

                // Mostrar pedidos sin filtro de mes
                mostrarPedidosFiltrados();
            } else {
                Toast.makeText(this, "Error al obtener pedidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarPedidosPorMes(int mesSeleccionado) {
        if (mesSeleccionado == 0) {
            // Mostrar todos los pedidos (sin filtrar por mes)
            mostrarPedidosFiltrados();
            return;
        }

        ArrayList<PedidoSA> pedidosMes = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM", new Locale("es", "ES"));

        for (PedidoSA pedido : pedidosFiltrados) {
            String mesPedido = dateFormat.format(pedido.getFechaHora());
            if (mesPedido.equalsIgnoreCase(getResources().getStringArray(R.array.meses_array)[mesSeleccionado])) {
                pedidosMes.add(pedido);
            }
        }

        pedidosFiltrados = pedidosMes;
        mostrarPedidosFiltrados();
    }

    private void mostrarPedidosFiltrados() {
        resumenProductos.clear();
        int totalPedidos = 0;
        double ventasTotales = 0.0;

        // Limpiar el contenedor principal
        linearLayoutContainer.removeAllViews();

        // Crear el encabezado de la tabla
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        headerRow.setBackgroundColor(getResources().getColor(R.color.gray));

        // Crear encabezados de columna
        TextView platoHeader = crearTextoEncabezado("Plato");
        headerRow.addView(platoHeader);

        TextView precioHeader = crearTextoEncabezado("Precio Unitario");
        headerRow.addView(precioHeader);

        TextView cantidadHeader = crearTextoEncabezado("Cantidad");
        headerRow.addView(cantidadHeader);

        TextView totalHeader = crearTextoEncabezado("Total");
        headerRow.addView(totalHeader);

        // Agregar la fila de encabezados al contenedor
        linearLayoutContainer.addView(headerRow);

        // Procesar cada pedido
        for (PedidoSA pedido : pedidosFiltrados) {
            totalPedidos++;

            for (ProductoSA producto : pedido.getProductos()) {
                String nombreProducto = producto.getNombre();

                if (!resumenProductos.containsKey(nombreProducto)) {
                    resumenProductos.put(nombreProducto, new ProductoResumen(
                            producto.getNombre(), producto.getPrecio(), producto.getCantidad(), producto.getTotal()
                    ));
                } else {
                    ProductoResumen resumen = resumenProductos.get(nombreProducto);
                    resumen.sumarCantidad(producto.getCantidad());
                    resumen.sumarTotal(producto.getTotal());
                }
            }
        }

        // Agregar filas con productos procesados
        for (ProductoResumen resumen : resumenProductos.values()) {
            ventasTotales += resumen.getTotal(); // Sumar totales de cada producto

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Crear columnas de datos
            TextView tvNombre = crearTextoCelda(resumen.getNombre());
            row.addView(tvNombre);

            TextView tvCantidad = crearTextoCelda("S/ " +  String.valueOf(resumen.getCantidad())); // Cantidad
            row.addView(tvCantidad);

            TextView tvPrecio = crearTextoCelda(String.format("%.0f", resumen.getPrecio())); // Precio Unitario
            row.addView(tvPrecio);

            TextView tvTotal = crearTextoCelda("S/ " + String.format("%.2f", resumen.getTotal())); // Total
            row.addView(tvTotal);

            // Agregar la fila al contenedor
            linearLayoutContainer.addView(row);
        }

        // Actualizar ventas totales y total de pedidos
        tvVentasTotales.setText("S/ " + String.format("%.2f", ventasTotales));
        tvTotalPedidos.setText(String.valueOf(totalPedidos));

        // Graficar productos
        graficarProductos();
    }

    // Métodos auxiliares para crear TextViews dinámicos
    private TextView crearTextoEncabezado(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(8, 8, 8, 8);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // Distribuir el espacio proporcionalmente
        ));
        return textView;
    }

    private TextView crearTextoCelda(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(14);
        textView.setPadding(16, 8, 16, 8);
        textView.setGravity(Gravity.CENTER);

        // Configurar layout params para tamaño y márgenes
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, // Ancho proporcional
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1 // Peso igual para distribuir columnas
        );
        params.setMargins(2, 2, 2, 2); // Margen entre celdas
        textView.setLayoutParams(params);

        return textView;
    }

    private void graficarProductos() {
        if (resumenProductos.isEmpty()) {
            Toast.makeText(this, "No hay datos para graficar", Toast.LENGTH_SHORT).show();
            barChart.clear();
            barChart.invalidate();
            return;
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;

        for (ProductoResumen resumen : resumenProductos.values()) {
            entries.add(new BarEntry(index++, (int) resumen.getTotal()));
            labels.add(resumen.getNombre());
            String total = Double.toString(resumen.getTotal());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Ventas");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(dataSet);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if ((int) value < labels.size()) {
                    return labels.get((int) value);
                } else {
                    return ""; // Evita IndexOutOfBoundsException
                }
            }
        });

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getAxisLeft().setGranularity(1f);
        barChart.invalidate();
    }

}