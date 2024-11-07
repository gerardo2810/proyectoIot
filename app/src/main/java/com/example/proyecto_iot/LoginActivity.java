package com.example.proyecto_iot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.repartidor.RegistroRepartidorActivity;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    private final static String TAG = "msg-test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //verifica si el usuario ya se encuentra logueado
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                Log.d("msg-test", "Firebase uid: " + currentUser.getUid());
                goToMainActivity();
            }
        }

        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(view -> {

            loginBtn.setEnabled(false);

            List<AuthUI. IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            );

            AuthMethodPickerLayout customLayout =
                    new AuthMethodPickerLayout.Builder(R.layout.login_personalizado)
                            .setGoogleButtonId(R.id.btn_login_google)
                            .setEmailButtonId(R.id.btn_login_mail)
                            .build();

            //no hay sesión
            Intent intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAuthMethodPickerLayout(customLayout)
                    .setAvailableProviders(providers)
                    .build();

            signInLauncher.launch(intent);
        });

    }

    ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        Log.d(TAG, "Firebase uid: " + user.getUid() + "\n" +
                                "Display name: " + user.getDisplayName() + "\n" +
                                "Email: " + user.getEmail());

                        user.reload().addOnCompleteListener(task -> {
                            //Verificacion de email
                            if (user.isEmailVerified()) {
                                goToMainActivity();
                            } else {
                                user.sendEmailVerification().addOnCompleteListener(task2 -> {
                                    Toast.makeText(LoginActivity.this,
                                            "Se le ha enviado un correo para validar su cuenta",
                                            Toast.LENGTH_LONG).show();
                                });
                            }

                        });
                    } else {
                        Log.d(TAG, "user == null");
                    }
                } else {
                    Log.d(TAG, "Canceló el Log-in");
                }
                loginBtn.setEnabled(true);
            }
    );

    public void abrirPagRegistroRepartidor (View view) {
        Intent intent = new Intent(this, RegistroRepartidorActivity.class);
        startActivity(intent);
    }
    public void mostrarAlerta(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Registro Exitoso");
        alertDialog.setMessage("¡Gracias por querer unirte al equipo!\n" +
                "\n" +
                "Esta información será validada por el administrador\n" +
                "\n" +
                "Pronto te llegará un correo para el acceso a tu cuenta");
        alertDialog.setPositiveButton("Cerrar",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("msgAlerta","Positive");
                    }
                });
        alertDialog.show();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
