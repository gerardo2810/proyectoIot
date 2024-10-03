package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.HistorialPedidosAdapter;
import com.example.proyecto_iot.cliente.RecyclerView.Pedido;
import com.example.proyecto_iot.cliente.RecyclerView.SpaceItemDecoration; // Asegúrate de importar esta clase

import java.util.ArrayList;
import java.util.List;

public class HistorialPedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistorialPedidos;
    private HistorialPedidosAdapter adapter;
    private List<Pedido> listaPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pedidos_cliente);

        // Inicializar RecyclerView
        recyclerViewHistorialPedidos = findViewById(R.id.recyclerViewHistorialPedidos);
        recyclerViewHistorialPedidos.setLayoutManager(new LinearLayoutManager(this));

        // Configurar ItemDecoration para reducir espacio entre los elementos
        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_space);
        recyclerViewHistorialPedidos.addItemDecoration(new SpaceItemDecoration(spaceInPixels));

        // Crear lista de pedidos (aquí puedes obtener los datos de una fuente real)
        listaPedidos = new ArrayList<>();
        listaPedidos.add(new Pedido("Papa John's", "Entregado", "01/09", 1));
        listaPedidos.add(new Pedido("Fridays", "Entregado", "21/07", 2));
        listaPedidos.add(new Pedido("Fridays", "Entregado", "21/07", 3));
        listaPedidos.add(new Pedido("Fridays", "Entregado", "21/07", 4));
        listaPedidos.add(new Pedido("Fridays", "Entregado", "21/07", 5));

        // Configurar Adapter
        adapter = new HistorialPedidosAdapter(listaPedidos, this);
        recyclerViewHistorialPedidos.setAdapter(adapter);

        // Configurar la flecha de retroceso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la vista de Carrito
                Intent intent = new Intent(HistorialPedidosActivity.this, CarritoClienteActivity.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para no volver a ella con el botón de retroceso
            }
        });
    }
}
