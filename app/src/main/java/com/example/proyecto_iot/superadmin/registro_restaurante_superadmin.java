package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.Administrador;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteSA;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class registro_restaurante_superadmin extends AppCompatActivity {

    Administrador administrador;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private EditText textFieldNombre, textFieldUbicacion, textFieldDescripccion, textFieldEslogan, textFieldPrecioDelivery;
    private Spinner spinnerTipoComida;

    private static final int PICK_IMAGE_REQUEST_LOGO = 1;
    private static final int PICK_IMAGE_REQUEST_PORTADA = 2;
    private ImageView imgLogoRest, imgPreview;
    private Button btnUploadLogo, btnUploadImage, btnUploadPhoto;
    private Uri imageUri, imageUriLogo; // Uri de la imagen seleccionada
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_registro_restaurante);

        imgPreview = findViewById(R.id.imgPreview);
        imgLogoRest = findViewById(R.id.imgLogoRest);
        btnUploadLogo = findViewById(R.id.buttonUploadLogo);
        btnUploadImage = findViewById(R.id.buttonUploadImage);

        storageReference = FirebaseStorage.getInstance().getReference("iconosRestaurantes");

        // Botones para seleccionar imágenes
        btnUploadLogo.setOnClickListener(v -> selectImage(PICK_IMAGE_REQUEST_LOGO));
        btnUploadImage.setOnClickListener(v -> selectImage(PICK_IMAGE_REQUEST_PORTADA));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        administrador = (Administrador) getIntent().getSerializableExtra("administrador");


        Button btnGuardar = findViewById(R.id.buttonRegistrarRest);

        textFieldNombre = findViewById(R.id.editTextNombreRest);
        textFieldUbicacion = findViewById(R.id.editTextUbicacionRest);
        textFieldDescripccion = findViewById(R.id.editTextDescripcionRest);
        textFieldEslogan = findViewById(R.id.editTextEsloganRest);
        textFieldPrecioDelivery = findViewById(R.id.editTextPrecioDelivery);

        //Gestion de spinner tipo comida
        spinnerTipoComida = findViewById(R.id.spinnerTipoComida);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.tipo_de_comidas, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoComida.setAdapter(adapter1);
        //----------------------------------------------------------------------------

        btnGuardar.setOnClickListener(view -> {
            String nombre = textFieldNombre.getText().toString();
            String ubicacion = textFieldUbicacion.getText().toString();
            String tipoComida = spinnerTipoComida.getSelectedItem().toString();
            String descripcion = textFieldDescripccion.getText().toString();
            String slogan = textFieldEslogan.getText().toString();
            String precioDelivery = textFieldPrecioDelivery.getText().toString();

            if (nombre.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty() || tipoComida.equals("-Seleccionar-") || slogan.isEmpty() || precioDelivery.isEmpty()) {
                Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
            } else if (imageUri == null) {
                Toast.makeText(this, "Debe seleccionar una imagen", Toast.LENGTH_LONG).show();
            } else {
                btnGuardar.setEnabled(false);

                registrarAdmin(administrador.getCorreo(), administrador.getContrasena());

            }
        });

    }

    private void registrarAdmin(String email, String password) {
        Toast.makeText(this, "Espere un momento...", Toast.LENGTH_SHORT).show();
        administrador.setRestaurante(textFieldNombre.getText().toString());

        db.collection("administradores")
                .add(administrador)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idAdministrador = task.getResult().getId();

                        RestauranteSA restaurante = new RestauranteSA();
                        restaurante.setIdAdministrador(idAdministrador); // Establecer el ID del administrador

                        String imageFileName = UUID.randomUUID().toString();
                        String logoFileName = UUID.randomUUID().toString();
                        String portadaFileName = UUID.randomUUID().toString();

                        StorageReference logoRef = storageReference.child(logoFileName);
                        StorageReference portadaRef = storageReference.child(portadaFileName);

                        logoRef.putFile(imageUriLogo)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Obtener la URL del logo
                                    logoRef.getDownloadUrl().addOnSuccessListener(logoUri -> {
                                        restaurante.setFotoLogo(logoUri.toString());

                                        portadaRef.putFile(imageUri)
                                                .addOnSuccessListener(taskSnapshot1 -> {
                                                    // Obtener la URL de la portada
                                                    portadaRef.getDownloadUrl().addOnSuccessListener(portadaUri -> {
                                                        restaurante.setFotoPortada(portadaUri.toString());

                                                        restaurante.setNombre(textFieldNombre.getText().toString());
                                                        restaurante.setUbicacion(textFieldUbicacion.getText().toString());
                                                        restaurante.setTipoDeComida(spinnerTipoComida.getSelectedItem().toString());
                                                        restaurante.setDescripcion(textFieldDescripccion.getText().toString());
                                                        restaurante.setVentas(0);
                                                        restaurante.setOpen(false);
                                                        restaurante.setEslogan(textFieldEslogan.getText().toString());
                                                        double precioDelivery = Double.parseDouble(textFieldPrecioDelivery.getText().toString());
                                                        restaurante.setPrecioDelivery(precioDelivery);

                                                        // Agregar restaurante a Firestore
                                                        db.collection("restaurantes")
                                                                .add(restaurante)
                                                                .addOnSuccessListener(documentReference -> {
                                                                    String restauranteId = documentReference.getId();

                                                                    try {
                                                                        Bitmap qrBitmap = generateQRCode(restauranteId);
                                                                        saveQRCodeToStorage(qrBitmap, restauranteId); // Opcional: Guardar el QR en Storage
                                                                    } catch (Exception e) {
                                                                        Toast.makeText(this, "Error al generar el código QR", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                    Toast.makeText(this, "Administrador y restaurante registrados exitosamente", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(this, gestion_usuarios_superadmin.class);
                                                                    startActivity(intent);
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(this, "Algo ocurrió al intentar registrar el restaurante", Toast.LENGTH_SHORT).show();
                                                                });
                                                    });
                                                });
                                    });
                                });
                    }
                });

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // Error al registrar
                        Toast.makeText(this, "Error registro admin: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, gestion_usuarios_superadmin.class);
                        startActivity(intent);
                    }
                });
    }


    private Bitmap generateQRCode(String text) throws WriterException {
        int width = 500, height = 500;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }


    private void saveQRCodeToStorage(Bitmap bitmap, String restauranteId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference qrRef = FirebaseStorage.getInstance().getReference("qrs").child(restauranteId + ".png");
        qrRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> qrRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Opcional: Guardar el URL del QR en Firestore
                            db.collection("restaurantes").document(restauranteId).update("qrCodeUrl", uri.toString());
                        })
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar el código QR", Toast.LENGTH_SHORT).show();
                });
    }



    // Método para seleccionar una imagen desde la galería
    private void selectImage(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), requestCode);
    }

    // Método para manejar el resultado de la selección de la imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST_LOGO) {
                imageUriLogo = data.getData();
                imgLogoRest.setImageURI(imageUriLogo);
            } else if (requestCode == PICK_IMAGE_REQUEST_PORTADA) {
                imageUri = data.getData();
                imgPreview.setImageURI(imageUri);
            }
        }
    }

}