package com.example.proyecto_iot.superadmin;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;

public class editar_perfil_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView imageViewSelected;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_editar_perfil);

        //Volver una pantalla atras
        LinearLayout regresar = findViewById(R.id.header_layout);

        regresar.setOnClickListener(v -> {
            Intent intent = new Intent(this, perfil_superadmin.class);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_perfil);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_usuarios) {
                    intent = new Intent(editar_perfil_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(editar_perfil_superadmin.this, gestion_reportes_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(editar_perfil_superadmin.this, perfil_superadmin.class);
                }
                if (intent != null) {
                    intent.putExtra("SELECTED_ITEM_ID", item.getItemId());
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        //----------------------------------------------------------------------------

        //Gestion del Formulario
        Button buttonChooseImage = findViewById(R.id.buttonUploadImage);
        imageViewSelected = findViewById(R.id.imageViewSelected);

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
        //----------------------------------------------------------------------------

    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Validar la extensión del archivo
                if (isImageFile(imageUri)) {
                    imageViewSelected.setImageURI(imageUri);
                    imageViewSelected.setBackground(null);
                } else {
                    Toast.makeText(this, "Solo se permiten imágenes JPEG, PNG, JPG o AVIF", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean isImageFile(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        String type = contentResolver.getType(uri);
        Log.d("Image File Type", "MIME Type: " + type);

        if (type != null) {
            boolean isValidImage = type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/jpg") || type.equals("image/avif");
            Log.d("Image File Validation", "Is Valid Image: " + isValidImage);
            return isValidImage;
        }
        return false;
    }

}
