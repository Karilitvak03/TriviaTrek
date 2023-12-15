package com.example.triviatrek;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class SugerenciaPreguntaActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sugerenciapregunta_main);

        db = FirebaseFirestore.getInstance();  // Inicio el coso de Firestore
        Button btnVolver6 = findViewById(R.id.btnVolver6);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        EditText txtApellido3= findViewById(R.id.txtApellido3);
        EditText txtNombre3= findViewById(R.id.txtNombre3);
        EditText txtEmail4= findViewById(R.id.txtEmail4);
        EditText txtSugerirPregunta  = findViewById(R.id.txtSugerirPregunta);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = txtApellido3.getText().toString();
                String apellido = txtNombre3.getText().toString();
                String email = txtEmail4.getText().toString();
                String pregunta = txtSugerirPregunta.getText().toString();

                if (nombre.isEmpty() || apellido.isEmpty()  || email.isEmpty() || pregunta.isEmpty()) {
                    Toast.makeText(SugerenciaPreguntaActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear un mapa con los datos a enviar a Firestore
                Map<String, Object> sugerencia = new HashMap<>();
                sugerencia.put("nombre", nombre);
                sugerencia.put("apellido", apellido);
                sugerencia.put("email", email);
                sugerencia.put("pregunta", pregunta);

                // Agregar el documento a la colección "sugerencias"
                db.collection("sugerencias")
                        .add(sugerencia)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // El documento se agregó exitosamente
                                Toast.makeText(SugerenciaPreguntaActivity.this, "Sugerencia Pregunta enviada correctamente", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Hubo un error al agregar el documento
                                Toast.makeText(SugerenciaPreguntaActivity.this, "Error al enviar la Sugerencia Pregunta", Toast.LENGTH_SHORT).show();
                                Log.e("TAG", "Error al enviar la Sugerencia Pregunta", e);
                            }
                        });
            }
        });

        btnVolver6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }
    public void volverAtras() {

        onBackPressed();
    }
}


