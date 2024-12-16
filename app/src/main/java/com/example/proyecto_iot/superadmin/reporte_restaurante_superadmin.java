package com.example.proyecto_iot.superadmin;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class reporte_restaurante_superadmin extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private BarChart barChart;
    private TextView tvVentasTotales, tvTotalPedidos;
    private Spinner spinnerMes;

    private LinearLayout linearLayoutContainer;
    private String restauranteUID;
    private ArrayList<PedidoSA> pedidosFiltrados = new ArrayList<>();
    private Map<String, ProductoResumen> resumenProductos = new HashMap<>();
    private TextView textView4;

    String totalsales, totalorders, selectedValue;
    private Button btnDownloadPdf;
    Bitmap barChartBitmap;
    String nombreRestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_reporte_restaurante);

        createNotificationChannel();

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener referencia a vistas
        barChart = findViewById(R.id.barChartVentas);
        tvVentasTotales = findViewById(R.id.tvVentasTotales);
        tvTotalPedidos = findViewById(R.id.tvTotalPedidos);
        linearLayoutContainer = findViewById(R.id.linearLayoutContainer);
        spinnerMes = findViewById(R.id.spinnerMes);
        restauranteUID = getIntent().getStringExtra("restauranteUID");

        // Referencia al documento en la colección "restaurantes"
        DocumentReference docRef = db.collection("restaurantes").document(restauranteUID);

        // Inicializar el TextView
        textView4 = findViewById(R.id.textView4);

        // Obtener el nombre del restaurante
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Obtener el campo 'nombre' del documento
                    nombreRestaurante = document.getString("nombre");

                    textView4.setText("Reporte de Ventas de " + nombreRestaurante);

                }
            }
        });

        configurarSpinnerMes();

        obtenerPedidos();

        // Inicializa los elementos de la vista
        spinnerMes = findViewById(R.id.spinnerMes); // Spinner de los meses
        btnDownloadPdf = findViewById(R.id.buttonDownloadPDF);

        // Configura el botón para descargar el PDF
        btnDownloadPdf.setOnClickListener(v -> {
            // Verificar si tenemos permiso para mostrar notificaciones
            if (!hasNotificationPermission()) {
                // Si no tenemos el permiso, solicitarlo
                requestNotificationPermission();
            } else {
                // Si ya tenemos el permiso, generamos el reporte PDF
                generateSalesReport(this, resumenProductos, barChartBitmap, selectedValue, totalsales, totalorders);
            }
        });

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

    public void generateSalesReport(
            Context context,
            Map<String, ProductoResumen> salesData,
            Bitmap barChartBitmap,
            String selectedMonth,
            String totalSales,
            String totalOrders
    ) {
        if (salesData.isEmpty()) {
            Toast.makeText(context, "No se puede generar el reporte ya que no hay datos", Toast.LENGTH_SHORT).show();
        } else {
            // Define the file path
            String pdfPath = "";
            if(selectedMonth.equals("-Seleccionar-")){
                pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/ReporteVentas" + nombreRestaurante + "General2024.pdf";
            } else {
                pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/ReporteVentas" + nombreRestaurante + selectedMonth + "2024.pdf";
            }
            File pdfFile = new File(pdfPath);

            try {
                // Create a PDF writer instance
                PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);

                // Add dynamic title
                String title = selectedMonth.equals("-Seleccionar-")
                        ? "Reporte de Ventas General"
                        : "Reporte de Ventas del Mes: " + selectedMonth;

                Paragraph titleParagraph = new Paragraph(title)
                        .setFontSize(18)
                        .setBold()
                        .setFontColor(ColorConstants.BLACK)
                        .setMarginBottom(20);
                document.add(titleParagraph);

                // Add total sales and orders
                Paragraph totalsParagraph = new Paragraph(
                        "Total de Ventas: " + totalSales + "\nTotal de Pedidos: " + totalOrders)
                        .setFontSize(12)
                        .setFontColor(ColorConstants.BLACK)
                        .setMarginBottom(20);
                document.add(totalsParagraph);

                // Define the table column widths (added column for Unit Price)
                float[] columnWidths = {200f, 100f, 100f, 100f}; // Adjust column widths
                Table table = new Table(columnWidths);

                // Add table header
                table.addCell("Producto");
                table.addCell("Cantidad");
                table.addCell("Precio Unitario (S/)");
                table.addCell("Total (S/)");

                // Populate the table with sales data
                for (Map.Entry<String, ProductoResumen> entry : salesData.entrySet()) {
                    ProductoResumen productoResumen = entry.getValue(); // Get the ProductoResumen object

                    // Extract product data from ProductoResumen
                    String nombreProducto = productoResumen.getNombre();
                    int cantidadVendida = productoResumen.getCantidad();
                    double precioUnitario = productoResumen.getPrecio(); // Get unit price
                    double totalVentas = productoResumen.getTotal();

                    // Add the product data to the table
                    table.addCell(nombreProducto);
                    table.addCell(String.valueOf(cantidadVendida)); // Add quantity
                    table.addCell("S/ " + String.format("%.2f", precioUnitario)); // Add unit price
                    table.addCell("S/ " + String.format("%.2f", totalVentas)); // Add total sales (formatted)
                }

                // Add the table to the document
                document.add(table);

                // Add bar chart image
                if (barChartBitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    barChartBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] imageBytes = stream.toByteArray();

                    ImageData imageData = ImageDataFactory.create(imageBytes);
                    Image image = new Image(imageData);
                    image.setAutoScale(true); // Auto-scale image to fit
                    image.setMarginTop(20);
                    document.add(image);
                }

                // Close the document
                document.close();
                Toast.makeText(context, "Reporte generado exitosamente.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
            }
            showDownloadNotification(pdfPath);
        }
    }

    private Bitmap getBarChartBitmap(BarChart barChart) {
        // Captura el gráfico como un Bitmap
        barChart.setDrawingCacheEnabled(true);
        barChart.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(barChart.getDrawingCache());
        barChart.setDrawingCacheEnabled(false);
        return bitmap;
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

    private ArrayList<PedidoSA> pedidosOriginales = new ArrayList<>(); // Mantén la lista original

    private void obtenerPedidos() {
        CollectionReference pedidosRef = db.collection("pedidos");

        pedidosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                pedidosFiltrados.clear();
                pedidosOriginales.clear(); // Asegúrate de limpiar la lista original

                for (QueryDocumentSnapshot document : task.getResult()) {
                    PedidoSA pedido = document.toObject(PedidoSA.class);

                    // Filtrar por restaurante y estado
                    if (pedido.getIdRestaurante().equals(restauranteUID) && pedido.getEstado() == 8) {
                        pedidosFiltrados.add(pedido);
                        pedidosOriginales.add(pedido); // Agrega a la lista original
                    }
                }

                mostrarPedidosFiltrados();
            } else {
                Toast.makeText(this, "Error al obtener pedidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarPedidosPorMes(int mesSeleccionado) {
        Log.d("FiltrarPedidos", "Mes seleccionado: " + mesSeleccionado);
        Log.d("FiltrarPedidos", "Pedidos originales: " + pedidosOriginales.size());
        Log.d("FiltrarPedidos", "Pedidos filtrados antes de procesar: " + pedidosFiltrados.size());

        TextView tvReporteMes = findViewById(R.id.textViewMes);

        // Verifica si pedidosOriginales está vacío o nulo
        if (pedidosOriginales == null || pedidosOriginales.isEmpty()) {
            tvReporteMes.setText("Reporte de Ventas: General");
            pedidosFiltrados = new ArrayList<>(); // Lista vacía.
            mostrarPedidosFiltrados();
            return;
        }

        // Para opción "-Seleccionar-"
        if (mesSeleccionado == 0) {
            pedidosFiltrados = new ArrayList<>(pedidosOriginales);
            if (pedidosFiltrados.isEmpty()) {
                tvReporteMes.setText("Reporte de Ventas: General");
            } else {
                tvReporteMes.setText("Reporte de Ventas: General");
            }
            mostrarPedidosFiltrados();
            return;
        }

        // Para un mes específico
        String mesSeleccionadoTexto = getResources().getStringArray(R.array.meses_array)[mesSeleccionado];
        ArrayList<PedidoSA> pedidosMes = new ArrayList<>();
        for (PedidoSA pedido : pedidosOriginales) {
            // Filtra pedidos por fecha usando la cadena del mes seleccionado
            if (pedido.getFechaHora().toLowerCase().contains(mesSeleccionadoTexto.toLowerCase())) {
                pedidosMes.add(pedido);
            }
        }

        pedidosFiltrados = pedidosMes;

        tvReporteMes.setText("Reporte de Ventas: " + mesSeleccionadoTexto);

        mostrarPedidosFiltrados();
    }

    private void mostrarPedidosFiltrados() {
        // Limpia el contenedor principal y otros elementos si no hay datos.
        if (pedidosFiltrados == null || pedidosFiltrados.isEmpty()) {
            resumenProductos.clear();
            linearLayoutContainer.removeAllViews(); // Limpia cualquier vista previa.
            tvVentasTotales.setText("S/ 0.00"); // Resetea el texto de ventas totales.
            tvTotalPedidos.setText("0"); // Resetea el texto de total de pedidos.
            graficarProductos(); // Llama al método de graficar para que limpie el gráfico.

            return; // No procesa nada más si no hay datos.
        }

        // Inicializa las variables de resumen.
        resumenProductos.clear();
        int totalPedidos = 0;
        double ventasTotales = 0.0;

        // Limpia el contenedor principal
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

        TextView precioHeader = crearTextoEncabezado("Cantidad");
        headerRow.addView(precioHeader);

        TextView cantidadHeader = crearTextoEncabezado("Precio Unitario");
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
                            producto.getNombre(), producto.getCantidad(), producto.getPrecio(), producto.getTotal()
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

            // Mostrar solo la cantidad (sin "S/")
            TextView tvCantidad = crearTextoCelda(String.valueOf(resumen.getCantidad())); // Cantidad sin "S/"
            row.addView(tvCantidad);

            TextView tvPrecio = crearTextoCelda("S/ " + String.format("%.2f", resumen.getPrecio())); // Precio Unitario
            row.addView(tvPrecio);

            TextView tvTotal = crearTextoCelda("S/ " + String.format("%.2f", resumen.getTotal())); // Total con "S/"
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
                    return "";
                }
            }
        });

        // Desactivar los xlabels
        barChart.getXAxis().setDrawLabels(false);

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getAxisLeft().setGranularity(1f);
        barChart.invalidate();
        barChart.getDescription().setEnabled(false);

        barChartBitmap = getBarChartBitmap(barChart);
        totalsales = tvVentasTotales.getText().toString();
        totalorders = tvTotalPedidos.getText().toString();
        selectedValue = spinnerMes.getSelectedItem().toString();
    }

    // Método para crear la notificación
    private void showDownloadNotification(String pdfPath) {

        // Obtener el archivo PDF
        File pdfFile = new File(pdfPath);

        // Usar FileProvider para obtener una URI de contenido
        Uri pdfUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", pdfFile);

        // Crear el Intent para abrir el archivo PDF
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Conceder permisos para acceder a la URI
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Crear el PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE);

        // Crear la notificación
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.logoapp45) // Icono de la notificación
                .setContentTitle("PDF descargado")  // Título de la notificación
                .setContentText("Toca para abrir el PDF")
                .setAutoCancel(true)  // Se elimina la notificación al hacer clic
                .setContentIntent(pendingIntent);  // El PendingIntent que abrirá el PDF

        // Obtener el NotificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notificationBuilder.build());
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Solicitar permiso para enviar notificaciones en Android 13 o superior
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
    }

    private boolean hasNotificationPermission() {
        // Verifica si el permiso para enviar notificaciones ha sido concedido
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    public void createNotificationChannel() {
        // Verifica si la versión de Android es Oreo o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default";  // ID del canal
            CharSequence channelName = "Default Channel";  // Nombre del canal
            int importance = NotificationManager.IMPORTANCE_DEFAULT;  // Prioridad del canal
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            // Puedes configurar otras propiedades del canal aquí (por ejemplo, sonido, vibración, etc.)
            channel.setDescription("Canal por defecto para notificaciones");

            // Registra el canal en el sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}