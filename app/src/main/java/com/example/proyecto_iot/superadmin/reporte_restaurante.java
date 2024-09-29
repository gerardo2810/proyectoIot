package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class reporte_restaurante extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_reporte_restaurante);

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(reporte_restaurante.this, lista_restaurantes_superadmin.class);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion de la los graficos estadisticos

                //grafico de barras
        BarChart barChart = findViewById(R.id.barChart);
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        entries1.add(new BarEntry(0, 90000));
        entries1.add(new BarEntry(1, 155000));
        entries1.add(new BarEntry(2, 65000));
        BarDataSet dataSet1 = new BarDataSet(entries1, "Dinero en S/");
        dataSet1.setColors(new int[] {
                getResources().getColor(R.color.colorEgresos),
                getResources().getColor(R.color.colorIngresos),
                getResources().getColor(R.color.colorValorNeto)
        });
        BarData barData = new BarData(dataSet1);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        final String[] labels = {"Ingresos", "Egresos", "Valor Neto"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(labels.length);
        xAxis.setCenterAxisLabels(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(entries1.size() - 1 + 0.25f);
        barChart.invalidate();

                //grafico pastel
        PieChart pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> entries2 = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            entries2.add(new PieEntry(i * 10, "platillo_" + i));  // Crea 8 entradas, "platillo_1" a "platillo_8"
        }
        PieDataSet dataSet2 = new PieDataSet(entries2, null);
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }
        dataSet2.setColors(colors);
        PieData pieData = new PieData(dataSet2);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(true); // Habilitar la leyenda
        pieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Alinear verticalmente
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Alinear horizontalmente
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL); // Cambiar a orientación horizontal
        pieChart.getLegend().setDrawInside(false); // No dibujar dentro del gráfico
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
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
                    intent = new Intent(reporte_restaurante.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(reporte_restaurante.this, gestion_reportes_superadmin.class);
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
}