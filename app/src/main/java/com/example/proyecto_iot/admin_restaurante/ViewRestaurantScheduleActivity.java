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

public class ViewRestaurantScheduleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_horarios);

        Button btnCloseRestaurant = findViewById(R.id.btn_close_restaurant);

        btnCloseRestaurant.setOnClickListener(v -> {
            // Lógica para cerrar el restaurante (puedes mostrar un dialogo de confirmación)
            Toast.makeText(this, "Restaurante cerrado", Toast.LENGTH_SHORT).show();
        });

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Termina esta actividad para volver a la anterior
                finish();
            }
        });

        // Botón "Aceptar Pedido"
        btnCloseRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el diálogo de confirmación
                showConfirmDialog();
            }
        });
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewRestaurantScheduleActivity.this, R.style.CustomAlertDialog);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_close, null);
        builder.setView(customLayout);

        // Botones dentro del diálogo
        Button btnConfirmar = customLayout.findViewById(R.id.btn_confirmar);
        Button btnCancelar = customLayout.findViewById(R.id.btn_cancelar);

        AlertDialog dialog = builder.create();

        // Acciones para el botón Confirmar
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra el diálogo
                dialog.dismiss();

                // Muestra un mensaje
                Toast.makeText(ViewRestaurantScheduleActivity.this, "Restaurante Cerrado", Toast.LENGTH_SHORT).show();

                // Redirigir a la actividad AbrirRestauranteActivity
                Intent intent = new Intent(ViewRestaurantScheduleActivity.this, AbrirRestauranteActivity.class);
                startActivity(intent);

                // Finalizar la actividad actual
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
