package com.example.proyecto_iot.repartidor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.gestion_usuarios_superadmin;
import com.example.proyecto_iot.superadmin.lista_usuarios_superadmin;
import com.example.proyecto_iot.superadmin.registro_nuevoadmin_superadmin;
import com.example.proyecto_iot.superadmin.solicitudes_repartidores_superadmin;
import com.example.proyecto_iot.superadmin.ver_logs_superadmin;

public class InicioRepartidorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_repartidor);


        //Gestion de los cardviews
        for (int i = 1; i <= 2; i++) {
            int arrowIconId = getResources().getIdentifier("arrow_icon_" + i, "id", getPackageName());
            ImageView arrowIcon = findViewById(arrowIconId);
            final int finalI = i;

            arrowIcon.setOnClickListener(v -> {
                Intent intent = null;
                if (finalI == 1) {
                    intent = new Intent(InicioRepartidorActivity.this, NuevoPedidoActivity.class);
                } else if (finalI == 2) {
                    intent = new Intent(InicioRepartidorActivity.this, NuevoPedidoActivity.class);
                }

                if (intent != null) {
                    startActivity(intent);
                }
            });
        }
        //----------------------------------------------------------------------------
        Intent intent = getIntent();
        if (intent.getBooleanExtra("showDialog", false)) {
            mostrarAlerta();
        }

    }

    public void mostrarAlerta(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Registro Exitoso");
        alertDialog.setMessage("¡Se registró correctamente el pedido!");
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
