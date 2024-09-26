package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class carrito_cliente extends AppCompatActivity {

    private int productQuantity = 0;
    private double productPrice = 15.00; // Precio unitario del producto
    private TextView textQuantity;
    private TextView productPriceTextView;
    private Button addButton;
    private TextView cartCount;
    private LinearLayout productContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_cliente);

        // Inicializar elementos
        textQuantity = findViewById(R.id.quantity);
        productPriceTextView = findViewById(R.id.product_price);
        addButton = findViewById(R.id.add_button);
        cartCount = findViewById(R.id.cart_count);
        ScrollView scrollView = findViewById(R.id.scroll_products);
        // Si dentro del ScrollView tienes un LinearLayout para agregar productos
        LinearLayout productContainer = scrollView.findViewById(R.id.product_container);


        // Inicializar cantidad y ocultar el botón al inicio
        textQuantity.setText(String.valueOf(productQuantity));
        addButton.setVisibility(View.GONE);
        cartCount.setVisibility(View.GONE);

        // Botones de aumentar y disminuir
        ImageView increaseButton = findViewById(R.id.increase_quantity);
        ImageView decreaseButton = findViewById(R.id.decrease_quantity);
        ImageView deleteProduct = findViewById(R.id.delete_product);
        ImageView shoppingCartIcon = findViewById(R.id.shopping_cart);

        // Listener para aumentar la cantidad
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productQuantity++;
                updateQuantityAndPrice();
            }
        });

        // Listener para disminuir la cantidad
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productQuantity > 0) {
                    productQuantity--;
                    updateQuantityAndPrice();
                }
            }
        });

        // Listener para eliminar un producto
        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Eliminar el producto y actualizar el contador de carrito
                productQuantity = 0;
                updateQuantityAndPrice();
                productContainer.setVisibility(View.GONE); // Esconder producto
            }
        });

        // Listener para vaciar el carrito
        TextView clearCart = findViewById(R.id.clear_cart);
        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Vaciar todos los productos del carrito
                productQuantity = 0;
                updateQuantityAndPrice();
                productContainer.setVisibility(View.GONE);
                cartCount.setVisibility(View.GONE);
            }
        });

        // Listener para el botón de añadir productos
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí puedes agregar la lógica para añadir el producto al sistema del carrito
            }
        });

        LinearLayout navRestaurantes = findViewById(R.id.nav_restaurantes);
        LinearLayout navPerfil = findViewById(R.id.nav_perfil);

        // Listener para navegar a la vista de restaurantes
        navRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(carrito_cliente.this, inicio_cliente.class);
                startActivity(intent);
            }
        });

        // Listener para navegar a la vista del perfil
        navPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(carrito_cliente.this, perfil_cliente.class);
                startActivity(intent);
            }
        });

        // Listener para permanecer en la vista de carrito
        shoppingCartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(carrito_cliente.this, carrito_cliente.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Función para actualizar la cantidad y el precio del producto.
     */
    private void updateQuantityAndPrice() {
        textQuantity.setText(String.valueOf(productQuantity));

        // Calcular el nuevo precio total
        double totalPrice = productQuantity * productPrice;
        productPriceTextView.setText("S/ " + String.format("%.2f", totalPrice));

        // Mostrar u ocultar el botón de añadir producto
        if (productQuantity > 0) {
            addButton.setVisibility(View.VISIBLE);
            addButton.setText("Añadir S/ " + String.format("%.2f", totalPrice));
            cartCount.setVisibility(View.VISIBLE);
            cartCount.setText(String.valueOf(productQuantity));
        } else {
            addButton.setVisibility(View.GONE); // Ocultar si no hay productos
            cartCount.setVisibility(View.GONE); // Ocultar contador del carrito
        }
    }
}
