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
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtClave;
    private Button btnIngresar;
    private TextView txtRegistrarse;

    private DocumentSnapshot documento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtEmail);
        txtClave = findViewById(R.id.txtClave);
        btnIngresar = findViewById(R.id.btnIngresar);
        txtRegistrarse = findViewById(R.id.textView4);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    // Saco el feo espacio que aveces queda al ingresar datos
                    String email = txtEmail.getText().toString().trim().toLowerCase();

                    // Verificando si el chabon esta registrado
                    FirebaseFirestore.getInstance().collection("usuarios")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            // El chabon no está registrado
                                            Toast.makeText(LoginActivity.this, "Cuenta no registrada", Toast.LENGTH_SHORT).show();
                                            // vacio los campos para no perder tiempo en borrarlos yo xD
                                            txtEmail.setText("");
                                            txtClave.setText("");
                                        } else {
                                            // El chabon está registrado, procede con la autenticación
                                            FirebaseAuth.getInstance()
                                                    .signInWithEmailAndPassword(email, txtClave.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                // Obtener el ID del usuario autenticado
                                                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                                // Consultar el rol del usuario en Firestore
                                                                FirebaseFirestore.getInstance().collection("usuarios")
                                                                        .document(userId)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentSnapshot document = task.getResult();
                                                                                    if (document != null && document.exists()) {
                                                                                        // Obtener el rol del documento
                                                                                        String rol = document.getString("rol");

                                                                                        // Redirigir según el rol
                                                                                        if ("admin".equals(rol)) {
                                                                                            // Si el usuario tiene rol de admin, ir a MenuAdminActivity
                                                                                            Intent intentAdmin = new Intent(LoginActivity.this, AdminMenuActivity.class);
                                                                                            startActivity(intentAdmin);
                                                                                            finish();
                                                                                        } else{
                                                                                            // Si el usuario tiene rol de user, ir a MainActivity
                                                                                            Intent intentUser = new Intent(LoginActivity.this, MainActivity.class);
                                                                                            startActivity(intentUser);
                                                                                            finish();
                                                                                        }
                                                                                    }
                                                                                } else {
                                                                                    // Manejar la falla en la consulta
                                                                                    Toast.makeText(LoginActivity.this, "Error al obtener el rol del usuario", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            } else { // Error de usuario o contraseña
                                                                Toast.makeText(LoginActivity.this, "Error: Verifica tus datos :(", Toast.LENGTH_SHORT).show();
                                                                // vacio los campos para no perder tiempo en borrarlos yo xD
                                                                txtEmail.setText("");
                                                                txtClave.setText("");
                                                            }
                                                        }
                                                    });
                                        }
                                    } else {
                                        // Manejar la falla en la consulta
                                        Toast.makeText(LoginActivity.this, "Error en la consulta de usuarios", Toast.LENGTH_SHORT).show();
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
