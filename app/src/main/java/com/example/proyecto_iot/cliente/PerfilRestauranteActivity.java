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

        // Inicializar Firestore
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

        String restauranteName = intent.getStringExtra("restauranteName"); // Para mostrar nombre si es necesario

// Crear un listener para el clic en el icono de favorito
        favoriteIcon.setOnClickListener(v -> {
            // Obtener el usuario logueado en Firebase
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Obtener la referencia del documento de usuario en la colección 'clientes'
                DocumentReference userRef = db.collection("clientes").document(userId);

                // Consultar si el restaurante ya está en la lista de favoritos
                userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtener la lista de favoritos del usuario
                        List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");

                        if (favoritos != null && favoritos.contains(restauranteId)) {
                            // Si ya está en favoritos, eliminarlo
                            userRef.update("favoritos", FieldValue.arrayRemove(restauranteId))
                                    .addOnSuccessListener(aVoid -> {
                                        // Aquí puedes mostrar un mensaje si la operación es exitosa
                                        Toast.makeText(this, restauranteName + " eliminado de favoritos", Toast.LENGTH_SHORT).show();

                                        // Animación para eliminar del favorito
                                        favoriteIcon.setImageResource(R.drawable.baseline_heart_broken_24); // Cambio a icono vacío
                                        animateFavoriteIcon(favoriteIcon, false); // Animación de desfavoritar

                                        // Puedes también mostrar un mensaje de animación de corazones si es necesario
                                        showFloatingHearts(false);
                                    })
                                    .addOnFailureListener(e -> {
                                        // En caso de error
                                        Toast.makeText(this, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Si no está en favoritos, agregarlo
                            userRef.update("favoritos", FieldValue.arrayUnion(restauranteId))
                                    .addOnSuccessListener(aVoid -> {
                                        // Aquí puedes mostrar un mensaje si la operación es exitosa
                                        Toast.makeText(this, restauranteName + " agregado a favoritos", Toast.LENGTH_SHORT).show();

                                        // Animación de cambio de color con efecto de zoom
                                        favoriteIcon.setImageResource(R.drawable.baseline_favorite_24); // Icono lleno
                                        animateFavoriteIcon(favoriteIcon, true); // Animación de favorito

                                        // Mostrar corazones flotando en pantalla por 3 segundos
                                        showFloatingHearts(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        // En caso de error
                                        Toast.makeText(this, "Error al agregar restaurante a favoritos", Toast.LENGTH_SHORT).show();
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
        // Consultar los platos que pertenecen a las categorías
        db.collection("platos")
                .whereIn("idCategoria", categoriaIds) // Filtrar por las categorías obtenidas
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        productosList.clear();

                        for (DocumentSnapshot document : task.getResult()) {
                            String id =document.getId();
                            String nombre = document.getString("Nombre");
                            String descripcion = document.getString("Descripcion");
                            double precio = document.contains("Precio") ? document.getDouble("Precio") : 0.0;
                            String imageUrl = document.getString("Imagen");
                            int cantidadInicial = 1;

                            productosList.add(new Producto(id,nombre, descripcion, precio, cantidadInicial, imageUrl));
                        }

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
        // Animación de zoom
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(icon, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(icon, "scaleY", 1f, 1.5f, 1f);

        // Cambiar el color del icono dependiendo de si es favorito o no
        if (isFavorited) {
            icon.setImageTintList(ColorStateList.valueOf(Color.RED)); // Cambiar a color rojo cuando es favorito
        } else {
            icon.setImageTintList(ColorStateList.valueOf(Color.WHITE)); // Cambiar a blanco cuando no es favorito
        }

        // Duración total de la animación
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnim, scaleYAnim);
        animatorSet.setDuration(300); // Duración de la animación
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