package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Categoria;
import com.example.proyecto_iot.cliente.RecyclerView.CategoriaAdapter;
import com.example.proyecto_iot.cliente.RecyclerView.OrderAdapter;
import com.example.proyecto_iot.cliente.RecyclerView.Pedido;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoAdapter;
import com.example.proyecto_iot.cliente.RecyclerView.Restaurante;
import com.example.proyecto_iot.cliente.RecyclerView.RestauranteAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class InicioClienteActivity extends AppCompatActivity {
    private RecyclerView recyclerBestOption;
    private RestauranteAdapter bestOptionAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerCategories;
    private RecyclerView recyclerFavoritos;
    private RestauranteAdapter popularesAdapter;
    private RestauranteAdapter favoritosAdapter;

    private RecyclerView recyclerPopulares;
    private com.example.proyecto_iot.cliente.RecyclerView.CategoriaAdapter categoryAdapter;


    private List<Restaurante> bestOptionList;
    private List<Restaurante> popularesList;
    private List<Restaurante> favoritosList;

    private RecyclerView recyclerOrders;
    private OrderAdapter orderAdapter;
    private List<Pedido> ordersList;

    private EditText orderSearch; // El cuadro de búsqueda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_cliente);

        verificarEstadoUsuario();

        // Enlazar el EditText de búsqueda
        orderSearch = findViewById(R.id.order_search);
        db = FirebaseFirestore.getInstance();
        // Configurar un listener para que, al hacer clic, abra la actividad de búsqueda
        orderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la actividad de búsqueda
                Intent intent = new Intent(InicioClienteActivity.this, SearchRestaurantesActivity.class);
                startActivity(intent);
            }
        });

        // Configurar RecyclerView
        recyclerCategories = findViewById(R.id.recycler_categories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Obtener la lista de categorías hardcodeadas
        List<Categoria> categoryList = getCategoryList();
        // Configurar el adaptador con la lista de categorías
        categoryAdapter = new CategoriaAdapter(categoryList, this);
        recyclerCategories.setAdapter(categoryAdapter);




        recyclerOrders = findViewById(R.id.recycler_orders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ordersList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, ordersList);
        recyclerOrders.setAdapter(orderAdapter);
        fetchOrdersForCurrentUser();

        // Inicializar RecyclerView de favoritos
        recyclerFavoritos = findViewById(R.id.recycler_favoritos);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerFavoritos.setLayoutManager(layoutManager1);
        favoritosList = new ArrayList<>();
        favoritosAdapter = new RestauranteAdapter(this, favoritosList);
        recyclerFavoritos.setAdapter(favoritosAdapter);
        fetchFavoritosParaClienteLogueado();

        // Suponiendo que ya tienes el usuario logueado con Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid(); // Obtener el ID del usuario logueado
            // Obtener referencia a la colección de clientes
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("clientes").document(userId);

            // Obtener los datos del cliente desde Firestore
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtener los datos del cliente
                        String direccion = document.getString("Direccion");
                        String fotoUrl = document.getString("FotoURL");

                        // Actualizar el TextView con la dirección
                        TextView direccionTextView = findViewById(R.id.direccion);
                        direccionTextView.setText(direccion);

                        // Actualizar el ImageView con la foto de perfil
                        ImageView iconoPerfilImageView = findViewById(R.id.icono_perfil);
                        Glide.with(this)
                                .load(fotoUrl)
                                .into(iconoPerfilImageView);  // Usando Glide para cargar la imagen
                    }
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            });
        }
        ImageView iconoPerfil = findViewById(R.id.icono_perfil);
        iconoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el intent para navegar a MenuClienteActivity
                Intent intent = new Intent(InicioClienteActivity.this, MenuClienteActivity.class);
                startActivity(intent);
            }
        });





        // Inicializar RecyclerView de populares
        recyclerPopulares = findViewById(R.id.recycler_populares);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerPopulares.setLayoutManager(layoutManager2);
        popularesList = new ArrayList<>();
        popularesAdapter = new RestauranteAdapter(this, popularesList);
        recyclerPopulares.setAdapter(popularesAdapter);
        fetchPopularesFromFirebase();

        // Cargar productos desde Firebase
        // Inicializar RecyclerView
        recyclerBestOption = findViewById(R.id.recycler_best_option);
        recyclerBestOption.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bestOptionList = new ArrayList<>();
        bestOptionAdapter = new RestauranteAdapter(this, bestOptionList);
        recyclerBestOption.setAdapter(bestOptionAdapter);
        fetchBestOptionsFromFirebase();

        // Inicializar BottomNavigationView

        // Marcar el ítem de "Restaurantes" como seleccionado

    }


    // Método para obtener los restaurantes desde Firebase que esten abiertos
    private void fetchBestOptionsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurantes").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        bestOptionList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String restauranteId = document.getId(); // Obtener el ID del documento
                            String nombre = document.getString("nombre");
                            double precioDelivery = document.getDouble("precioDelivery");
                            String tipoDeComida = document.getString("tipoDeComida");
                            String ubicacion = document.getString("ubicacion");
                            String fotoPortada = document.getString("fotoPortada");
                            String fotoLogo = document.getString("fotoLogo");
                            Boolean open = document.getBoolean("open");

                            if (open == true){
                                bestOptionList.add(new Restaurante(restauranteId, nombre, precioDelivery, tipoDeComida, ubicacion,fotoPortada, fotoLogo,0, open));
                            }
                        }
                        bestOptionAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error al obtener los datos", task.getException());
                    }
                });
    }

    // Método para obtener los restaurantes desde Firebase con cantidad de ventas >= 50 y que estén abiertos
    private void fetchPopularesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurantes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                popularesList.clear(); // Limpiar la lista antes de agregar nuevos elementos
                for (DocumentSnapshot document : task.getResult()) {
                    try {
                        String restauranteId = document.getId(); // Obtener el ID del documento
                        String nombre = document.contains("nombre") ? document.getString("nombre") : "Nombre no disponible";
                        double precioDelivery = document.contains("precioDelivery") ? document.getDouble("precioDelivery") : 0.0;
                        String tipoDeComida = document.contains("tipoDeComida") ? document.getString("tipoDeComida") : "Desconocido";
                        String ubicacion = document.contains("ubicacion") ? document.getString("ubicacion") : "Ubicación no disponible";
                        String fotoPortada = document.contains("fotoPortada") ? document.getString("fotoPortada") : null;
                        String fotoLogo = document.contains("fotoLogo") ? document.getString("fotoLogo") : null;
                        int ventas = document.contains("ventas") ? document.getLong("ventas").intValue() : 0;
                        Boolean open = document.contains("open") ? document.getBoolean("open") : false;

                        Log.d("FirestoreData", "Procesando restaurante: " + nombre);

                        // Filtrar restaurantes con ventas >= 50 y abiertos
                        if (ventas >= 50 && Boolean.TRUE.equals(open)) {
                            popularesList.add(new Restaurante(
                                    restauranteId,
                                    nombre,
                                    precioDelivery,
                                    tipoDeComida,
                                    ubicacion,
                                    fotoPortada,
                                    fotoLogo,
                                    ventas,
                                    open
                            ));
                        }
                    } catch (Exception e) {
                        Log.e("Firestore", "Error procesando documento: " + document.getId(), e);
                    }
                }
                popularesAdapter.notifyDataSetChanged(); // Notificar cambios al adaptador
            } else {
                Log.e("Firestore", "Error al obtener los datos", task.getException());
            }
        });
    }
    private void fetchFavoritosFromFirebase(String clienteId) {
        db.collection("clientes").document(clienteId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();

                        // Obtener lista de IDs de favoritos
                        List<String> favoritosIds = (List<String>) document.get("favoritos");
                        if (favoritosIds != null && !favoritosIds.isEmpty()) {
                            // Mostrar la sección de favoritos
                            findViewById(R.id.favorite_title).setVisibility(View.VISIBLE);
                            findViewById(R.id.recycler_favoritos).setVisibility(View.VISIBLE);

                            favoritosList.clear(); // Limpiar lista
                            int[] procesados = {0}; // Contador para procesar todos los IDs

                            for (String restauranteId : favoritosIds) {
                                if (restauranteId == null || restauranteId.trim().isEmpty()) {
                                    Log.e("Favoritos", "Se encontró un ID inválido.");
                                    procesados[0]++;
                                    continue; // Ignorar IDs nulos o vacíos
                                }

                                // Obtener datos del restaurante por ID
                                db.collection("restaurantes").document(restauranteId).get()
                                        .addOnCompleteListener(restauranteTask -> {
                                            procesados[0]++;
                                            if (restauranteTask.isSuccessful() && restauranteTask.getResult() != null) {
                                                DocumentSnapshot restauranteDoc = restauranteTask.getResult();

                                                // Crear objeto Restaurante
                                                String nombre = restauranteDoc.getString("nombre");
                                                double precioDelivery = restauranteDoc.contains("precioDelivery") && restauranteDoc.getDouble("precioDelivery") != null
                                                        ? restauranteDoc.getDouble("precioDelivery")
                                                        : 0.0;
                                                String tipoDeComida = restauranteDoc.getString("tipoDeComida");
                                                String ubicacion = restauranteDoc.getString("ubicacion");
                                                String fotoPortada = restauranteDoc.getString("fotoPortada");
                                                String fotoLogo = restauranteDoc.getString("fotoLogo");
                                                Boolean open = restauranteDoc.getBoolean("open");

                                                // Agregar restaurante a la lista solo si tiene un ID válido
                                                if (restauranteId != null && !restauranteId.isEmpty() && nombre != null) {
                                                    favoritosList.add(new Restaurante(
                                                            restauranteId, nombre, precioDelivery, tipoDeComida, ubicacion, fotoPortada, fotoLogo, 0, open
                                                    ));
                                                }
                                            } else {
                                                Log.e("Favoritos", "No se pudo obtener datos del restaurante con ID: " + restauranteId);
                                            }

                                            // Actualizar adaptador una vez que se procesen todos los IDs
                                            if (procesados[0] == favoritosIds.size()) {
                                                favoritosAdapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        } else {
                            // Ocultar sección si no hay favoritos
                            findViewById(R.id.favorite_title).setVisibility(View.GONE);
                            findViewById(R.id.recycler_favoritos).setVisibility(View.GONE);
                            Log.d("Favoritos", "El cliente no tiene restaurantes favoritos.");
                        }
                    } else {
                        Log.e("Firestore", "Error al obtener cliente", task.getException());
                    }
                });
    }


    // Obtener ID del cliente logueado
    private void fetchFavoritosParaClienteLogueado() {
        String clienteId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (clienteId != null) {
            fetchFavoritosFromFirebase(clienteId);
        } else {
            Log.e("Auth", "Cliente no logueado.");
        }
    }

    private void fetchOrdersForCurrentUser() {
        String clienteId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("pedidos")
                .whereEqualTo("idCliente", clienteId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ordersList.clear(); // Limpiar la lista antes de llenarla
                        long currentTime = System.currentTimeMillis(); // Tiempo actual en milisegundos

                        for (DocumentSnapshot document : task.getResult()) {
                            // Validar estado del pedido
                            int estado = document.contains("estado") ? document.getLong("estado").intValue() : -1;
                            String fechaHora = document.getString("fechaHora");

                            // Validar y obtener otros campos
                            String idPedido = document.getId(); // ID del pedido
                            String idRestaurante = document.getString("idRestaurante");
                            String nombreRestaurante = document.getString("nombreRestaurante");
                            String direccion = document.getString("direccion");
                            double pagoTotal = document.contains("pagoTotal") ? document.getDouble("pagoTotal") : 0.0;

                            // Obtener productos de la lista
                            List<HashMap<String, Object>> productosData = (List<HashMap<String, Object>>) document.get("productos");
                            List<Producto> productos = new ArrayList<>();
                            if (productosData != null) {
                                for (HashMap<String, Object> productoData : productosData) {
                                    String id = (String) productoData.get("id");
                                    String descripcion = (String) productoData.get("descripcion");
                                    int cantidad = ((Long) productoData.get("cantidad")).intValue();
                                    productos.add(new Producto(id, descripcion, cantidad));
                                }
                            }

                            // Manejar estados 2 y 3
                            if (estado == 2 || estado == 3 || estado==7) {
                                ordersList.add(new Pedido(idPedido, idRestaurante, nombreRestaurante, estado, fechaHora, direccion, pagoTotal, productos));
                            }

                            // Manejar estado 4 (entregado con un tiempo límite de 2 minutos)
                            if (estado == 4 && fechaHora != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                                    Date orderDate = sdf.parse(fechaHora);

                                    if (orderDate != null) {
                                        long orderTime = orderDate.getTime();
                                        long timeDiff = currentTime - orderTime;

                                        // Si han pasado menos de 2 minutos, agregar a la lista
                                        if (timeDiff <= 2 * 60 * 1000) { // 2 minutos en milisegundos
                                            ordersList.add(new Pedido(idPedido, idRestaurante, nombreRestaurante, estado, fechaHora, direccion, pagoTotal, productos));
                                        } else {
                                            // Si han pasado más de 2 minutos, eliminar de Firestore
                                            db.collection("pedidos").document(document.getId()).delete()
                                                    .addOnSuccessListener(aVoid -> Log.d("Pedidos", "Pedido eliminado con éxito"))
                                                    .addOnFailureListener(e -> Log.e("Pedidos", "Error al eliminar pedido", e));
                                        }
                                    }
                                } catch (ParseException e) {
                                    Log.e("Fecha", "Error al parsear la fecha: " + fechaHora, e);
                                }
                            }
                        }

                        // Actualizar la interfaz
                        if (!ordersList.isEmpty()) {
                            findViewById(R.id.orders_title).setVisibility(View.VISIBLE);
                            recyclerOrders.setVisibility(View.VISIBLE);
                            orderAdapter.notifyDataSetChanged();
                        } else {
                            findViewById(R.id.orders_title).setVisibility(View.GONE);
                            recyclerOrders.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("Firestore", "Error al obtener pedidos: ", task.getException());
                    }
                });
    }

    private List<Categoria> getCategoryList() {
        List<Categoria> categoryList = new ArrayList<>();
        categoryList.add(new Categoria("Pollo", R.drawable.chicken));
        categoryList.add(new Categoria("Carnes", R.drawable.carne));
        categoryList.add(new Categoria("Hamburguesas", R.drawable.hamburguesa));
        categoryList.add(new Categoria("Pizzas", R.drawable.pizza));
        categoryList.add(new Categoria("Pastas", R.drawable.pasta));
        categoryList.add(new Categoria("Sushi", R.drawable.makis));
        categoryList.add(new Categoria("Postres", R.drawable.postre));
        categoryList.add(new Categoria("Helados", R.drawable.helados));
        categoryList.add(new Categoria("Jugos", R.drawable.jugo));
        categoryList.add(new Categoria("Bowls", R.drawable.bowls));
        return categoryList;
    }

    private void verificarEstadoUsuario() {
        // Obtener instancia de FirebaseAuth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser usuarioActual = firebaseAuth.getCurrentUser();

        if (usuarioActual != null) {
            String uid = usuarioActual.getUid();

            // Referencia a Firestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Buscar el documento del usuario por UID
            firestore.collection("clientes").document(uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Boolean habilitado = document.getBoolean("habilitado");
                                if (!habilitado) {
                                    // Desloguear al usuario y redirigir al LoginActivity
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    Toast.makeText(this, "Su cuenta está inhabilitada. Contáctese con soporte.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(this, "El documento no existe.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Error al verificar el estado del usuario.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Si no hay un usuario logueado, redirigir al LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

}




