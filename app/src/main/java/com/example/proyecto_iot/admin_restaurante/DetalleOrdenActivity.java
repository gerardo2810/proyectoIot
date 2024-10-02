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
        String idPedido = getIntent().getStringExtra("id_pedido");
        String cliente = getIntent().getStringExtra("cliente");
        String direccion = getIntent().getStringExtra("direccion");
        String estado = getIntent().getStringExtra("estado");
        String detalles = getIntent().getStringExtra("detalles");

        // Obtener referencias a las vistas
        TextView tvIdPedido = findViewById(R.id.tv_id_pedido);
        TextView tvCliente = findViewById(R.id.tv_cliente);
        TextView tvDireccion = findViewById(R.id.tv_direccion);
        TextView tvEstado = findViewById(R.id.tv_estado);
        TextView tvDetalles = findViewById(R.id.tv_detalles);

        // Asignar los datos recibidos a las vistas
        tvIdPedido.setText(idPedido);
        tvCliente.setText(cliente);
        tvDireccion.setText(direccion);
        tvEstado.setText(estado);
        tvDetalles.setText(detalles);
    }
}
