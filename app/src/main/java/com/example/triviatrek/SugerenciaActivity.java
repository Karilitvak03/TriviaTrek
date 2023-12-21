package com.example.triviatrek;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class SugerenciaActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ImageView iconoVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencia);
        db = FirebaseFirestore.getInstance();  // Inicio el coso de Firestore

        iconoVolver = findViewById(R.id.iconoVolver);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        EditText txtApellido = findViewById(R.id.txtApellido3);
        EditText txtNombre = findViewById(R.id.txtNombre3);
        EditText txtEmail = findViewById(R.id.txtEmail4);
        EditText txtAsunto = findViewById(R.id.txtAsunto);
        EditText txtSugerirPregunta = findViewById(R.id.txtSugerirPregunta);

        // ID del chabon logueado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Referencia al documento del usuario logueado
            DocumentReference userRef = db.collection("usuarios").document(uid);

            // Obtener datos del usuario
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Datos del usuario encontrados
                            String nombre = document.getString("nombre");
                            String apellido = document.getString("apellido");
                            String email = document.getString("email");

                            // Llenar los campos de texto con los datos del usuario
                            txtNombre.setText(nombre);
                            txtApellido.setText(apellido);
                            txtEmail.setText(email);
                        } else {
                            Toast.makeText(SugerenciaActivity.this, "El documento del usuario no existe", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SugerenciaActivity.this, "Error al obtener el documento del usuario: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        iconoVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = txtNombre.getText().toString();
                String apellido = txtApellido.getText().toString();
                String email = txtEmail.getText().toString();
                String asunto = txtAsunto.getText().toString();
                String pregunta = txtSugerirPregunta.getText().toString();

                if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || asunto.isEmpty() || pregunta.isEmpty()) {
                    Toast.makeText(SugerenciaActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear un mapa con los datos a enviar a Firestore
                Map<String, Object> sugerencia = new HashMap<>();
                sugerencia.put("nombre", nombre);
                sugerencia.put("apellido", apellido);
                sugerencia.put("email", email);
                sugerencia.put("asunto", asunto);
                sugerencia.put("pregunta", pregunta);

                // Agregar el documento a la colección "sugerencias"
                db.collection("sugerencias")
                        .add(sugerencia)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // El documento se agregó exitosamente
                                Toast.makeText(SugerenciaActivity.this, "Gracias por contactarnos!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Hubo un error al agregar el documento
                                Toast.makeText(SugerenciaActivity.this, "Error al enviar la Sugerencia", Toast.LENGTH_SHORT).show();
                                Log.e("TAG", "Error al enviar la Sugerencia", e);
                            }
                        });
                //onBackPressed();
                finish();
            }
        });

    }

}


