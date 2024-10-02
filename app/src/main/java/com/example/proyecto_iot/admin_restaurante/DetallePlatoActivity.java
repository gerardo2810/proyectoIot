package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class DetallePlatoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_detalle_plato);

        ImageView ivImagenPlato = findViewById(R.id.iv_imagen_plato);
        TextView tvNombrePlato = findViewById(R.id.tv_nombre_plato_detalle);
        TextView tvCategoriaPlato = findViewById(R.id.tv_categoria_plato_detalle);
        TextView tvDescripcion = findViewById(R.id.tv_descripcion_plato_detalle);
        TextView tvPrecio = findViewById(R.id.tv_precio_plato_detalle);
        TextView tvCantidadVendida = findViewById(R.id.tv_cantidad_vendida);
        TextView tvGanancia = findViewById(R.id.tv_ganancia);

        // Obtener datos del intent
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String categoria = intent.getStringExtra("categoria");
        String descripcion = intent.getStringExtra("descripcion");
        String precio = intent.getStringExtra("precio");
        String cantVendida = intent.getStringExtra("cantVendida");
        String ganancia = intent.getStringExtra("ganancia");

        // Asignar valores a la vista
        tvNombrePlato.setText(nombre);
        tvCategoriaPlato.setText(categoria);
        tvDescripcion.setText(descripcion);
        tvPrecio.setText(precio);
        tvCantidadVendida.setText(cantVendida);
        tvGanancia.setText(ganancia);
    }
}
