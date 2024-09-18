package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroRepartidorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_repartidor);

        Spinner spinner = findViewById(R.id.spinnerIdType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.document_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

// Para que el primer elemento actúe como hint
        spinner.setSelection(0, false);

    }


    public void abrirPagLogueo (View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
