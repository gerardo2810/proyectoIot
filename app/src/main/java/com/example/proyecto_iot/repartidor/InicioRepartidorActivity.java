package com.example.proyecto_iot.repartidor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.repartidor.RecyclerView.PedidoRecoger;
import com.example.proyecto_iot.repartidor.RecyclerView.PedidosRecogerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class InicioRepartidorActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewListaPedidosRecoger;
    private List<PedidoRecoger> listaPedidos;
    private PedidosRecogerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_repartidor);

        //----------------------------------------------------------------------------
        Intent intent = getIntent();
        if (intent.getBooleanExtra("showDialog", false)) {
            mostrarAlerta();
        }

        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_home);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_home) {
                    intent = new Intent(InicioRepartidorActivity.this, InicioRepartidorActivity.class);
                } else if (item.getItemId() == R.id.navigation_historial) {
                    intent = new Intent(InicioRepartidorActivity.this, HistorialRepartidorActivity.class);
                }else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(InicioRepartidorActivity.this, PerfilRepartidorActivity.class);
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
        //Gestion del Recycler View
        recyclerViewListaPedidosRecoger = findViewById(R.id.recyclerViewListaPedidosRecoger);
        recyclerViewListaPedidosRecoger.setLayoutManager(new LinearLayoutManager(this));
        listaPedidos = new ArrayList<>();
        listaPedidos.add(new PedidoRecoger("1 pedido a","Av. de los Precursores 281, San Miguel"));
        listaPedidos.add(new PedidoRecoger("1 pedido a","Av. Simon Bolivar 1486, Pueblo libre"));
        listaPedidos.add(new PedidoRecoger("1 pedido a","Av. Universitaria 456, San Miguel"));
        listaPedidos.add(new PedidoRecoger("1 pedido a","Av. Venezuela 789, Cercado de Lima"));
        listaPedidos.add(new PedidoRecoger("1 pedido a","Av. Mariano Cornejo 1434, Pueblo libre"));
        listaPedidos.add(new PedidoRecoger("1 pedido a","Av. La marina 4596, San Miguel"));

        adapter = new PedidosRecogerAdapter(listaPedidos);
        recyclerViewListaPedidosRecoger.setAdapter(adapter);

    }

    public void mostrarAlerta(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Registro Exitoso");
        alertDialog.setMessage("¡Se registró correctamente el pedido!");
        alertDialog.setPositiveButton("Cerrar",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("msgAlerta","Positive");
                    }
                });
        alertDialog.show();
    }
}
