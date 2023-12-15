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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtClave;
    private Button btnIngresar;
    private TextView txtRegistrarse;

    private DocumentSnapshot documento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        txtEmail = findViewById(R.id.txtEmail);
        txtClave = findViewById(R.id.txtClave);
        btnIngresar = findViewById(R.id.btnIngresar);
        txtRegistrarse = findViewById(R.id.textView4);

    /*    btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(txtEmail.getText().toString(), txtClave.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Guardo el uid del chabon logueado
                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        // Obtengo la referencia al documento del chabon en Firestore
                                        FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                // Info del chabon logueado
                                                                String nombre = document.getString("nombre");
                                                                String apellido = document.getString("apellido");
                                                                String email = document.getString("email");
                                                                String clave = document.getString("clave");
                                                                int oro = document.getLong("oro").intValue();

                                                                Toast.makeText(LoginActivity.this, "Sesion iniciada correctamente", Toast.LENGTH_SHORT).show();

                                                                // Mando al chabon logueado al mainActivity
                                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    }
                                                });

                                    } else {
                                        Toast.makeText(LoginActivity.this, "Error: Verifica tus datos :(", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

     */

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
                                        Toast.makeText(LoginActivity.this, "Bienvenido ", Toast.LENGTH_SHORT).show();
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
            mostrarMensaje("Error: Pone un eMail válido porfa!");
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
