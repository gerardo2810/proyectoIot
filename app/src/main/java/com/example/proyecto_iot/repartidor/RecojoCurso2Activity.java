package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecojoCurso2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recojo_curso_2);

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

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InicioRepartidorActivity.class); // Regresar a la vistaInicio
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
        startActivity(intent);
    }

}
