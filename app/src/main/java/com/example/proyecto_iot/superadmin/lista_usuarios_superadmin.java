package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class lista_usuarios_superadmin extends AppCompatActivity {

    private Spinner spinnerRol, spinnerEstado;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private UsuarioAdapterSA adapter;
    private List<UsuarioSA> listaUsuarios;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_lista_usuarios);

        db = FirebaseFirestore.getInstance();

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(lista_usuarios_superadmin.this, gestion_usuarios_superadmin.class);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion del Recycler View
        recyclerView = findViewById(R.id.recyclerViewListaUsuariosSA);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaUsuarios = new ArrayList<>();
        adapter = new UsuarioAdapterSA(listaUsuarios, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        //----------------------------------------------------------------------------

        //Gestion de spinner Rol
        spinnerRol = findViewById(R.id.spinnerRol);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_roles, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter1);

        spinnerRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rolSeleccionado = spinnerRol.getSelectedItem().toString();
                if (!rolSeleccionado.equals("-Seleccionar-")) {
                    spinnerEstado.setEnabled(true);
                    cargarUsuariosPorRol(rolSeleccionado);
                } else {
                    spinnerEstado.setEnabled(false);
                    listaUsuarios.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //----------------------------------------------------------------------------

        //Gestion de spinner Estado
        spinnerEstado = findViewById(R.id.spinnerEstado);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_estados, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter2);

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarUsuariosPorEstado(spinnerEstado.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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

    private void cargarUsuariosPorRol(String rol) {
        listaUsuarios.clear();
        String coleccion = rol.toLowerCase();

        db.collection(coleccion)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if(coleccion.equals("administradores") || coleccion.equals("repartidores")){
                            String nombre = doc.getString("nombre") + " " + doc.getString("apellido");
                            String estado = doc.getBoolean("habilitado") ? "Activo" : "Inactivo";
                            //String foto = doc.getString("foto") != null ? doc.getString("foto") : doc.getString("FotoURL");
                            listaUsuarios.add(new UsuarioSA(doc.getId(), nombre, rol, estado));
                        } else {
                            String nombre = doc.getString("Nombre") + " " + doc.getString("Apellido");
                            String estado = doc.getBoolean("habilitado") ? "Activo" : "Inactivo";
                            //String foto = doc.getString("foto") != null ? doc.getString("foto") : doc.getString("FotoURL");
                            listaUsuarios.add(new UsuarioSA(doc.getId(), nombre, rol, estado));
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void filtrarUsuariosPorEstado(String estado) {
        if (estado.equals("-Seleccionar-")) {
            adapter.setUsuarios(listaUsuarios); // Restaurar lista original
        } else {
            List<UsuarioSA> usuariosFiltrados = new ArrayList<>();
            for (UsuarioSA usuario : listaUsuarios) {
                if (usuario.getEstado().equals(estado)) {
                    usuariosFiltrados.add(usuario);
                }
            }
            adapter.setUsuarios(usuariosFiltrados);
        }
    }

}