package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class EntregaCurso1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_curso_1);

    }

    public void abrirPagEntrega2 (View view) {
        Intent intent = new Intent(this, EntregaCurso2Activity.class);
        startActivity(intent);
    }
}
