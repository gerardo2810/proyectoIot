package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.UsuarioAdapterSA;
import com.example.proyecto_iot.superadmin.RecyclerView.UsuarioSA;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class lista_usuarios_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewListaUsuarios;
    private UsuarioAdapterSA adapter;
    private List<UsuarioSA> listaUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_lista_usuarios);

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(lista_usuarios_superadmin.this, gestion_usuarios_superadmin.class);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion de spinners
        Spinner spinner1 = findViewById(R.id.spinnerRol);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_roles, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        Spinner spinner2 = findViewById(R.id.spinnerEstado);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_estados, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        //----------------------------------------------------------------------------

        //Gestion del Recycler View
        recyclerViewListaUsuarios = findViewById(R.id.recyclerViewListaUsuariosSA);
        recyclerViewListaUsuarios.setLayoutManager(new LinearLayoutManager(this));

        listaUsuarios = new ArrayList<>();
        listaUsuarios.add(new UsuarioSA("Ana", "Armas", "Administrador", "Activo"));
        listaUsuarios.add(new UsuarioSA("Benito", "Bueno", "Repartidor", "Inactivo"));
        listaUsuarios.add(new UsuarioSA("Carlos", "Carrion", "Cliente", "Activo"));
        listaUsuarios.add(new UsuarioSA("Daniela", "Delgado", "Administrador", "Activo"));
        listaUsuarios.add(new UsuarioSA("Eduardo", "Esquivel", "Repartidor", "Inactivo"));
        listaUsuarios.add(new UsuarioSA("Francisco", "Fernandez", "Cliente", "Activo"));
        listaUsuarios.add(new UsuarioSA("Gabriela", "Garcia", "Administrador", "Inactivo"));
        listaUsuarios.add(new UsuarioSA("Hector", "Hidalgo", "Repartidor", "Activo"));
        listaUsuarios.add(new UsuarioSA("Irene", "Iglesias", "Cliente", "Inactivo"));
        listaUsuarios.add(new UsuarioSA("Jorge", "Juarez", "Administrador", "Activo"));
        listaUsuarios.add(new UsuarioSA("Karla", "Krause", "Repartidor", "Inactivo"));
        listaUsuarios.add(new UsuarioSA("Luis", "Lopez", "Cliente", "Activo"));
        listaUsuarios.add(new UsuarioSA("Mariana", "Mendoza", "Administrador", "Inactivo"));
        listaUsuarios.add(new UsuarioSA("Nicolas", "Navarro", "Repartidor", "Activo"));
        listaUsuarios.add(new UsuarioSA("Oscar", "Ortega", "Cliente", "Inactivo"));
        listaUsuarios.add(new UsuarioSA("Patricia", "Perez", "Administrador", "Activo"));
        listaUsuarios.add(new UsuarioSA("Raul", "Ramirez", "Repartidor", "Inactivo"));
        listaUsuarios.add(new UsuarioSA("Silvia", "Sanchez", "Cliente", "Activo"));
        listaUsuarios.add(new UsuarioSA("Tomas", "Torres", "Administrador", "Inactivo"));
        listaUsuarios.add(new UsuarioSA("Ulises", "Uribe", "Repartidor", "Activo"));

        adapter = new UsuarioAdapterSA(listaUsuarios);
        recyclerViewListaUsuarios.setAdapter(adapter);
        //----------------------------------------------------------------------------

        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_usuarios);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_usuarios) {
                    intent = new Intent(lista_usuarios_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(lista_usuarios_superadmin.this, gestion_reportes_superadmin.class);
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
    }
}