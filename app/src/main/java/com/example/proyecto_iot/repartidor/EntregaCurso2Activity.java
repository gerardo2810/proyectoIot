package com.example.proyecto_iot.repartidor;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class EntregaCurso2Activity extends AppCompatActivity {

    String idRestaurante;
    String idPedido;
    String qrUrl;
    Button escanearQrButton;
    ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_curso_2);

        imgPhoto = findViewById(R.id.qr_image);

        mostrarUbicacion();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        idPedido = getIntent().getStringExtra("idPedido");
        db.collection("pedidos").document(idPedido)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idCliente = documentSnapshot.getString("idCliente");
                        String direccion = documentSnapshot.getString("direccion");
                        idRestaurante = documentSnapshot.getString("idRestaurante");

                        TextView direccionClienteTextView = findViewById(R.id.direccion_destino);
                        direccionClienteTextView.setText(direccion);

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

                        // Ahora consulta los datos del cliente
                        db.collection("clientes").document(idCliente)
                                .get()
                                .addOnSuccessListener(clienteSnapshot -> {
                                    if (clienteSnapshot.exists()) {
                                        String nombreCliente = clienteSnapshot.getString("Nombre");
                                        String apellidoCliente = clienteSnapshot.getString("Apellido");
                                        String numeroCelular = clienteSnapshot.getString("Telefono");

                                        // Muestra los datos del restaurante
                                        TextView nombreClienteTextView = findViewById(R.id.product_name);
                                        TextView numeroClienteTextView = findViewById(R.id.numero);
                                        TextView nombreClienteTextView1 = findViewById(R.id.nombreCliente);
                                        TextView numeroClienteTextView1 = findViewById(R.id.numeroCliente);

                                        String texto3 = nombreCliente + " " + apellidoCliente;

                                        nombreClienteTextView.setText(texto3);
                                        numeroClienteTextView.setText(numeroCelular);
                                        nombreClienteTextView1.setText(texto3);
                                        numeroClienteTextView1.setText(numeroCelular);
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

        db.collection("restaurantes").document(idRestaurante)
                .get()
                .addOnSuccessListener(restauranteSnapshot -> {
                    if (restauranteSnapshot.exists()) {
                        qrUrl = restauranteSnapshot.getString("qrCodeUrl");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar los datos del restaurante", Toast.LENGTH_SHORT).show();
                });


        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.placeholder)
                .into(imgPhoto);

        escanearQrButton = findViewById(R.id.button3);
        escanearQrButton.setOnClickListener(v -> iniciarEscaneoQR());

    }


    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InicioRepartidorActivity.class); // Regresar a la vistaInicio
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
        startActivity(intent);
    }

    // Método para iniciar el escaneo de QR
    private void iniciarEscaneoQR() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Escanea el código QR del cliente");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                String qrEscaneado = result.getContents(); // QR escaneado contiene el ID del restaurante

                // Validar si el ID escaneado coincide con el idRestaurante del Intent
                if (qrEscaneado.equals(idPedido)) {
                    qrExitoso(); // Llama al método para abrir ConfirmarPagoActivity
                } else {
                    Toast.makeText(this, "QR inválido. Inténtalo nuevamente.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No se escaneó ningún QR.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error al escanear el QR.", Toast.LENGTH_SHORT).show();
        }
    }

    public void qrExitoso(){
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ultima_vista", "InicioRepartidorActivity");
        editor.putString("idPedido", null); // ID del pedido se volverá vacío
        editor.apply();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String repartidorId = auth.getCurrentUser().getUid(); // Obtener UID del repartidor

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Actualizar pedido

        db.collection("pedidos").document(idPedido)
                .update("estado", 8)
                .addOnSuccessListener(aVoid -> {
                    // Acción si la actualización fue exitosa
                    guardarLog("se ha entregado exitosamente el pedido de id: " + idPedido, "Repartidor");
                    Log.d("Firebase", "Entrega del pedido exitosamente");
                })
                .addOnFailureListener(e -> {
                    // Acción si ocurrió un error
                    Log.w("Firebase", "Error al entregar el pedido", e);
                });

        // Ahora consulta los datos del cliente
        db.collection("restaurantes").document(idRestaurante)
                .get()
                .addOnSuccessListener(restauranteSnapshot -> {
                    if (restauranteSnapshot.exists()) {
                        // Datos del pedido
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "PE"));
                        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Lima"));
                        String fecha = dateFormat.format(new Date());


                        Map<String, Object> pedido = new HashMap<>();
                        pedido.put("fechaHora", FieldValue.serverTimestamp());
                        pedido.put("fecha", fecha);
                        pedido.put("nombreRestaurante", restauranteSnapshot.getString("nombre"));
                        pedido.put("idRepartidor", repartidorId);

                        // Guardar en Firestore
                        db.collection("historialPedidos")
                                .add(pedido)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Pedido guardado en el historial", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al guardar el pedido", Toast.LENGTH_SHORT).show();
                                });

                        Intent intent = new Intent(this, InicioRepartidorActivity.class);
                        intent.putExtra("showDialog", true);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar los datos del restaurante", Toast.LENGTH_SHORT).show();
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

    public void mostrarUbicacion() {

        //Actualizar pedido
        FirebaseAuth auth = FirebaseAuth.getInstance();
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
