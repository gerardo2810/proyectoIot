package com.example.proyecto_iot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.repartidor.RegistroRepartidorActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Intent intent = getIntent();
        if (intent.getBooleanExtra("showDialog", false)) {
            mostrarAlerta();
        }

    }

    public void abrirPagRegistroRepartidor (View view) {
        Intent intent = new Intent(this, RegistroRepartidorActivity.class);
        startActivity(intent);
    }
    public void mostrarAlerta(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Registro Exitoso");
        alertDialog.setMessage("¡Gracias por querer unirte al equipo!\n" +
                "\n" +
                "Esta información será validada por el administrador\n" +
                "\n" +
                "Pronto te llegará un correo para el acceso a tu cuenta");
        alertDialog.setPositiveButton("Cerrar",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("msgAlerta","Positive");
                    }
                });
        alertDialog.show();
    }
}
