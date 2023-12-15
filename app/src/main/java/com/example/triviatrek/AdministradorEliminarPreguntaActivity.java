package com.example.triviatrek;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdministradorEliminarPreguntaActivity extends AppCompatActivity {

    private ListView listViewPreguntas;
    private Button btnEliminarPregunta;
    private FirebaseFirestore db;
    private Button btnVolver8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrador_eliminar_pregunta);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar ListView y botón
        listViewPreguntas = findViewById(R.id.listViewPreguntas); // Asegúrate de tener un ListView con el ID listViewPreguntas en tu layout.
        btnEliminarPregunta = findViewById(R.id.btnEliminarPregunta); // Asegúrate de tener un Button con el ID btnEliminarPregunta en tu layout.
        btnVolver8 = findViewById(R.id.btnVolver8);
        // Obtener y mostrar preguntas en el ListView
        obtenerPreguntasDeFirestore();

        // Establecer un listener para el clic en un elemento del ListView
        listViewPreguntas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Habilitar el botón de eliminación al seleccionar una pregunta
                btnEliminarPregunta.setEnabled(true);
            }
        });

        // Establecer un listener para el clic en el botón de eliminación
        btnEliminarPregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la pregunta seleccionada
                String preguntaSeleccionada = (String) listViewPreguntas.getSelectedItem();

                // Eliminar la pregunta de la base de datos de Firebase
                eliminarPreguntaDeFirestore(preguntaSeleccionada);
            }
        });
        btnVolver8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void obtenerPreguntasDeFirestore() {
        db.collection("preguntas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> preguntasList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Obtener la pregunta del documento
                                String pregunta = document.getString("pregunta");

                                // Agregar la pregunta a la lista
                                preguntasList.add(pregunta);
                            }

                            // Mostrar preguntas en el ListView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AdministradorEliminarPreguntaActivity.this, android.R.layout.simple_list_item_1, preguntasList);
                            listViewPreguntas.setAdapter(adapter);
                        } else {
                            // Manejar errores
                            Exception exception = task.getException();
                            if (exception != null) {
                                // Imprimir el error en el log
                                Log.e("AdministradorEliminarPregunta", "Error al obtener preguntas de Firestore", exception);

                                // Puedes mostrar un mensaje de error al usuario si es necesario
                                Toast.makeText(AdministradorEliminarPreguntaActivity.this, "Error al obtener preguntas de Firestore", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void eliminarPreguntaDeFirestore(String pregunta) {
        // Obtener el ID del documento de la pregunta seleccionada
        db.collection("preguntas")
                .whereEqualTo("pregunta", pregunta)
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            // Obtener el ID del documento de la primera coincidencia (debería ser única)
                            String documentId = task.getResult().getDocuments().get(0).getId();

                            // Eliminar la pregunta de la base de datos de Firebase
                            db.collection("preguntas").document(documentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // La pregunta se eliminó exitosamente
                                            Toast.makeText(AdministradorEliminarPreguntaActivity.this, "Pregunta eliminada correctamente", Toast.LENGTH_SHORT).show();

                                            // Puedes actualizar la lista de preguntas si es necesario
                                            obtenerPreguntasDeFirestore();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Hubo un error al eliminar la pregunta
                                            Toast.makeText(AdministradorEliminarPreguntaActivity.this, "Error al eliminar la pregunta", Toast.LENGTH_SHORT).show();
                                            Log.e("AdministradorEliminarPregunta", "Error al eliminar la pregunta", e);
                                        }
                                    });
                        } else {
                            // No se encontró la pregunta en la base de datos
                            Toast.makeText(AdministradorEliminarPreguntaActivity.this, "No se encontró la pregunta en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}