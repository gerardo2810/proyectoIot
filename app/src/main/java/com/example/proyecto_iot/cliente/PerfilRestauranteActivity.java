package com.example.proyecto_iot.cliente;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PerfilRestauranteActivity extends AppCompatActivity {

    private ProductoAdapter adapter;
    private List<Producto> productosList = new ArrayList<>();
    private List<Producto> carritoList = new ArrayList<>();
    private TextView carritoCantidadTextView;
    private ImageView carritoIcon;
    private int totalCantidadCarrito = 0;
    private boolean productosAñadidos = false; // Indicador para saber si se añadieron productos al carrito
    private LinearLayout alertLayout; // Layout de la alerta

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_restaurante_cliente);

        // Inicializar Firesto
        db = FirebaseFirestore.getInstance();

        // Obtener los datos del Intent
        Intent intent = getIntent();
        String nombreRestaurante = intent.getStringExtra("nombre_restaurante");
        String categoriaRestaurante = intent.getStringExtra("categoria_restaurante");
        double precioDelivery = intent.getDoubleExtra("precio_delivery", 0.0);
        String direccionRestaurante = intent.getStringExtra("direccion_restaurante");
        String fotoLogo = intent.getStringExtra("foto_logo");
        String fotoPortada = intent.getStringExtra("foto_portada");
        // Obtener las vistas ImageView por ID
        ImageView coverImageView = findViewById(R.id.cover_image);
        ImageView profileImageView = findViewById(R.id.profile_image);

        // Usar Glide o Picasso para cargar las imágenes en los ImageView desde las URLs
        if (fotoLogo != null && !fotoLogo.isEmpty()) {
            Glide.with(this)
                    .load(fotoLogo)
                    .into(profileImageView);  // Cargar la imagen de portada
        }

        if (fotoPortada != null && !fotoPortada.isEmpty()) {
            Glide.with(this)
                    .load(fotoPortada)
                    .into(coverImageView);  // Cargar la imagen de perfil
        }
        String restauranteId = intent.getStringExtra("restauranteId");
        if (restauranteId == null || restauranteId.isEmpty()) {
            Toast.makeText(this, "Error: Restaurante no válido.", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si el ID es inválido
            return;
        }

        // Obtener la referencia al icono de favorito
        ImageView favoriteIcon = findViewById(R.id.favorite_icon);

        String restauranteName = intent.getStringExtra("nombre_restaurante"); // Para mostrar nombre si es necesario

// Crear un listener para el clic en el icono de favorito
        favoriteIcon.setOnClickListener(v -> {
            // Obtener el usuario logueado en Firebase
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Obtener la referencia del documento del cliente
                DocumentReference userRef = db.collection("clientes").document(userId);

                // Consultar si el restaurante está en favoritos
                userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");

                        if (favoritos != null && favoritos.contains(restauranteId)) {
                            // Eliminar el restaurante de favoritos
                            userRef.update("favoritos", FieldValue.arrayRemove(restauranteId))
                                    .addOnSuccessListener(aVoid -> {
                                        // Actualizar el ícono a corazón con borde
                                        favoriteIcon.setImageResource(R.drawable.corazonborde);
                                        favoriteIcon.setImageTintList(ColorStateList.valueOf(Color.BLACK));
                                        favoriteIcon.setVisibility(View.VISIBLE);

                                        // Animación de desfavoritar
                                        animateFavoriteIcon(favoriteIcon, false);

                                        // Mostrar mensaje de confirmación
                                        Toast.makeText(this, restauranteName + " eliminado de favoritos", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Agregar el restaurante a favoritos
                            userRef.update("favoritos", FieldValue.arrayUnion(restauranteId))
                                    .addOnSuccessListener(aVoid -> {
                                        // Actualizar el ícono a corazón rojo
                                        favoriteIcon.setImageResource(R.drawable.baseline_favorite_24);
                                        favoriteIcon.setImageTintList(ColorStateList.valueOf(Color.RED));
                                        favoriteIcon.setVisibility(View.VISIBLE);

                                        // Animación de favorito
                                        animateFavoriteIcon(favoriteIcon, true);
                                        showFloatingHearts(true);

                                        // Mostrar mensaje de confirmación
                                        Toast.makeText(this, restauranteName + " agregado a favoritos", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al agregar a favoritos", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                });
            }
        });

// Verificar el estado inicial del corazón
        // Estado inicial del ícono
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference userRef = db.collection("clientes").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");

                    if (favoritos != null && favoritos.contains(restauranteId)) {
                        // Restaurante está en favoritos, cambiar ícono a rojo
                        favoriteIcon.setImageResource(R.drawable.baseline_favorite_24);
                        favoriteIcon.setImageTintList(ColorStateList.valueOf(Color.RED));
                    } else {
                        // Restaurante no está en favoritos, mostrar corazón con borde
                        favoriteIcon.setImageResource(R.drawable.corazonborde);
                        favoriteIcon.setImageTintList(null); // Evitar aplicar tintes al borde
                    }
                    favoriteIcon.setVisibility(View.VISIBLE); // Asegurar visibilidad
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al verificar favoritos", Toast.LENGTH_SHORT).show();
            });
        }

// Listener del ícono favorito
        favoriteIcon.setOnClickListener(v -> {
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            if (user1 != null) {
                String userId1 = user1.getUid();
                FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                DocumentReference userRef1 = db1.collection("clientes").document(userId1);

                userRef1.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");

                        if (favoritos != null && favoritos.contains(restauranteId)) {
                            // Eliminar de favoritos
                            userRef1.update("favoritos", FieldValue.arrayRemove(restauranteId))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, restauranteName + " eliminado de favoritos", Toast.LENGTH_SHORT).show();

                                        // Restaurar el ícono sin tintes
                                        favoriteIcon.setImageResource(R.drawable.corazonborde);
                                        favoriteIcon.setImageTintList(null); // Sin tinte
                                        animateFavoriteIcon(favoriteIcon, false);
                                    });
                        } else {
                            // Agregar a favoritos
                            userRef1.update("favoritos", FieldValue.arrayUnion(restauranteId))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, restauranteName + " agregado a favoritos", Toast.LENGTH_SHORT).show();

                                        // Cambiar a corazón rojo
                                        favoriteIcon.setImageResource(R.drawable.baseline_favorite_24);
                                        favoriteIcon.setImageTintList(ColorStateList.valueOf(Color.RED));
                                        animateFavoriteIcon(favoriteIcon, true);
                                        showFloatingHearts(true);
                                    });
                        }
                    }
                });
            }
        });


        System.out.println("perfil resta");
        System.out.println(precioDelivery);

        // Configurar los TextViews con los datos recibidos
        TextView nombreTextView = findViewById(R.id.restaurant_name);
        TextView categoriaTextView = findViewById(R.id.restaurant_category);
        TextView deliveryTextView = findViewById(R.id.delivery_price);
        TextView direccionTextView = findViewById(R.id.restaurant_address);

        nombreTextView.setText(nombreRestaurante);
        categoriaTextView.setText(categoriaRestaurante);
        deliveryTextView.setText(String.format("S/. %.2f", precioDelivery));
        direccionTextView.setText(direccionRestaurante);

        // Configurar el RecyclerView
        RecyclerView recyclerProductos = findViewById(R.id.recycler_perfil_restaurante);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        // Crear el adaptador
        adapter = new ProductoAdapter(this, productosList, this::agregarProductoAlCarrito);
        recyclerProductos.setAdapter(adapter);

        // Inicializar el TextView y el icono del carrito
        carritoCantidadTextView = findViewById(R.id.cart_count);
        carritoIcon = findViewById(R.id.shopping_cart);
        carritoIcon.setVisibility(View.GONE);

        carritoIcon.setOnClickListener(view -> {
            Intent intent1 = new Intent(PerfilRestauranteActivity.this, CarritoClienteActivity.class);

            // Pasar la lista de productos del carrito
            intent1.putExtra("carrito", new ArrayList<>(carritoList));

            // Pasar los datos del restaurante
            intent1.putExtra("nombre_restaurante", nombreRestaurante);
            intent1.putExtra("fotoLogo", fotoLogo);
            intent1.putExtra("precioDelivery", precioDelivery);
            intent1.putExtra("restauranteId", restauranteId); // Pasar el ID del restaurante

            // Iniciar la actividad del carrito
            startActivity(intent1);
        });


        EditText searchText = findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("Search", "Text Changed: " + charSequence.toString());
                adapter.filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        fetchProductosFromFirebase();

        // Inicializar y configurar la alerta
        alertLayout = findViewById(R.id.alert_layout);
        Button btnKeep = findViewById(R.id.btn_keep);
        Button btnDelete = findViewById(R.id.btn_delete);

        // Ocultar la alerta al iniciar
        alertLayout.setVisibility(View.GONE);

        // Configurar el botón "Mantener"
        btnKeep.setOnClickListener(v -> ocultarAlerta());

        // Configurar el botón "Sí, eliminar"
        btnDelete.setOnClickListener(v -> {
            carritoList.clear(); // Limpiar el carrito
            productosAñadidos = false; // Reiniciar el indicador
            navegarAInicioCliente(); // Navegar a la vista de inicio
        });
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(view -> {
            if (productosAñadidos) {
                mostrarAlerta(); // Mostrar la alerta si hay productos añadidos
            } else {
                navegarAInicioCliente(); // Navegar directamente si no se añadieron productos
            }
        });
        ImageView clearSearchIcon = findViewById(R.id.clear_search_icon);

        // Listener para el ícono de basurero
        clearSearchIcon.setOnClickListener(view -> {
            searchText.setText(""); // Borra el texto del EditText
        });


    }
    private void navegarAInicioCliente() {
        Intent intent = new Intent(PerfilRestauranteActivity.this, InicioClienteActivity.class);
        startActivity(intent);
        finish();
    }


    private void fetchProductosFromFirebase() {
        // Obtener el ID del restaurante del Intent
        String restauranteId = getIntent().getStringExtra("restauranteId");

        if (restauranteId == null || restauranteId.isEmpty()) {
            Toast.makeText(this, "Error: Restaurante no válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Paso 1: Obtener las categorías asociadas al restaurante
        db.collection("categorias")
                .whereEqualTo("idRestaurante", restauranteId) // Filtrar por el ID del restaurante
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> categoriaIds = new ArrayList<>();

                        // Extraer los IDs de las categorías
                        for (DocumentSnapshot document : task.getResult()) {
                            categoriaIds.add(document.getId());
                        }

                        if (!categoriaIds.isEmpty()) {
                            // Paso 2: Obtener los platos asociados a las categorías
                            fetchPlatosPorCategorias(categoriaIds);
                        } else {
                            Toast.makeText(this, "No hay platos para este restaurante.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error al cargar categorías.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchPlatosPorCategorias(List<String> categoriaIds) {
        db.collection("platos")
                .whereIn("idCategoria", categoriaIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        productosList.clear();

                        for (DocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String nombre = document.getString("Nombre");
                            String descripcion = document.getString("Descripcion");
                            double precio = document.contains("Precio") ? document.getDouble("Precio") : 0.0;
                            String imageUrl = document.getString("Imagen");
                            int cantidadInicial = 1;

                            productosList.add(new Producto(id, nombre, descripcion, precio, cantidadInicial, imageUrl));
                        }

                        // Actualizar productosListFull para el filtrado
                        adapter.updateProductosListFull(new ArrayList<>(productosList));

                        // Notificar al adaptador de los cambios
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al cargar platos.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void agregarProductoAlCarrito(Producto producto, int cantidad) {
        boolean productoExistente = false;
        for (Producto p : carritoList) {
            if (p.getNombre().equals(producto.getNombre())) {
                p.setCantidad(p.getCantidad() + cantidad);
                productoExistente = true;
                break;
            }
        }

        if (!productoExistente) {
            producto.setCantidad(cantidad);
            carritoList.add(producto);
        }

        totalCantidadCarrito += cantidad;
        productosAñadidos = true; // Indicar que se añadieron productos
        actualizarCarritoCantidad();
    }


    private void actualizarCarritoCantidad() {
        if (totalCantidadCarrito > 0) {
            carritoIcon.setVisibility(View.VISIBLE);
            carritoCantidadTextView.setVisibility(View.VISIBLE);
            carritoCantidadTextView.setText(String.valueOf(totalCantidadCarrito));
        } else {
            carritoIcon.setVisibility(View.GONE);
            carritoCantidadTextView.setVisibility(View.GONE);
        }
    }
    private void mostrarAlerta() {
        alertLayout.setVisibility(View.VISIBLE);
    }

    private void ocultarAlerta() {
        alertLayout.setVisibility(View.GONE);
    }

    // Método para realizar la animación de color y zoom
    private void animateFavoriteIcon(ImageView icon, boolean isFavorited) {
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(icon, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(icon, "scaleY", 1f, 1.5f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnim, scaleYAnim);
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    // Método para mostrar corazones flotantes en pantalla (si se agregó a favoritos)
    private void showFloatingHearts(boolean isFavorited) {
        if (!isFavorited) return; // No mostramos corazones si el restaurante fue eliminado de favoritos

        // Crear un Heart ImageView para la animación
        ImageView heart = new ImageView(this);
        heart.setImageResource(R.drawable.baseline_heart_broken_24); // Usa un drawable de corazón

        // Agregar el ImageView al layout principal
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
        params.leftMargin = new Random().nextInt(500); // Posición aleatoria en el eje X
        params.topMargin = -100; // Inicialmente fuera de la pantalla en el eje Y
        heart.setLayoutParams(params);

        // Agregar el corazón al contenedor
        FrameLayout rootLayout = findViewById(R.id.floating_hearts_container); // Asegúrate de tener un FrameLayout en tu layout principal
        rootLayout.addView(heart);

        // Animación de caída de corazón
        ObjectAnimator fallAnim = ObjectAnimator.ofFloat(heart, "translationY", -100, 1000); // Animación de caída
        fallAnim.setDuration(3000); // 3 segundos de animación
        fallAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        fallAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rootLayout.removeView(heart); // Eliminar el corazón después de la animación
            }
        });

        fallAnim.start();
    }
}