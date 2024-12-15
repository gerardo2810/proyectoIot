package com.example.proyecto_iot;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class RegisterRepartidorActivity extends AppCompatActivity {
    private EditText etNombre;
    private EditText etApellido;
    private EditText etDNI;
    private EditText etNacimiento;
    private EditText etDireccion;
    private EditText etTelefono;
    private EditText etEmail;
    private TextInputLayout txtPassword;
    private Button btnRegister;
    private TextView lblLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //PDF
    private static final int PICK_PDF_REQUEST = 2;  // Solicitar código para seleccionar el PDF
    private TextView textViewPdfUrl; // TextView para mostrar la URL del PDF
    private Button buttonSelectPdf;  // Botón para seleccionar el archivo PDF
    private Uri selectedPdfUri = null; // Variable para almacenar la URI del archivo PDF seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_repartidor);

        // Inicialización de vistas
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etDNI = findViewById(R.id.etDNI);
        etNacimiento = findViewById(R.id.etNacimiento);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        etEmail = findViewById(R.id.etEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        lblLogin = findViewById(R.id.lblLogin);

        //PDF
        textViewPdfUrl = findViewById(R.id.textViewPdfUrl);
        buttonSelectPdf = findViewById(R.id.buttonSelectPdf);
        buttonSelectPdf.setOnClickListener(v -> openFileChooser());

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Botón de registro
        btnRegister.setOnClickListener(view -> createCliente());

        // Redirección al login
        lblLogin.setOnClickListener(view -> openLoginActivity());
    }

    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void createCliente() {
        // Obtención de datos del formulario
        String nombre = etNombre.getText().toString();
        String apellido = etApellido.getText().toString();
        String dni = etDNI.getText().toString();
        String nacimiento = etNacimiento.getText().toString();
        String direccion = etDireccion.getText().toString();
        String telefono = etTelefono.getText().toString();
        String email = etEmail.getText().toString();
        String password = txtPassword.getEditText().getText().toString();

        // Validación de campos
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingrese un Nombre");
            etNombre.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError("Ingrese un Apellido");
            etApellido.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dni)) {
            etDNI.setError("Ingrese un DNI");
            etDNI.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nacimiento)) {
            etNacimiento.setError("Ingrese una Fecha de Nacimiento");
            etNacimiento.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(direccion)) {
            etDireccion.setError("Ingrese una Dirección");
            etDireccion.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError("Ingrese un Teléfono");
            etTelefono.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese un Correo");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Ingrese una Contraseña");
            txtPassword.requestFocus();
            return;
        }

        // Creación de usuario en Firebase Authentication
        Toast.makeText(RegisterRepartidorActivity.this, "Espere un momento...", Toast.LENGTH_LONG).show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("repartidores").document(userID);

                        String defaultPhotoUrl = "https://cdn-icons-png.flaticon.com/256/3849/3849119.png";

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "PE"));
                        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Lima"));
                        String fechaHora = dateFormat.format(new Date());

                        // Estructura del documento
                        Map<String, Object> repartidor = new HashMap<>();
                        repartidor.put("nombre", nombre);
                        repartidor.put("apellido", apellido);
                        repartidor.put("dni", dni);
                        repartidor.put("nacimiento", nacimiento);
                        repartidor.put("direccion", direccion);
                        repartidor.put("telefono", telefono);
                        repartidor.put("email", email);
                        repartidor.put("contraseña", password);
                        repartidor.put("foto", defaultPhotoUrl);
                        repartidor.put("habilitado",true);
                        repartidor.put("aceptado",false);
                        repartidor.put("fecha",fechaHora);

                        // Subir PDF si se seleccionó uno
                        if (selectedPdfUri != null) {
                            uploadPdfToFirebase(selectedPdfUri, userID, documentReference, repartidor);
                        } else {
                            // Si no se seleccionó un PDF, guardar el usuario sin PDF
                            repartidor.put("cv","No adjuntó");
                            saveUserDocument(documentReference, repartidor);
                        }
                    } else {
                        Toast.makeText(RegisterRepartidorActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //PDF
    // Método para subir el archivo PDF a Firebase Storage
    private void uploadPdfToFirebase(Uri pdfUri, String userID, DocumentReference documentReference, Map<String, Object> repartidor) {
        // Crear una referencia en Firebase Storage para el archivo PDF
        StorageReference pdfRef = FirebaseStorage.getInstance().getReference().child("cvRepartidores/" + userID + ".pdf");

        // Subir el archivo PDF
        pdfRef.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL del archivo subido
                    pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String pdfUrl = uri.toString();
                        Log.d("PDF Upload", "PDF subido exitosamente: " + pdfUrl);
                        // Guardar repartidor
                        repartidor.put("cv",pdfUrl);
                        documentReference.set(repartidor)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterRepartidorActivity.this, "Registro exitoso, debe esperar a que sea aceptado", Toast.LENGTH_LONG).show();
                                    openLoginActivity();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegisterRepartidorActivity.this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("PDF Upload", "Error al subir el PDF", e);
                    Toast.makeText(this, "Error al subir el archivo PDF", Toast.LENGTH_SHORT).show();
                });
    }
    // Guardar los datos del repartidor sin PDF
    private void saveUserDocument(DocumentReference documentReference, Map<String, Object> repartidor) {
        documentReference.set(repartidor)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterRepartidorActivity.this, "Registro exitoso, debe esperar a que sea aceptado", Toast.LENGTH_SHORT).show();
                    openLoginActivity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterRepartidorActivity.this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Método para abrir el selector de archivos PDF
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf"); // Seleccionar solo archivos PDF
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedPdfUri = data.getData(); // Obtén el URI del archivo PDF seleccionado
            // Obtener el nombre del archivo desde la URI
            String fileName = getFileName(selectedPdfUri);
            textViewPdfUrl.setText("PDF seleccionado: " + fileName);  // Mostrar el URI del PDF en el TextView
        }
    }

    // Método para obtener el nombre del archivo desde la URI
    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
                cursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }
        return fileName;
    }

}
