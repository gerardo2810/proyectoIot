package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecojoCurso2Activity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recojo_curso_2);

        mostrarUbicacion();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String idPedido = getIntent().getStringExtra("idPedido");

        db.collection("pedidos").document(idPedido)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idRestaurante = documentSnapshot.getString("idRestaurante");
                        String idCliente = documentSnapshot.getString("idCliente");
                        String estadoPedido = "Listo para recojer";
                        // Recupera el array de productos (IDs)
                        List<Map<String,Object>> productos = (List<Map<String,Object>>) documentSnapshot.get("productos");

                        if (productos != null && !productos.isEmpty()) {

                            List<String> nombresProductos = new ArrayList<>();

                            for (Map<String,Object> producto :productos){
                                String nombre = (String) producto.get("nombre");
                                nombresProductos.add("1x " + nombre);
                            }


                            int cantidadProductos = productos.size();
                            TextView cantidadTextView = findViewById(R.id.cantidadProductos);
                            String texto7 = cantidadProductos + " producto(s)";
                            cantidadTextView.setText(texto7);
                            // Muestra los nombres de los productos en la vista

                            TextView productosTextView = findViewById(R.id.productos_text_view);
                            productosTextView.setText(String.join("\n", nombresProductos)); // Muestra cada nombre en una nueva línea

                        } else {
                            Toast.makeText(this, "No hay productos en este pedido", Toast.LENGTH_SHORT).show();
                        }

                        TextView tvIdPedido = findViewById(R.id.idPedido);
                        tvIdPedido.setText(idPedido);

                        TextView tvEstadoPedido = findViewById(R.id.texto2);
                        tvEstadoPedido.setText(estadoPedido);

                        // Ahora consulta los datos del restaurante
                        db.collection("restaurantes").document(idRestaurante)
                                .get()
                                .addOnSuccessListener(restauranteSnapshot -> {
                                    if (restauranteSnapshot.exists()) {
                                        String nombreRestaurante = restauranteSnapshot.getString("nombre");
                                        String direccionRestaurante = restauranteSnapshot.getString("ubicacion");
                                        String logoUrl = restauranteSnapshot.getString("fotoLogo");

                                        // Muestra los datos del restaurante
                                        TextView nombreRestauranteTextView = findViewById(R.id.product_name);
                                        TextView direccionRestauranteTextView = findViewById(R.id.product_description);
                                        ImageView imageViewRestaurante = findViewById(R.id.product_image);

                                        nombreRestauranteTextView.setText(nombreRestaurante);
                                        direccionRestauranteTextView.setText(direccionRestaurante);
                                        Glide.with(this)
                                                .load(logoUrl) // URL del logo
                                                .placeholder(R.drawable.baseline_file_upload_24) // Imagen temporal mientras carga
                                                .into(imageViewRestaurante); // Tu ImageView
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al cargar los datos del restaurante", Toast.LENGTH_SHORT).show();
                                });
                        db.collection("clientes").document(idCliente)
                                .get()
                                .addOnSuccessListener(clienteSnapshot -> {
                                    if (clienteSnapshot.exists()) {
                                        String nombreCliente = clienteSnapshot.getString("Nombre");
                                        String apellidoCliente = clienteSnapshot.getString("Apellido");
                                        String numeroCelular = clienteSnapshot.getString("Telefono");

                                        // Muestra los datos del restaurante
                                        TextView nombreClienteTextView = findViewById(R.id.nombreCliente);
                                        TextView numeroCelularTextView = findViewById(R.id.numeroCelular);

                                        String texto1 = nombreCliente + " "+ apellidoCliente;

                                        nombreClienteTextView.setText(texto1);
                                        numeroCelularTextView.setText(numeroCelular);

                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al cargar los datos del restaurante", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar los datos del pedido", Toast.LENGTH_SHORT).show();
                });

        Button boton1 = findViewById(R.id.button3);
        boton1.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ultima_vista", "EntregaCurso1Activity");
            editor.putString("idPedido", idPedido); // ID del pedido
            editor.apply();

            //Actualizar pedido

            db.collection("pedidos").document(idPedido)
                    .update("estado", 7)
                    .addOnSuccessListener(aVoid -> {
                        // Acción si la actualización fue exitosa
                        guardarLog("Recogió el pedido de id: " + idPedido + " y ya esta en camino a la entrega.", "Repartidor");
                        Log.d("Firebase", "Entrega del pedido en progreso");
                    })
                    .addOnFailureListener(e -> {
                        // Acción si ocurrió un error
                        Log.w("Firebase", "Error al tomar el recojo del pedido", e);
                    });


            Intent intent = new Intent(this, EntregaCurso1Activity.class);
            intent.putExtra("idPedido", idPedido);
            startActivity(intent);
        });

    }

    public void guardarLog(String mensaje, String rol) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener el UID del usuario logueado
        String usuarioUID = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "Usuario desconocido";

        // Obtener fecha y hora actuales
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String horaActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Crear un mapa para guardar el log
        HashMap<String, Object> logData = new HashMap<>();
        logData.put("mensaje", mensaje);
        logData.put("usuarioUID", usuarioUID);
        logData.put("rol", rol);
        logData.put("fecha", fechaActual);
        logData.put("hora", horaActual);

        // Guardar el log en Firestore
        db.collection("logs")
                .add(logData)
                .addOnSuccessListener(documentReference -> {
                    // Éxito al guardar el log
                    System.out.println("Log guardado con éxito. ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Error al guardar el log
                    System.err.println("Error al guardar el log: " + e.getMessage());
                });
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InicioRepartidorActivity.class); // Regresar a la vistaInicio
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
        startActivity(intent);
    }

    public void mostrarUbicacion() {

        //Actualizar pedido
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        int selfPermissionFineLocation = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int selfPermissionCoarseLocation = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (selfPermissionFineLocation == PackageManager.PERMISSION_GRANTED &&
                selfPermissionCoarseLocation == PackageManager.PERMISSION_GRANTED) {

            //tenemos permisos
            FusedLocationProviderClient providerClient = LocationServices.getFusedLocationProviderClient(this);
            providerClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();
                    Map<String, Object> ubicacion = new HashMap<>();
                    ubicacion.put("lat", latitud);
                    ubicacion.put("lng", longitud);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("repartidores")
                            .document(userId)
                            .set(new HashMap<String, Object>() {{
                                put("ubicacionRepartidor", ubicacion);
                            }}, SetOptions.merge()) // Usar merge para no sobrescribir otros datos
                            .addOnSuccessListener(aVoid -> Log.d("Location", "Ubicación actualizada"))
                            .addOnFailureListener(e -> Log.e("Location", "Error al actualizar ubicación", e));

                }

            });

        } else {
            //no tenemos permisos, se deben solicitar
            locationPermissionLauncher.launch(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            });

        }

    }

    ActivityResultLauncher<String[]> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean fineLocationGranted = result.get(android.Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseLocationGranted = result.get(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                if (fineLocationGranted != null && fineLocationGranted) {
                    Log.d("msg-test-locationPermissionLauncher", "Permiso de ubicación precisa concedido");
                    mostrarUbicacion();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    Log.d("msg-test-locationPermissionLauncher", "Permiso de ubicación aproximada concedido");
                } else {
                    Log.d("msg-test-locationPermissionLauncher", "Ningún permiso concedido");
                }
            }
    );

}
