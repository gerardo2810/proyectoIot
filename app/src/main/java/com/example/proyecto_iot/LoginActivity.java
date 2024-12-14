package com.example.proyecto_iot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.admin_restaurante.AbrirRestauranteActivity;
import com.example.proyecto_iot.admin_restaurante.InicioRestauranteActivity;
import com.example.proyecto_iot.cliente.InicioClienteActivity;
import com.example.proyecto_iot.repartidor.InicioRepartidorActivity;
import com.example.proyecto_iot.repartidor.RegistroRepartidorActivity;
import com.example.proyecto_iot.superadmin.gestion_usuarios_superadmin;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView lblRegister;
    private Button googleSignInButton, loginRepartidorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.txtMail);
        passwordEditText = findViewById(R.id.txtPasswordEditText);
        loginButton = findViewById(R.id.btnLogin);
        lblRegister = findViewById(R.id.lblRegister);
        googleSignInButton = findViewById(R.id.btnGoogle);
        loginRepartidorButton = findViewById(R.id.btnRegistrarRepartidor);

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginButton.setEnabled(false);
        emailEditText.addTextChangedListener(loginTextWatcher);
        passwordEditText.addTextChangedListener(loginTextWatcher);

        // Inicio de sesión con correo y contraseña
        loginButton.setOnClickListener(v -> signInWithEmail(emailEditText.getText().toString(), passwordEditText.getText().toString()));

        // Registro
        lblRegister.setOnClickListener(v -> openRegisterActivity());

        // Botón de inicio de sesión con Google
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        loginRepartidorButton.setOnClickListener(v -> openRegisterRepartidorActivity());
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = emailEditText.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();

            loginButton.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openRegisterRepartidorActivity() {
        Intent intent = new Intent(this, RegisterRepartidorActivity.class);
        startActivity(intent);
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkIfNewUser(user, false); // false indica que no es inicio de sesión con Google
                    } else {
                        Toast.makeText(LoginActivity.this, "Error en inicio de sesión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Error en autenticación con Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkIfNewUser(user, true); // true indica inicio de sesión con Google
                    } else {
                        Toast.makeText(LoginActivity.this, "Error en autenticación con Google", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfNewUser(FirebaseUser user, boolean isGoogleSignIn) {
        String uid = user.getUid();
        String email = user.getEmail();

        db.collection("clientes").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean habilitado = documentSnapshot.getBoolean("habilitado");
                if(habilitado){
                    navigateToActivity(InicioClienteActivity.class);
                } else {
                    Toast.makeText(LoginActivity.this, "Su cuenta está inhabilitada. Contactese con soporte", Toast.LENGTH_LONG).show();
                }
            } else {
                checkOtherCollections(uid, email, isGoogleSignIn);
            }
        });
    }

    private void checkOtherCollections(String uid, String email, boolean isGoogleSignIn) {
        db.collection("repartidores").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean habilitado = documentSnapshot.getBoolean("habilitado");
                Boolean aceptado = documentSnapshot.getBoolean("aceptado");
                if(habilitado && aceptado){
                    navigateToActivity(InicioRepartidorActivity.class);
                }
                if(!habilitado && aceptado){
                    Toast.makeText(LoginActivity.this, "Su cuenta está inhabilitada. Contactese con soporte", Toast.LENGTH_LONG).show();
                }
                if(habilitado && !aceptado){
                    Toast.makeText(LoginActivity.this, "Su solicitud aun se encuentra en revisión.", Toast.LENGTH_LONG).show();
                }
                if(!habilitado && !aceptado){
                    Toast.makeText(LoginActivity.this, "Su solicitud ha sido rechazada.", Toast.LENGTH_LONG).show();
                }
            } else {
                db.collection("administradores").document(uid).get().addOnSuccessListener(adminSnapshot -> {
                    if (adminSnapshot.exists()) {
                        db.collection("restaurantes")
                                .whereEqualTo("idAdministrador", uid)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        Boolean habilitado = adminSnapshot.getBoolean("habilitado");
                                        if(habilitado){
                                            DocumentSnapshot restaurantDoc = querySnapshot.getDocuments().get(0);
                                            String idRestaurante = restaurantDoc.getId();

                                            Intent intent = new Intent(LoginActivity.this, InicioRestauranteActivity.class);
                                            intent.putExtra("idRestaurante", idRestaurante);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Su cuenta está inhabilitada. Contactese con soporte", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "No se encontró restaurante para este administrador", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(LoginActivity.this, "Error al buscar restaurante: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        db.collection("superadmin").document(uid).get().addOnSuccessListener(superAdminSnapshot -> {
                            if (superAdminSnapshot.exists()) {
                                navigateToActivity(gestion_usuarios_superadmin.class);
                            } else {
                                if (isGoogleSignIn) {
                                    Intent intent = new Intent(LoginActivity.this, CompleteRegisterActivity.class);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Credenciales no válidas o cuenta no registrada.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(LoginActivity.this, targetActivity);
        startActivity(intent);
        finish();
    }
}
