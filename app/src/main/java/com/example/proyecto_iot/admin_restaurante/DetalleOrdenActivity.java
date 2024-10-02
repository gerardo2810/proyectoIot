package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class DetalleOrdenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_detalle_orden);

        // Obtener los datos del Intent
        String estado = getIntent().getStringExtra("estado");
        String idPedido = getIntent().getStringExtra("orderId");
        String date = getIntent().getStringExtra("direccion");
        String cliente = getIntent().getStringExtra("cliente");
        String direccion = getIntent().getStringExtra("direccion");
        String precio = getIntent().getStringExtra("direccion");
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
        tvPrecio.setText(precio);
        tvRepartidor.setText(repartidor);
    }
}
