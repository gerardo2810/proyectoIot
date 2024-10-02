package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class EntregaCurso2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_curso_2);

    }
    public void abrirPagInicio (View view) {
        Intent intent = new Intent(this, InicioRepartidorActivity.class);
        intent.putExtra("showDialog", true);
        startActivity(intent);
    }
}
