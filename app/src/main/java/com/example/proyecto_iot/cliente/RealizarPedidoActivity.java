package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class RealizarPedidoActivity extends AppCompatActivity {

    private ImageView backArrow;
    private Button payButton;
    private TextView seeMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_realizar_pedido_cliente);

        // Configurar el padding para ajustar a las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de vistas
        backArrow = findViewById(R.id.back_arrow);
        payButton = findViewById(R.id.pay_button);
        seeMore = findViewById(R.id.see_more);

        // Listener para la flecha de retroceso
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Regresar a la vista anterior (puede ser InicioClienteActivity o CarritoClienteActivity)
                Intent intent = new Intent(RealizarPedidoActivity.this, CarritoClienteActivity.class); // O CarritoClienteActivity, según sea el caso
                startActivity(intent);
                finish(); // Finaliza la actividad actual
            }
        });

        // Listener para el botón "Ver más"
        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí podrías navegar a una actividad donde muestres más detalles de los productos
                Intent intent = new Intent(RealizarPedidoActivity.this, VerMasProductosClienteActivity.class); // Crear esta actividad según tu estructura
                startActivity(intent);
            }
        });

        // Listener para el botón de pagar
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes agregar la lógica de pago
                // Por ejemplo, redirigir a una página de confirmación de pedido o procesamiento de pago
                Intent intent = new Intent(RealizarPedidoActivity.this, SeguimientoPedidoActivity.class); // Crear esta actividad según la estructura de tu app
                startActivity(intent);
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_restaurantes) {
                    startActivity(new Intent(RealizarPedidoActivity.this, InicioClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_carrito) {
                    startActivity(new Intent(RealizarPedidoActivity.this, CarritoClienteActivity.class));
                    return true;
                } else if (id == R.id.navigation_ordenes) {
                    startActivity(new Intent(RealizarPedidoActivity.this, HistorialPedidosActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(RealizarPedidoActivity.this, PerfilClienteActivity.class));
                    return true;
                }

                return false;
            }
        });


    }
}
