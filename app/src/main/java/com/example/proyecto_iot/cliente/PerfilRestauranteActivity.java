package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class PerfilRestauranteActivity extends AppCompatActivity {

    private int productQuantity = 0;
    private double productPrice = 15.00;
    private TextView textQuantity;
    private TextView productPriceTextView;
    private Button buttonAddProduct;
    private TextView cartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_restaurante_cliente);

        // Listener para el botón de retroceso que regresa a inicio_cliente
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, InicioClienteActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
        });

        // Listener para el carrito en el menú inferior
        ImageView cartIcon = findViewById(R.id.shopping_cart);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, CarritoClienteActivity.class);
                startActivity(intent);
            }
        });

        // Inicializar elementos para el manejo de cantidad del producto
        textQuantity = findViewById(R.id.text_quantity);
        productPriceTextView = findViewById(R.id.product_price);
        buttonAddProduct = findViewById(R.id.button_add_product);
        cartCount = findViewById(R.id.cart_count);

        // Inicializar botones de más y menos
        Button buttonIncrease = findViewById(R.id.button_increase);
        Button buttonDecrease = findViewById(R.id.button_decrease);

        // Inicializar cantidad y ocultar botón de añadir al inicio
        textQuantity.setText(String.valueOf(productQuantity));
        buttonAddProduct.setVisibility(View.GONE); // Ocultar el botón al inicio
        cartCount.setVisibility(View.GONE); // Ocultar el contador del carrito al inicio

        // Listener para aumentar la cantidad
        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productQuantity++;
                updateQuantityAndPrice();
            }
        });

        // Listener para disminuir la cantidad
        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productQuantity > 0) {
                    productQuantity--;
                    updateQuantityAndPrice();
                }
            }
        });

        // Listener para el botón de añadir producto al carrito
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí puedes añadir el producto al carrito
                // Ejemplo: Puedes almacenar el producto en un array o lista de productos seleccionados
            }
        });

        // Listeners para la barra de navegación
        // Icono de restaurantes
        LinearLayout iconoRestaurantes = findViewById(R.id.nav_restaurantes);
        iconoRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, InicioClienteActivity.class);
                startActivity(intent);
            }
        });

        // Icono del carrito en la barra de navegación
        LinearLayout iconoCarritoNav = findViewById(R.id.nav_carrito);
        iconoCarritoNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, CarritoClienteActivity.class);
                startActivity(intent);
            }
        });

        // Icono de perfil en la barra de navegación
        LinearLayout iconoPerfilNav = findViewById(R.id.nav_perfil);
        iconoPerfilNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, PerfilClienteActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Función para actualizar la cantidad y el precio del producto
     */
    private void updateQuantityAndPrice() {
        textQuantity.setText(String.valueOf(productQuantity));

        // Calcular el nuevo precio total
        double totalPrice = productQuantity * productPrice;
        productPriceTextView.setText("S/ " + String.format("%.2f", totalPrice));

        // Mostrar u ocultar el botón de añadir producto
        if (productQuantity > 0) {
            buttonAddProduct.setVisibility(View.VISIBLE);
            buttonAddProduct.setText("Añadir S/ " + String.format("%.2f", totalPrice));
            cartCount.setVisibility(View.VISIBLE);
            cartCount.setText(String.valueOf(productQuantity));
        } else {
            buttonAddProduct.setVisibility(View.GONE);
            cartCount.setVisibility(View.GONE);
        }
    }
}
