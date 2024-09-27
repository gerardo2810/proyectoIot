package com.example.proyecto_iot.superadmin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.SuperadminActivityGestionReportesBinding;

public class gestion_usuarios_superadmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SuperadminActivityGestionReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}