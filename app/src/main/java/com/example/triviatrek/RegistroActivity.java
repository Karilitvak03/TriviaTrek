package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
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
    private Button btnAceptar;
    private FirebaseFirestore db;
    private ImageView iconoVolver5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        db = FirebaseFirestore.getInstance();

        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtEmail = findViewById(R.id.txtEmail2);
        txtClave = findViewById(R.id.txtClave2);
        txtConfirmarClave = findViewById(R.id.txtClave3);

        btnAceptar = findViewById(R.id.btnAceptar);
        iconoVolver5 = findViewById(R.id.iconoVolver5);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    // Limpiar espacios iniciales y finales
                    String email = txtEmail.getText().toString().trim().toLowerCase(); // Convertir a minúsculas
                    String nombre = txtNombre.getText().toString().trim();
                    String apellido = txtApellido.getText().toString().trim();

                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email, txtConfirmarClave.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Registra el usuario en Firestore
                                        registarUsuario(nombre, apellido, email); // Realiza el registro en Firestore
                                    } else {
                                        // Manejar errores de autenticación
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(RegistroActivity.this, "Error en el registro: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        iconoVolver5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAtras();
            }
        });
    }

    private void volverAtras() {
        onBackPressed();
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
    private void registarUsuario(String nombre, String apellido, String email) {
        // En el Map de usuario guardo los datos
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("apellido", apellido);
        usuario.put("email", email);
        usuario.put("oro", 100); // Agrega oro = 100
        usuario.put("rol", "user"); // Agrega rol = "user"

        // Obtener el UID del usuario autenticado
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Actualiza el documento con el UID correcto
        db.collection("usuarios")
                .document(uid)
                .set(usuario)
                .addOnSuccessListener(documentReference -> {
                    // Registro en Firestore completado con éxito
                    Toast.makeText(RegistroActivity.this, "Registro trekminado exitosamente", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    mostrarMensaje("Ocurrió un error, intente nuevamente" + e.getMessage());
                });
    }

}
