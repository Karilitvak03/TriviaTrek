package com.example.triviatrek;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageView;

public class AdministradorVerSugerenciaActivity extends AppCompatActivity {

    private ListView listViewMensajes;
    private FirebaseFirestore db;
    private ImageView iconoVolver2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrador_sugerencia_pregunta);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar ListView
        listViewMensajes = findViewById(R.id.listViewMensajes); // Asegúrate de tener un ListView con el ID listViewMensajes en tu layout.

        // Obtener mensajes de Firestore y mostrarlos en el ListView
        obtenerMensajesDeFirestore();

        iconoVolver2 = findViewById(R.id.iconoVolver2);


        iconoVolver2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAtras();
            }
        });
    }

    private void volverAtras() {
        onBackPressed();
    }

    private void obtenerMensajesDeFirestore() {
        db.collection("sugerencias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> mensajesList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Obtener datos del documento
                                String nombre = document.getString("nombre");
                                String apellido = document.getString("apellido");
                                String email = document.getString("email");
                                String pregunta = document.getString("pregunta");

                                // Construir el mensaje
                                String mensaje = "Nombre: " + nombre + "\nApellido: " + apellido + "\nEmail: " + email + "\nPregunta: " + pregunta;

                                // Agregar el mensaje a la lista
                                mensajesList.add(mensaje);
                            }

                            // Mostrar mensajes en el ListView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AdministradorVerSugerenciaActivity.this, android.R.layout.simple_list_item_1, mensajesList);
                            listViewMensajes.setAdapter(adapter);
                        } else {
                            // Manejar errores
                            Exception exception = task.getException();
                            if (exception != null) {
                                // Imprimir el error en el log
                                Log.e("AdministradorVerSugerencia", "Error al obtener mensajes de Firestore", exception);

                                // Puedes mostrar un mensaje de error al usuario si es necesario
                                Toast.makeText(AdministradorVerSugerenciaActivity.this, "Error al obtener mensajes de Firestore", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}