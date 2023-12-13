package com.example.triviatrek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtClave;
    private Button btnIngresar;
    private TextView txtRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        txtEmail = findViewById(R.id.txtEmail);
        txtClave = findViewById(R.id.txtClave);
        btnIngresar = findViewById(R.id.btnIngresar);
        txtRegistrarse = findViewById(R.id.textView4);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(txtEmail.getText().toString(), txtClave.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(LoginActivity.this, "Bienvenido ", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Error. Verifica tus datos :(", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        txtRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validarCampos() {
        String email = txtEmail.getText().toString().trim();
        String clave = txtClave.getText().toString();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarMensaje("Error: Pone un eMail valido porfa!");
            return false;
        }

        if (TextUtils.isEmpty(clave) || clave.length() < 6) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        // Si todos los campos estan bien, retorna true :)
        return true;
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
