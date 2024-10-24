package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.proyecto_iot.R;

public class DetalleOrdenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_detalle_orden);

        // Obtener los datos del Intent
        String estado = getIntent().getStringExtra("estado");
        String idPedido = getIntent().getStringExtra("orderId");
        String date = getIntent().getStringExtra("date");
        String cliente = getIntent().getStringExtra("cliente");
        String direccion = getIntent().getStringExtra("direccion");
        String precio = getIntent().getStringExtra("precio");
        String repartidor = getIntent().getStringExtra("repartidor");

        // Obtener referencias a las vistas
        TextView tvIdPedido = findViewById(R.id.tv_idOrder);
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvCliente = findViewById(R.id.tv_cliente);
        TextView tvDireccion = findViewById(R.id.tv_direccion);
        TextView tvEstado = findViewById(R.id.order_status_button);
        TextView tvPrecio = findViewById(R.id.tv_total);
        TextView tvRepartidor = findViewById(R.id.tv_repartidor);

        // Asignar los datos recibidos a las vistas
        tvEstado.setText(estado);
        tvIdPedido.setText(idPedido);
        tvDate.setText(date);
        tvCliente.setText(cliente);
        tvDireccion.setText(direccion);
        tvPrecio.setText("S/ " + precio);
        tvRepartidor.setText(repartidor);


        switch (estado) {
            case "ENTREGADO":
                tvEstado.setBackgroundColor(ContextCompat.getColor(this, R.color.order_delivered));
                break;
            case "EN PREPARACIÓN":
                tvEstado.setBackgroundColor(ContextCompat.getColor(this, R.color.order_in_preparation));
                break;
            case "EN CAMINO":
                tvEstado.setBackgroundColor(ContextCompat.getColor(this, R.color.order_on_the_way));
                break;
            case "EN TIENDA":
                tvEstado.setBackgroundColor(ContextCompat.getColor(this, R.color.order_in_store));
                break;
            default:
                tvEstado.setBackgroundColor(ContextCompat.getColor(this, R.color.default_order_background));
                break;
        }

        ImageView backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Termina esta actividad para volver a la anterior
                finish();
            }
        });
    }
}
