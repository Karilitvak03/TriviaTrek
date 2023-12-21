package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ImageView iconoVolver7;
    private EditText txtNombre2, txtApellido2, txtEmail3, txtClaveNueva, txtClaveFinal, txtClaveActual;
    private Switch swClave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Obtener referencias a los elementos de la interfaz
        iconoVolver7 = findViewById(R.id.iconoVolver7);
        Button btnAceptar2 = findViewById(R.id.btnAceptar2);
        swClave = findViewById(R.id.swClave);
        txtNombre2 = findViewById(R.id.txtNombre2);
        txtApellido2 = findViewById(R.id.txtApellido2);
        txtEmail3 = findViewById(R.id.txtEmail3);
        txtClaveNueva = findViewById(R.id.txtClaveNueva);
        txtClaveFinal = findViewById(R.id.txtClaveFinal);
        txtClaveActual = findViewById(R.id.txtClaveActual);

        txtClaveActual.setVisibility(View.INVISIBLE);
        txtClaveNueva.setVisibility(View.INVISIBLE);
        txtClaveFinal.setVisibility(View.INVISIBLE);

        iconoVolver7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                txtEmail3.setText(userEmail);
            }
            obtenerDatosUsuario(currentUser.getUid());
        }

        swClave.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch en ON, mostrar campos de clave
                txtClaveActual.setVisibility(View.VISIBLE);
                txtClaveNueva.setVisibility(View.VISIBLE);
                txtClaveFinal.setVisibility(View.VISIBLE);
            } else {
                // Switch en OFF, ocultar campos de clave
                txtClaveActual.setVisibility(View.GONE);
                txtClaveNueva.setVisibility(View.GONE);
                txtClaveFinal.setVisibility(View.GONE);
            }
        });

        btnAceptar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarPerfil();
            }
        });


    }
    private void obtenerDatosUsuario(String uid) {
        // Consultar la colección "usuarios" en Firestore usando el UID del usuario actual
        db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Si el documento existe, obtener los datos y autocompletar los campos
                            String nombre = documentSnapshot.getString("nombre");
                            String apellido = documentSnapshot.getString("apellido");

                            if (nombre != null && apellido != null) {
                                txtNombre2.setText(nombre);
                                txtApellido2.setText(apellido);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error en caso de que la consulta falle
                        Toast.makeText(EditarPerfilActivity.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void actualizarPerfil() {

        // Obtener los valores de los campos
        String nombre = txtNombre2.getText().toString().trim();
        String apellido = txtApellido2.getText().toString().trim();
        String email = txtEmail3.getText().toString().trim();
        String nuevaClave = txtClaveNueva.getText().toString().trim();
        String confirmarClave = txtClaveFinal.getText().toString().trim();
        String claveActual = txtClaveActual.getText().toString().trim();

        // Validar que los campos requeridos estén completos
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) || TextUtils.isEmpty(email)) {
            Toast.makeText(EditarPerfilActivity.this, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar el formato del correo electrónico
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(EditarPerfilActivity.this, "Ingrese un email válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar la longitud de la nueva contraseña (si se proporciona)
        if (!TextUtils.isEmpty(nuevaClave) && nuevaClave.length() < 6) {
            Toast.makeText(EditarPerfilActivity.this, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las contraseñas coincidan (si se proporciona una nueva contraseña)
        if (!TextUtils.isEmpty(nuevaClave) && !nuevaClave.equals(confirmarClave)) {
            Toast.makeText(EditarPerfilActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar la contraseña actual si el Switch está activado
        if (swClave.isChecked()) {
            if (TextUtils.isEmpty(claveActual)) {
                Toast.makeText(EditarPerfilActivity.this, "Ingrese la contraseña actual", Toast.LENGTH_SHORT).show();
                return;
            }

            // Autenticar al usuario con su contraseña actual antes de actualizar el perfil
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userEmail = user.getEmail();
                if (userEmail != null) {
                    // Autenticar al usuario con su correo electrónico y contraseña actual
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, claveActual)
                            .addOnSuccessListener(authResult -> {
                                // La autenticación fue exitosa, actualizar el perfil
                                actualizarPerfilEnFirestore(user.getUid(), nombre, apellido, email, nuevaClave);
                            })
                            .addOnFailureListener(e -> {
                                // La autenticación falló, mostrar un mensaje de error
                                Toast.makeText(EditarPerfilActivity.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        } else {
            // El Switch está apagado, actualizar el perfil sin validar la contraseña actual
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                actualizarPerfilEnFirestore(user.getUid(), nombre, apellido, email, nuevaClave);
            }
        }
    }

    private void actualizarPerfilEnFirestore(String uid, String nombre, String apellido, String email, String nuevaClave) {
        // Crear un mapa con los datos a actualizar en Firestore
        Map<String, Object> datosPerfil = new HashMap<>();
        datosPerfil.put("nombre", nombre);
        datosPerfil.put("apellido", apellido);
        datosPerfil.put("email", email);

        // Actualizar el documento en la colección "usuarios" usando el UID del usuario actual
        db.collection("usuarios").document(uid)
                .update(datosPerfil)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarPerfilActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();

                    // Actualizar la contraseña si se proporciona una nueva
                    if (!TextUtils.isEmpty(nuevaClave)) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.updatePassword(nuevaClave)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(EditarPerfilActivity.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();

                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(EditarPerfilActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditarPerfilActivity.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Si no se proporcionó una nueva contraseña, simplemente cierra sesión
                        Intent intent = new Intent(EditarPerfilActivity.this, MainActivity.class);
                        intent.putExtra("actualizarDatos", true);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarPerfilActivity.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                });
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();

        // Redirigir al MainActivity
        Intent intent = new Intent(EditarPerfilActivity.this, MainActivity.class);
        intent.putExtra("actualizarDatos", true);
        startActivity(intent);
        finish();
    }


}

