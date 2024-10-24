package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;

public class NuevoPedidoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        Button boton1 = findViewById(R.id.button2);
        boton1.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ultima_vista", "RecojoCurso1Activity");
            editor.apply();


            Intent intent = new Intent(this, RecojoCurso1Activity.class);
            startActivity(intent);
        });

    }


}
