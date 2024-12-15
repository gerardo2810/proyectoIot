package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.RegisterActivity;

import java.util.Random;

public class PreloaderActivity extends AppCompatActivity {

    private String nombre, apellido, dni, nacimiento, direccion, telefono, email, password;
    private String codigoGenerado;
    private int intentos = 0;
    private CountDownTimer temporizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preloader);

        // Obtener datos del intent
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        dni = getIntent().getStringExtra("dni");
        nacimiento = getIntent().getStringExtra("nacimiento");
        direccion = getIntent().getStringExtra("direccion");
        telefono = getIntent().getStringExtra("telefono");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        codigoGenerado=getIntent().getStringExtra("codigo");
        System.out.println("Preloader CODIGO " + codigoGenerado);
        enviarCorreo(email, codigoGenerado);

        // Configurar temporizador e intentos
        configurarUI();
    }

    private void configurarUI() {
        TextView tvTemporizador = findViewById(R.id.tvTimer);
        EditText etCodigo = findViewById(R.id.etCodigo);
        Button btnValidar = findViewById(R.id.btnValidar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        temporizador = new CountDownTimer(1200000000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTemporizador.setText("Tiempo restante: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                Toast.makeText(PreloaderActivity.this, "Código expirado", Toast.LENGTH_SHORT).show();
                regresarARegistro();
            }
        }.start();

        btnValidar.setOnClickListener(v -> {
            String codigoIngresado = etCodigo.getText().toString();
            if (codigoIngresado.equals(codigoGenerado)) {
                temporizador.cancel();
                registrarCliente();
            } else {
                intentos++;
                Toast.makeText(this, "Código incorrecto. Intento: " + intentos, Toast.LENGTH_SHORT).show();
                if (intentos >= 4) {
                    Toast.makeText(this, "Demasiados intentos fallidos", Toast.LENGTH_SHORT).show();
                    regresarARegistro();
                }
            }
        });

        btnCancelar.setOnClickListener(v -> {
            temporizador.cancel();
            regresarARegistro();
        });
    }



    private void enviarCorreo(String correo, String codigo) {
        // Simula el envío del correo
        Toast.makeText(this, "Se envió un código a: " + correo, Toast.LENGTH_SHORT).show();
        System.out.println("Código enviado: " + codigo); // Solo para depuración
    }

    private void registrarCliente() {
        // Crear cliente y regresar a LoginActivity
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("registroCorrecto", true);
        intent.putExtra("nombre", nombre);
        intent.putExtra("apellido", apellido);
        intent.putExtra("dni", dni);
        intent.putExtra("nacimiento", nacimiento);
        intent.putExtra("direccion", direccion);
        intent.putExtra("telefono", telefono);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }

    private void regresarARegistro() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }
}
