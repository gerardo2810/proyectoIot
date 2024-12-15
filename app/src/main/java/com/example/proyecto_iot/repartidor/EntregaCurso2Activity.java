package com.example.proyecto_iot.repartidor;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_curso_2);



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
                            String texto7 = cantidadProductos + " productos";
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

        Button btnShowDialog = findViewById(R.id.button11);
        btnShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQrDialog();
            }
        });

    }
    public void abrirPagInicio (View view) {

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
                .update("estado", 4)
                .addOnSuccessListener(aVoid -> {
                    // Acción si la actualización fue exitosa
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

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InicioRepartidorActivity.class); // Regresar a la vistaInicio
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
        startActivity(intent);
    }

    private void showQrDialog() {
        // Crear el diálogo
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_qr_repartidor);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Ajustar tamaño

        ImageView imgPhoto = dialog.findViewById(R.id.imgPhoto);
        imgPhoto.setImageResource(R.drawable.imagen_qr_code);

        // Configurar el botón de cierre
        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
