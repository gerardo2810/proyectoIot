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

import java.util.ArrayList;
import java.util.List;

public class lista_usuarios_superadmin extends AppCompatActivity {

    private Spinner spinnerRol, spinnerEstado;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewListaUsuarios;
    private UsuarioAdapterSA adapter;
    private List<UsuarioSA> listaUsuariosOriginal;
    private List<UsuarioSA> listaUsuariosFiltrada;

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

        //Gestion del Recycler View
        recyclerViewListaUsuarios = findViewById(R.id.recyclerViewListaUsuariosSA);
        recyclerViewListaUsuarios.setLayoutManager(new LinearLayoutManager(this));

        listaUsuariosOriginal = cargarUsuarios();
        listaUsuariosFiltrada = new ArrayList<>(listaUsuariosOriginal);

        adapter = new UsuarioAdapterSA(listaUsuariosFiltrada);
        recyclerViewListaUsuarios.setAdapter(adapter);
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
                String rolSeleccionado = parent.getItemAtPosition(position).toString();
                Log.d("Spinner", "Rol seleccionado: " + rolSeleccionado); // Agregar este log
                filtrarUsuarios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay nada seleccionado
            }
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
                String estadoSeleccionado = parent.getItemAtPosition(position).toString();
                Log.d("Spinner", "Estado seleccionado: " + estadoSeleccionado);
                filtrarUsuarios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay nada seleccionado
            }
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

    // Método para cargar usuarios (simulado)
    private List<UsuarioSA> cargarUsuarios() {

        List<UsuarioSA> listaUsuarios = new ArrayList<>();

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

        return listaUsuarios;
    }

    // Método para filtrar usuarios según el rol seleccionado
    private void filtrarUsuarios() {
        listaUsuariosFiltrada.clear();

        String rolSeleccionado = spinnerRol.getSelectedItem().toString();
        String estadoSeleccionado = spinnerEstado.getSelectedItem().toString();

        if (rolSeleccionado.equals("-Seleccionar-") && estadoSeleccionado.equals("-Seleccionar-")) {
            listaUsuariosFiltrada.addAll(listaUsuariosOriginal);
        } else if (!rolSeleccionado.equals("-Seleccionar-") && estadoSeleccionado.equals("-Seleccionar-")){
            if (rolSeleccionado.equals("Administradores")) {
                for (UsuarioSA usuario : listaUsuariosOriginal) {
                    if (usuario.getRol().equals("Administrador")) {
                        listaUsuariosFiltrada.add(usuario);
                    }
                }
            } else if (rolSeleccionado.equals("Repartidores")) {
                for (UsuarioSA usuario : listaUsuariosOriginal) {
                    if (usuario.getRol().equals("Repartidor")) {
                        listaUsuariosFiltrada.add(usuario);
                    }
                }
            } else if (rolSeleccionado.equals("Clientes")) {
                for (UsuarioSA usuario : listaUsuariosOriginal) {
                    if (usuario.getRol().equals("Cliente")) {
                        listaUsuariosFiltrada.add(usuario);
                    }
                }
            }
        } else if (rolSeleccionado.equals("-Seleccionar-") && !estadoSeleccionado.equals("-Seleccionar-")){
            if (estadoSeleccionado.equals("Activo")) {
                for (UsuarioSA usuario : listaUsuariosOriginal) {
                    if (usuario.getEstado().equals("Activo")) {
                        listaUsuariosFiltrada.add(usuario);
                    }
                }
            } else if (estadoSeleccionado.equals("Inactivo")) {
                for (UsuarioSA usuario : listaUsuariosOriginal) {
                    if (usuario.getEstado().equals("Inactivo")) {
                        listaUsuariosFiltrada.add(usuario);
                    }
                }
            }
        } else if (!rolSeleccionado.equals("-Seleccionar-") && !estadoSeleccionado.equals("-Seleccionar-")){
            if (estadoSeleccionado.equals("Activo")) {
                if (rolSeleccionado.equals("Administradores")) {
                    for (UsuarioSA usuario : listaUsuariosOriginal) {
                        if (usuario.getRol().equals("Administrador") && usuario.getEstado().equals("Activo")) {
                            listaUsuariosFiltrada.add(usuario);
                        }
                    }
                } else if (rolSeleccionado.equals("Repartidores")) {
                    for (UsuarioSA usuario : listaUsuariosOriginal) {
                        if (usuario.getRol().equals("Repartidor") && usuario.getEstado().equals("Activo")) {
                            listaUsuariosFiltrada.add(usuario);
                        }
                    }
                } else if (rolSeleccionado.equals("Clientes")) {
                    for (UsuarioSA usuario : listaUsuariosOriginal) {
                        if (usuario.getRol().equals("Cliente") && usuario.getEstado().equals("Activo")) {
                            listaUsuariosFiltrada.add(usuario);
                        }
                    }
                }
            } else if (estadoSeleccionado.equals("Inactivo")) {
                if (rolSeleccionado.equals("Administradores")) {
                    for (UsuarioSA usuario : listaUsuariosOriginal) {
                        if (usuario.getRol().equals("Administrador") && usuario.getEstado().equals("Inactivo")) {
                            listaUsuariosFiltrada.add(usuario);
                        }
                    }
                } else if (rolSeleccionado.equals("Repartidores")) {
                    for (UsuarioSA usuario : listaUsuariosOriginal) {
                        if (usuario.getRol().equals("Repartidor") && usuario.getEstado().equals("Inactivo")) {
                            listaUsuariosFiltrada.add(usuario);
                        }
                    }
                } else if (rolSeleccionado.equals("Clientes")) {
                    for (UsuarioSA usuario : listaUsuariosOriginal) {
                        if (usuario.getRol().equals("Cliente") && usuario.getEstado().equals("Inactivo")) {
                            listaUsuariosFiltrada.add(usuario);
                        }
                    }
                }

            }
        }

        adapter.notifyDataSetChanged();
    }

}