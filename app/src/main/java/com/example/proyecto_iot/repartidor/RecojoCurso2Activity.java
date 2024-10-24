package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class RecojoCurso2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recojo_curso_2);
        Button boton1 = findViewById(R.id.button3);
        boton1.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ultima_vista", "EntregaCurso1Activity");
            editor.apply();


            Intent intent = new Intent(this, EntregaCurso1Activity.class);
            startActivity(intent);
        });

    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InicioRepartidorActivity.class); // Regresar a la vistaInicio
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
        startActivity(intent);
    }
}
