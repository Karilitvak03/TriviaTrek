package com.example.triviatrek;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ImageView iconoVolver7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        db = FirebaseFirestore.getInstance();

        iconoVolver7 = findViewById(R.id.iconoVolver7);
        Button btnAceptar2 = findViewById(R.id.btnAceptar2);

        iconoVolver7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAtras();
            }
        });

        btnAceptar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarPerfil();
            }
        });
    }

    private void volverAtras() {
        onBackPressed();
    }

    private void actualizarPerfil() {
        EditText txtNombre2 = findViewById(R.id.txtNombre2);
        EditText txtApellido2 = findViewById(R.id.txtApellido2);
        EditText txtEmail3 = findViewById(R.id.txtEmail3);
        EditText txtClave5 = findViewById(R.id.txtClave5);
        EditText txtClave4 = findViewById(R.id.txtClave4);

        String nombre = txtNombre2.getText().toString().trim();
        String apellido = txtApellido2.getText().toString().trim();
        String email = txtEmail3.getText().toString().trim();
        String nuevaClave = txtClave5.getText().toString().trim();
        String confirmarClave = txtClave4.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) || TextUtils.isEmpty(email) || TextUtils.isEmpty(nuevaClave) || TextUtils.isEmpty(confirmarClave)) {
            Toast.makeText(PerfilActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(PerfilActivity.this, "Ingrese un email válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(nuevaClave) || nuevaClave.length() < 6) {
            Toast.makeText(PerfilActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevaClave.equals(confirmarClave)) {
            Toast.makeText(PerfilActivity.this, "Las claves no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa con los datos a actualizar en Firestore
        Map<String, Object> datosPerfil = new HashMap<>();
        datosPerfil.put("nombre", nombre);
        datosPerfil.put("apellido", apellido);
        datosPerfil.put("email", email);


        // Actualizar el documento en la colección "perfiles" (cambia "perfiles" por el nombre de tu colección)
        db.collection("perfiles").document("ID_DEL_DOCUMENTO_ACTUALIZAR")
                .update(datosPerfil)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PerfilActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PerfilActivity.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

