package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminListaPreguntasActivity extends AppCompatActivity {

    private ListView listViewPreguntas;
    private FirebaseFirestore db;
    private ImageView iconoVolver8;
    private Spinner spinnerCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lista_preguntas);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar Spinner, ListView y botón
        spinnerCategorias = findViewById(R.id.spinnerCategorias2);
        listViewPreguntas = findViewById(R.id.listViewPreguntas);
        iconoVolver8 = findViewById(R.id.iconoVolver8);

        // Obtener y mostrar preguntas en el ListView
        obtenerPreguntasDeFirestore();

        // Cargar opciones del Spinner desde strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categorias2_array, R.layout.style_spinner_header);
        adapter.setDropDownViewResource(R.layout.style_spinner_items);
        spinnerCategorias.setAdapter(adapter);

        // Cambio de activity al elegir una pregunta
        listViewPreguntas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el documento completo de Firestore
                String preguntaSeleccionada = (String) parent.getItemAtPosition(position);

                // Consultar Firestore para obtener los detalles de la pregunta seleccionada
                db.collection("preguntas")
                        .whereEqualTo("pregunta", preguntaSeleccionada.trim())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0); // Tomar el primer documento
                                    if (document != null) {
                                        // Obtener los detalles de la pregunta
                                        String idPregunta = document.getId();
                                        String pregunta = document.getString("pregunta");
                                        ArrayList<String> opciones = (ArrayList<String>) document.get("opciones");
                                        int correcta = document.getLong("correcta").intValue(); // Asegurarse de que el valor sea un entero
                                        String imagen = document.getString("imagen");
                                        String categoria = document.getString("categoria");

                                        // Crear un Intent para pasar los datos a AdminPreguntaActivity
                                        Intent intent = new Intent(AdminListaPreguntasActivity.this, AdminPreguntaActivity.class);
                                        intent.putExtra("idPregunta", idPregunta);
                                        intent.putExtra("pregunta", pregunta);
                                        intent.putExtra("opciones", opciones);
                                        intent.putExtra("correcta", correcta);
                                        intent.putExtra("imagen", imagen);
                                        intent.putExtra("categoria", categoria);

                                        // Iniciar la actividad AdminPreguntaActivity
                                        //startActivity(intent);
                                        startActivityForResult(intent, 1);
                                    }
                                } else {
                                    // Manejar errores
                                    Log.e("AdminListaPreguntas", "Error al obtener detalles de la pregunta", task.getException());
                                    Toast.makeText(AdminListaPreguntasActivity.this, "Error al obtener detalles de la pregunta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });




        // Cambio de categoria desde el spinner para mas comodidad del chabon admin
        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                obtenerPreguntasDeFirestore(); // Muestro todas las preguntas
            }

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Preguntas de la categoría seleccionada
                String categoriaSeleccionada = spinnerCategorias.getSelectedItem().toString().toLowerCase();

                if (categoriaSeleccionada.equals("todas") || categoriaSeleccionada.equals("selecciona una categoría ↷") ) {
                    obtenerPreguntasDeFirestore(); // Muestro todas las preguntas
                } else {
                    obtenerPreguntasPorCategoria(categoriaSeleccionada); //Muestro por categoria
                }
            }

        });

        iconoVolver8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void obtenerPreguntasDeFirestore() {
        db.collection("preguntas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> preguntasList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Obtener la pregunta del documento
                                String pregunta = document.getString("pregunta");

                                // Agregar la pregunta a la lista
                                preguntasList.add(pregunta+"\n");
                            }

                            // Mostrar preguntas en el ListView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminListaPreguntasActivity.this, android.R.layout.simple_list_item_1, preguntasList);
                            listViewPreguntas.setAdapter(adapter);
                        } else {
                            // Manejar errores
                            Exception exception = task.getException();
                            if (exception != null) {
                                // Imprimir el error en el log
                                Log.e("AdminListaPreguntas", "Error al obtener preguntas", exception);

                                // Puedes mostrar un mensaje de error al usuario si es necesario
                                Toast.makeText(AdminListaPreguntasActivity.this, "Error al obtener preguntas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void obtenerPreguntasPorCategoria(String categoria) {
        db.collection("preguntas")
                .whereEqualTo("categoria", categoria)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> preguntasList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Obtener la pregunta del documento
                                String pregunta = document.getString("pregunta");

                                // Agregar la pregunta a la lista
                                preguntasList.add(pregunta+"\n");
                            }

                            // Mostrar preguntas en el ListView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminListaPreguntasActivity.this, android.R.layout.simple_list_item_1, preguntasList);
                            listViewPreguntas.setAdapter(adapter);
                        } else {
                            // Manejar errores
                            Exception exception = task.getException();
                            if (exception != null) {
                                // Imprimir el error en el log
                                Log.e("AdminListaPreguntas", "Error al obtener preguntas por categoría de Firestore", exception);

                                // Puedes mostrar un mensaje de error al usuario si es necesario
                                Toast.makeText(AdminListaPreguntasActivity.this, "Error al obtener preguntas por categoría de Firestore", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            spinnerCategorias.setSelection(0);
            obtenerPreguntasDeFirestore();
        }
    }


}