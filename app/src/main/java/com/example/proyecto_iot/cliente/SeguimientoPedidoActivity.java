package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

    // Variables para el seguimiento de estados
    private LinearLayout layoutEstados;
    private Handler handler;
    private Runnable runnable;
    private int estadoActual = 0;
    private int interval = 2 * 60 * 100; // 2 minutos en milisegundos
    private String[] estados = {"Recibido", "En preparación", "En camino", "Entregado"};
    private int[] imagenesEstados = {R.drawable.placeholder, R.drawable.reportes, R.drawable.reportes_1, R.drawable.repartidor_superadmin};
    private int[] imagenesRevisado = {R.drawable.baseline_check_circle_outline_24, R.drawable.baseline_check_circle_outline_24, R.drawable.baseline_check_circle_outline_24};  // Imágenes revisadas para cada estado

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
        qrButton = findViewById(R.id.qr_button);

        // Enlazar el layout donde se mostrarán los estados
        layoutEstados = findViewById(R.id.layout_estados);  // Agregar en tu XML un LinearLayout con este ID para los estados

        // Iniciar la actualización de los estados cada 2 minutos
        iniciarTrackingEstados();

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

    private void iniciarTrackingEstados() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (estadoActual < estados.length) {
                    actualizarEstado();
                    handler.postDelayed(this, interval); // Repetir cada 2 minutos
                }
            }
        };
        handler.post(runnable); // Iniciar el proceso
    }

    private void actualizarEstado() {
        // Cambiar la imagen del estado anterior a "check" (si no es el primer estado)
        if (estadoActual > 0) {
            if (estadoActual == 1) {
                // Reemplazar la imagen del estado "Recibido" por la de "check"
                ImageView estadoRecibido = findViewById(R.id.estado_recibido);
                estadoRecibido.setImageResource(R.drawable.baseline_check_circle_outline_24);
            } else if (estadoActual == 2) {
                // Reemplazar la imagen del estado "En preparación" por la de "check"
                ImageView estadoPreparacion = findViewById(R.id.estado_preparacion);
                estadoPreparacion.setImageResource(R.drawable.baseline_check_circle_outline_24);
            } else if (estadoActual == 3) {
                // Reemplazar la imagen del estado "En camino" por la de "check"
                ImageView estadoEnCamino = findViewById(R.id.estado_en_camino);
                estadoEnCamino.setImageResource(R.drawable.baseline_check_circle_outline_24);
            }
        }

        // Mostrar el siguiente estado y la línea correspondiente
        if (estadoActual == 0) {
            // Mostrar la línea 1 y el estado "En preparación" después de 2 minutos
            findViewById(R.id.linea_1).setVisibility(View.VISIBLE);
            findViewById(R.id.contenedor_preparacion).setVisibility(View.VISIBLE);
        } else if (estadoActual == 1) {
            // Mostrar la línea 2 y el estado "En camino" después de 2 minutos
            findViewById(R.id.linea_2).setVisibility(View.VISIBLE);
            findViewById(R.id.contenedor_camino).setVisibility(View.VISIBLE);
        } else if (estadoActual == 2) {
            // Mostrar la línea 3 y el estado "Entregado" después de 2 minutos
            findViewById(R.id.linea_3).setVisibility(View.VISIBLE);
            findViewById(R.id.contenedor_entregado).setVisibility(View.VISIBLE);
        }

        // Incrementar el estado actual para la próxima ejecución
        estadoActual++;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Detener el handler cuando la actividad se destruye
    }
}
