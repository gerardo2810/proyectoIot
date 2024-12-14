package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView; // Importar para actualizar el número de productos

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoCarritoAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VerMasProductosClienteActivity extends AppCompatActivity implements ProductoCarritoAdapter.OnProductUpdateListener {

    private RecyclerView recyclerView;
    private ProductoCarritoAdapter adapter;
    private List<Producto> productoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mas_productos_cliente);

        // Recuperar los valores del Intent
        String nombreRestaurante = getIntent().getStringExtra("nombreRestaurante");
        String restauranteId = getIntent().getStringExtra("restauranteId");
        // Referencia al ImageView
        ImageView profileImage = findViewById(R.id.profile_image);

        // Buscar en Firestore usando el restauranteId
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurantes").document(restauranteId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtener el campo fotoLogo
                        String fotoLogoUrl = documentSnapshot.getString("fotoLogo");

                        if (fotoLogoUrl != null && !fotoLogoUrl.isEmpty()) {
                            // Usar Glide para cargar la imagen
                            Glide.with(this)
                                    .load(fotoLogoUrl)
                                    .placeholder(R.drawable.placeholder) // Imagen mientras se carga
                                    .error(R.drawable.placeholder) // Imagen en caso de error
                                    .into(profileImage);
                        } else {
                            // En caso de que no haya URL, usar una imagen predeterminada
                            profileImage.setImageResource(R.drawable.placeholder);
                        }
                    } else {
                        // Documento no encontrado, usar imagen predeterminada
                        profileImage.setImageResource(R.drawable.placeholder);
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar errores en la consulta
                    e.printStackTrace();
                    // Usar una imagen predeterminada en caso de error
                    profileImage.setImageResource(R.drawable.placeholder);
                });

        // Recuperar la lista de productos
        ArrayList<Producto> carrito = (ArrayList<Producto>) getIntent().getSerializableExtra("carrito");

        // Recuperar el tamaño de la lista
        int carritoSize = getIntent().getIntExtra("carritoSize", 0);

        // Ahora puedes usar estos valores en tu actividad
        // Por ejemplo, mostrar el nombre del restaurante y el tamaño de la lista en un TextView
        TextView nombreRestauranteTextView = findViewById(R.id.restaurant_name1);
        TextView carritoSizeTextView = findViewById(R.id.products_count);

        nombreRestauranteTextView.setText(nombreRestaurante);
        carritoSizeTextView.setText("Productos - " + carritoSize);

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recycler_carrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener la lista de productos desde el Singleton
        productoList = CarritoSingleton.getInstance().getProductos();

        // Verificar si la lista es nula, si lo es, inicializarla como una lista vacía
        if (productoList == null) {
            productoList = new ArrayList<>();
        }

        // ** Contar los productos distintos y actualizar el TextView **
        TextView productsCountTextView = findViewById(R.id.products_count); // Asegúrate de que este ID coincide con tu XML
        int productosDistintos = productoList.size(); // Esto cuenta cuántos productos distintos hay
        productsCountTextView.setText("Productos - " + productosDistintos); // Actualiza el TextView

        // Configurar el adaptador con la lista de productos recibida
        adapter = new ProductoCarritoAdapter(productoList, this);
        recyclerView.setAdapter(adapter);

        // Configurar la flecha de retroceso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la vista de Realizar Pedido
                Intent intent = new Intent(VerMasProductosClienteActivity.this, RealizarPedidoActivity.class);
                finish(); // Finaliza la actividad actual para no volver a ella con el botón de retroceso
            }
        });

    }

    @Override
    public void onProductUpdated() {
        // Este método se llama cuando el adaptador notifica un cambio en los productos.
        // Aquí puedes actualizar cualquier lógica de UI, como el subtotal o refrescar el RecyclerView si es necesario.

        // Actualizar el adaptador en caso de que necesites reflejar los cambios en la interfaz
        adapter.notifyDataSetChanged();
    }
}
