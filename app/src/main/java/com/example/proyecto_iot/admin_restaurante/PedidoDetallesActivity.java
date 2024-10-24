package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class PedidoDetallesActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_mas_detalles);

        ImageView backButton = findViewById(R.id.back_poraceptar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Termina esta actividad para volver a la anterior (PorAceptarFragment)
                finish();
            }
        });

        // Botón "Aceptar Pedido"
        Button btnAceptarPedido = findViewById(R.id.btn_aceptar);
        btnAceptarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el diálogo de confirmación
                showConfirmDialog();
            }
        });
    }

    // Método para mostrar el diálogo de confirmación
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PedidoDetallesActivity.this, R.style.CustomAlertDialog); // Aplica el estilo personalizado
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        builder.setView(customLayout);

        // Botones dentro del diálogo
        Button btnConfirmar = customLayout.findViewById(R.id.btn_confirmar);
        Button btnCancelar = customLayout.findViewById(R.id.btn_cancelar);

        AlertDialog dialog = builder.create();

        // Acciones para el botón Confirmar
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes realizar la acción cuando el pedido es confirmado
                dialog.dismiss();
                // Muestra un mensaje o realiza la acción de aceptación del pedido
                Toast.makeText(PedidoDetallesActivity.this, "Pedido Aceptado", Toast.LENGTH_SHORT).show();

                // Finalizar la actividad y regresar al fragmento anterior
                finish();
            }
        });

        // Acciones para el botón Cancelar
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra el diálogo
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        dialog.show();
    }
}
