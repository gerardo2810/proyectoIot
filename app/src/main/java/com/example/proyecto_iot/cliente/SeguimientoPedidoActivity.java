package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SeguimientoPedidoActivity extends AppCompatActivity {

    // Declaración de las variables para las vistas
    private ImageView qrIcon;
    private TextView cancelOrder, payHere, scanQr;
    private LinearLayout qrButton, verificationButton, backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seguimiento_pedido_cliente);

        // Ajustar los márgenes para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Enlazar las vistas con sus respectivos IDs en el layout
        backArrow = findViewById(R.id.header_layout);
        qrIcon = findViewById(R.id.qr_icon);
        cancelOrder = findViewById(R.id.see_more);
        payHere = findViewById(R.id.text_pay_here);
        scanQr = findViewById(R.id.text_scan_qr);

        // LinearLayouts para los botones
        qrButton = findViewById(R.id.qr_button);

        // Listener para la flecha de retroceso - Dirige a "RealizarPedidoActivity"
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeguimientoPedidoActivity.this, RealizarPedidoActivity.class);
                startActivity(intent);
            }
        });

        // Listener para el botón de "Cancelar Órden" - Dirige a "InicioClienteActivity"
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeguimientoPedidoActivity.this, InicioClienteActivity.class);
                startActivity(intent);
            }
        });

        // Listener para el botón de QR - Dirige a "SeguimientoPedidoActivity"
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeguimientoPedidoActivity.this, SeguimientoPedidoActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_restaurantes) {
                    startActivity(new Intent(SeguimientoPedidoActivity.this, InicioClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_carrito) {
                    startActivity(new Intent(SeguimientoPedidoActivity.this, CarritoClienteActivity.class));
                    return true;
                } else if (id == R.id.navigation_ordenes) {
                    startActivity(new Intent(SeguimientoPedidoActivity.this, HistorialPedidosActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(SeguimientoPedidoActivity.this, PerfilClienteActivity.class));
                    return true;
                }

                return false;
            }
        });


    }
}
