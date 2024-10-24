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

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoCarritoAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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
                startActivity(intent);
                finish(); // Finaliza la actividad actual para no volver a ella con el botón de retroceso
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_restaurantes) {
                    startActivity(new Intent(VerMasProductosClienteActivity.this, InicioClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_carrito) {
                    startActivity(new Intent(VerMasProductosClienteActivity.this, CarritoClienteActivity.class));
                    return true;
                } else if (id == R.id.navigation_ordenes) {
                    startActivity(new Intent(VerMasProductosClienteActivity.this, HistorialPedidosActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(VerMasProductosClienteActivity.this, PerfilClienteActivity.class));
                    return true;
                }

                return false;
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
