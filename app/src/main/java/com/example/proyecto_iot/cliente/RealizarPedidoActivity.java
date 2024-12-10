package com.example.proyecto_iot.cliente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class RealizarPedidoActivity extends AppCompatActivity {

    private ImageView backArrow;
    private Button payButton;
    private TextView seeMore;
    private List<Producto> productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_realizar_pedido_cliente);

        // Configurar el padding para ajustar a las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recibir los datos del Intent
        Intent intent = getIntent();
        double subtotal = intent.getDoubleExtra("subtotal", 0.0);
        double precioDelivery = intent.getDoubleExtra("precio_delivery", 0.0); // Asegúrate de usar "precio_delivery"
        String nombreRestaurante = intent.getStringExtra("nombreRestaurante");
        String fotoLogo = intent.getStringExtra("fotoLogo");
        int cantidadProductos = intent.getIntExtra("cantidadProductos", 0); // Recibir la cantidad de productos

        String restauranteId = getIntent().getStringExtra("restauranteId");
        System.out.println("Realizar pedido" + restauranteId);
         productos = (List<Producto>) getIntent().getSerializableExtra("carrito");
        System.out.println("REALIZAR PEDIDO" + productos);



        // Debug para asegurarte de que la cantidad es correcta
        System.out.println("RealizarPedido -> Cantidad de productos: " + cantidadProductos);
        System.out.println("Realizar Pedido:");
        System.out.println("Subtotal: " + subtotal);
        System.out.println("Precio Delivery: " + precioDelivery);
        System.out.println("Nombre Restaurante: " + nombreRestaurante);
                                    System.out.println("Foto Logo: " + fotoLogo);

        // Calcular el total
        double pagoTotal = subtotal + precioDelivery;
        String temp = "Productos - " + cantidadProductos;
        // Mostrar los valores en los TextViews
        TextView costosProductosTextView = findViewById(R.id.costos_productos_value);
        TextView envioTextView = findViewById(R.id.envio_value);
        TextView pagoTotalTextView = findViewById(R.id.pago_total_value);
        TextView nameRestauranteTextView = findViewById(R.id.restaurant_name1);
        TextView subtotalTextView = findViewById(R.id.subtotal_value);
        TextView cantidadTextView = findViewById(R.id.products_count);
        ImageView fotoLogoImageView = findViewById(R.id.profile_image);

        // Formatear valores como moneda
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
        costosProductosTextView.setText(currencyFormat.format(subtotal));
        envioTextView.setText(currencyFormat.format(precioDelivery));
        pagoTotalTextView.setText(currencyFormat.format(pagoTotal));
        subtotalTextView.setText(currencyFormat.format(pagoTotal));
        nameRestauranteTextView.setText(nombreRestaurante);
        cantidadTextView.setText(String.valueOf(temp)); // Mostrar la cantidad como texto


        // Cargar la imagen del restaurante usando Glide
        Glide.with(this)
                .load(fotoLogo)
                .placeholder(R.drawable.placeholder) // Imagen de placeholder
                .into(fotoLogoImageView);
        // Inicialización de vistas
        backArrow = findViewById(R.id.back_arrow);
        payButton = findViewById(R.id.pay_button);
        seeMore = findViewById(R.id.see_more);

        // Listener para la flecha de retroceso
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Regresar a la vista anterior (puede ser InicioClienteActivity o CarritoClienteActivity)
                Intent intent = new Intent(RealizarPedidoActivity.this, CarritoClienteActivity.class); // O CarritoClienteActivity, según sea el caso
                startActivity(intent);
                finish(); // Finaliza la actividad actual
            }
        });

        // Listener para el botón "Ver más"
        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí podrías navegar a una actividad donde muestres más detalles de los productos
                Intent intent = new Intent(RealizarPedidoActivity.this, VerMasProductosClienteActivity.class); // Crear esta actividad según tu estructura
                startActivity(intent);
            }
        });

        // Listener para el botón de pagar
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la instancia de Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Obtener el ID del cliente logueado
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Obtener la dirección del cliente desde Firestore
                db.collection("clientes").document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Obtener la dirección del cliente
                                String direccionCliente = documentSnapshot.getString("Direccion");
                                System.out.println("Dirección del cliente: " + direccionCliente);

                                // Obtener los demás datos necesarios
                                double subtotal = intent.getDoubleExtra("subtotal", 0.0);
                                double precioDelivery = intent.getDoubleExtra("precio_delivery", 0.0);
                                String nombreRestaurante = intent.getStringExtra("nombreRestaurante");
                                String restauranteId = intent.getStringExtra("restauranteId");
                                double pagoTotal = subtotal + precioDelivery;
                                List<String> idProductos = new ArrayList<>();

                                // Extraer IDs de productos del carrito
                                for (Producto producto : productos) {
                                    idProductos.add(producto.getId());
                                }

                                // Obtener la fecha y hora actual en la zona horaria de Perú
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm:ss a", new Locale("es", "PE"));
                                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Lima"));
                                String fechaHora = dateFormat.format(new Date());

                                // Crear un mapa de datos para el pedido
                                Map<String, Object> pedidoData = new HashMap<>();
                                pedidoData.put("idCliente", userId);
                                pedidoData.put("direccion", direccionCliente);
                                pedidoData.put("estado", 0); // Estado inicial
                                pedidoData.put("fechaHora", fechaHora);
                                pedidoData.put("productos", idProductos);
                                pedidoData.put("idRepartidor", ""); // Campo vacío por ahora
                                pedidoData.put("idRestaurante", restauranteId);
                                pedidoData.put("nombreRestaurante", nombreRestaurante);
                                pedidoData.put("pagoTotal", pagoTotal);

                                // Agregar el pedido a la colección "pedidos"
                                db.collection("pedidos")
                                        .add(pedidoData)
                                        .addOnSuccessListener(documentReference -> {
                                            // Pedido creado con éxito
                                            System.out.println("Pedido creado con ID: " + documentReference.getId());

                                            // Navegar a la actividad de confirmación
                                            Intent intent = new Intent(RealizarPedidoActivity.this, PedidoAceptadoCliente.class);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Manejo de errores
                                            System.err.println("Error al crear el pedido: " + e.getMessage());
                                        });
                            } else {
                                System.err.println("El cliente no existe en la base de datos.");
                            }
                        })
                        .addOnFailureListener(e -> {
                            System.err.println("Error al obtener los datos del cliente: " + e.getMessage());
                        });
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_restaurantes) {
                    startActivity(new Intent(RealizarPedidoActivity.this, InicioClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_carrito) {
                    startActivity(new Intent(RealizarPedidoActivity.this, CarritoClienteActivity.class));
                    return true;
                } else if (id == R.id.navigation_ordenes) {
                    startActivity(new Intent(RealizarPedidoActivity.this, HistorialPedidosActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(RealizarPedidoActivity.this, PerfilClienteActivity.class));
                    return true;
                }

                return false;
            }
        });


    }
}
