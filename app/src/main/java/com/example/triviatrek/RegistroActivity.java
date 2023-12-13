package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText txtNombre, txtApellido, txtEmail, txtClave, txtConfirmarClave;
    private Button btnAceptar, btnVolver;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_main);

        db = FirebaseFirestore.getInstance();

        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtEmail = findViewById(R.id.txtEmail2);
        txtClave = findViewById(R.id.txtClave2);
        txtConfirmarClave = findViewById(R.id.txtClave3);

        btnAceptar = findViewById(R.id.btnAceptar);
        btnVolver = findViewById(R.id.btnVovler);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {

                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(txtEmail.getText().toString(), txtConfirmarClave.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        registarUsuario(); // Realiza el registro en Firestore

                                        Toast.makeText(RegistroActivity.this, "Registro completado exitosamente", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Manejar errores de autenticación
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(RegistroActivity.this, "Error en el registro: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                }else{
                    //Toast.makeText(RegistroActivity.this, "Error, vuelve a intentarlo porfa!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText().toString().trim();
        String apellido = txtApellido.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String clave = txtClave.getText().toString();
        String confirmarClave = txtConfirmarClave.getText().toString();

        //Validaciones por doquier
        if (TextUtils.isEmpty(nombre)) {
            mostrarMensaje("Ingrese su nombre");
            return false;
        }

        if (TextUtils.isEmpty(apellido)) {
            mostrarMensaje("Ingrese su apellido");
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarMensaje("Ingrese un email válido");
            return false;
        }

        if (TextUtils.isEmpty(clave) || clave.length() < 6) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        if (!clave.equals(confirmarClave)) {
            mostrarMensaje("Las contraseñas no coinciden");
            return false;
        }

        // Si todos los campos estan bien, retorna true :)
        return true;
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
    private void registarUsuario() {
        String nombre = txtNombre.getText().toString().trim();
        String apellido = txtApellido.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();

        // En el Map de usuario guardo los datos
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("apellido", apellido);
        usuario.put("email", email);

        // Obtener una referencia a la colección "usuarios" en tu Firestore
        db.collection("usuarios")
                .add(usuario)
                .addOnSuccessListener(documentReference -> {
                    // Registro en Firestore completado con éxito
                    Toast.makeText(RegistroActivity.this, "Registro en Firestore completado", Toast.LENGTH_SHORT).show();

                    // Ahora, realiza la autenticación después de que el registro en Firestore ha tenido éxito
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(txtEmail.getText().toString(), txtConfirmarClave.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Autenticación completada con éxito
                                    Toast.makeText(RegistroActivity.this, "Autenticación completada", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Manejar errores de autenticación
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegistroActivity.this, "Error en la autenticación: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    mostrarMensaje("Error al guardar en Firestore: " + e.getMessage());
                });
    }

}
