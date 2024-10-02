package com.example.proyecto_iot.cliente;

import android.os.Bundle;

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

        // Crear lista de pedidos (aquí puedes obtener los datos de una fuente real)
        listaPedidos = new ArrayList<>();
        listaPedidos.add(new Pedido("Papa John's", "Entregado", "01/09"));
        listaPedidos.add(new Pedido("Fridays", "Entregado", "21/07"));

        // Configurar Adapter
        adapter = new HistorialPedidosAdapter(listaPedidos);
        recyclerViewHistorialPedidos.setAdapter(adapter);
    }
}
