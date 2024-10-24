package com.example.proyecto_iot.repartidor;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class EntregaCurso2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_curso_2);
        Button btnShowDialog = findViewById(R.id.button11);
        btnShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQrDialog();
            }
        });

    }
    public void abrirPagInicio (View view) {
        Intent intent = new Intent(this, InicioRepartidorActivity.class);
        intent.putExtra("showDialog", true);
        startActivity(intent);
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InicioRepartidorActivity.class); // Regresar a la vistaInicio
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
        startActivity(intent);
    }

    private void showQrDialog() {
        // Crear el diálogo
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_qr_repartidor);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Ajustar tamaño

        ImageView imgPhoto = dialog.findViewById(R.id.imgPhoto);
        imgPhoto.setImageResource(R.drawable.imagen_qr_code);

        // Configurar el botón de cierre
        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
