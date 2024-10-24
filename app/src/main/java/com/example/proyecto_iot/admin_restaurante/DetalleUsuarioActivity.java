package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class DetalleUsuarioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_detalle_usuario);

        ImageView ivImagenUsuario = findViewById(R.id.iv_imagen_usuario);
        TextView tvNombreUsuario = findViewById(R.id.tv_nombre_usuario);
        TextView tvEdad = findViewById(R.id.tv_edad);
        TextView tvDni = findViewById(R.id.tv_dni);
        TextView tvCorreo = findViewById(R.id.tv_correo);
        TextView tvTelefono = findViewById(R.id.tv_telefono);
        TextView tvCantPedidos = findViewById(R.id.cant_pedidos);
        TextView tvGastado = findViewById(R.id.tv_gastado);

        // Obtener datos del intent
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String edad = intent.getStringExtra("edad");
        String correo = intent.getStringExtra("correo");
        String dni = intent.getStringExtra("dni");
        String telefono = intent.getStringExtra("telefono");
        String cantPedidos = intent.getStringExtra("cantPedidos");
        String gastado = intent.getStringExtra("gastado");

        // Asignar valores a la vista
        tvNombreUsuario.setText(nombre);
        tvEdad.setText(edad);
        tvDni.setText(dni);
        tvCorreo.setText(correo);
        tvTelefono.setText(telefono);
        tvCantPedidos.setText(cantPedidos);
        tvGastado.setText(gastado);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
