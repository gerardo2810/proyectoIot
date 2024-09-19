package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class RecojoCurso2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recojo_curso_2);

    }
    public void abrirPagEntrega1 (View view) {
        Intent intent = new Intent(this, EntregaCurso1Activity.class);
        startActivity(intent);
    }
}
