package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;

public class NuevoPedidoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

    }
    public void abrirPagRecojo1 (View view) {
        Intent intent = new Intent(this, RecojoCurso1Activity.class);
        startActivity(intent);
    }
}
