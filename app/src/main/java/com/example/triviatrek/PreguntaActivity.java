package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreguntaActivity extends AppCompatActivity {

    private TextView txtPregunta;
    private ImageView imgPregunta;
    private RadioGroup radioGroup;
    private RadioButton rbOpcion1;
    private RadioButton rbOpcion2;
    private RadioButton rbOpcion3;
    private Button btnContinuar;
    private TextView txtOro;
    private TextView txtUsuario;

    private FirebaseFirestore db;
    private DocumentSnapshot documento;
    private int respuestasCorrectas = 0;
    private int intentosRestantes = 3;
    private int cantidadOro;
    private boolean primeraVez = true;

    private Set<String> preguntasMostradas = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        db = FirebaseFirestore.getInstance();
        String categoria = getIntent().getStringExtra("categoria"); // Me guardo la categoria que se selecciono

        txtPregunta = findViewById(R.id.txtPregunta);
        imgPregunta = findViewById(R.id.imgPregunta);
        radioGroup = findViewById(R.id.radioGroup1);
        rbOpcion1 = findViewById(R.id.rbOpcion1);
        rbOpcion2 = findViewById(R.id.rbOpcion2);
        rbOpcion3 = findViewById(R.id.rbOpcion3);
        btnContinuar = findViewById(R.id.btnContinuar);

        txtOro = findViewById(R.id.txtOro);
        txtUsuario = findViewById(R.id.txtusuario);

        // Obtener el chabon logueado
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            // UID del chabon logueado
            String uid = usuario.getUid();

            // Referencia al documento del chabon logueado
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("usuarios").document(uid);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Documento del chabon logueado
                            String nombreUsuario = document.getString("nombre");
                            int oroUsuario = document.getLong("oro").intValue();

                            // Muestro los datos del chabon logueado xD
                            txtUsuario.setText("Usuario: " + nombreUsuario);
                            txtOro.setText("Oro: " + oroUsuario);
                        }
                    }
                }
            });
        } else {
            Toast.makeText(PreguntaActivity.this, "No existe usuario", Toast.LENGTH_SHORT).show();

        }

        ponerPregunta(categoria); // Pongo una pregunta de la categoria que eligió el chabon

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    Toast.makeText(PreguntaActivity.this, "Selecciona una opción, por favor.", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    int opcionElegida = radioGroup.indexOfChild(selectedRadioButton);

                    int correcta = documento.getLong("correcta").intValue();

                    if (opcionElegida == correcta) {
                        Toast.makeText(PreguntaActivity.this, "¡Respuesta Correcta! +10 oro", Toast.LENGTH_SHORT).show();
                        cantidadOro += 10;
                        respuestasCorrectas++;
                    } else {
                        // Respuesta Incorrecta
                        Toast.makeText(PreguntaActivity.this, "Respuesta Incorrecta. -10 oro", Toast.LENGTH_SHORT).show();
                        cantidadOro -= 10;
                    }

                    intentosRestantes--;

                    if (intentosRestantes > 0) {
                        ponerPregunta(categoria); // Pongo otra pregunta
                        actualizarInterfazOro();
                        radioGroup.clearCheck();
                    } else {
                        redirigirAMenu(); // Si no hay mas intentos mando al chabon al menu
                    }
                }
            }
        });
    }

    private void ponerPregunta(String categoria) {
        double random = Math.random();

        if (categoria.equals("random")) {
            // Si preguntasMostradas está vacío, obtén cualquier pregunta aleatoria
            if (preguntasMostradas.isEmpty()) {
                db.collection("preguntas")
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                    // Obtener la pregunta aleatoria desde el primer documento
                                    documento = task.getResult().getDocuments().get(0);

                                    // Agrega la pregunta actual al conjunto de preguntas mostradas
                                    preguntasMostradas.add(documento.getId());

                                    // Actualiza la interfaz con la nueva pregunta
                                    actualizarInterfaz(documento);
                                    bonusOro(documento.getLong("correcta").intValue());
                                }
                            }
                        });
            } else {
                // Si preguntasMostradas no está vacío, obtén una pregunta aleatoria excluyendo las mostradas
                db.collection("preguntas")
                        .whereNotIn("id", new ArrayList<>(preguntasMostradas))
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                    // Obtener la pregunta aleatoria desde el primer documento
                                    documento = task.getResult().getDocuments().get(0);

                                    // Agrega la pregunta actual al conjunto de preguntas mostradas
                                    preguntasMostradas.add(documento.getId());

                                    // Actualiza la interfaz con la nueva pregunta
                                    actualizarInterfaz(documento);
                                    bonusOro(documento.getLong("correcta").intValue());
                                }
                            }
                        });
            }
        } else {
            if (preguntasMostradas.isEmpty()) {
                db.collection("preguntas")
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                    // Obtener la pregunta aleatoria desde el primer documento
                                    documento = task.getResult().getDocuments().get(0);

                                    // Agrega la pregunta actual al conjunto de preguntas mostradas
                                    preguntasMostradas.add(documento.getId());

                                    // Actualiza la interfaz con la nueva pregunta
                                    actualizarInterfaz(documento);
                                }
                            }
                        });
            }else {

                // Si la categoría no es 'random', selecciona una pregunta de la categoría específica
                db.collection("preguntas")
                        .whereEqualTo("categoria", categoria)
                        .whereNotIn("id", new ArrayList<>(preguntasMostradas))
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                    // Obtener la pregunta aleatoria desde el primer documento
                                    documento = task.getResult().getDocuments().get(0);

                                    // Agrega la pregunta actual al conjunto de preguntas mostradas
                                    preguntasMostradas.add(documento.getId());

                                    // Actualiza la interfaz con la nueva pregunta
                                    actualizarInterfaz(documento);
                                }
                            }
                        });
            }
        }
    }

    private void actualizarInterfazOro() {
        txtOro.setText(String.valueOf(cantidadOro));
    }

    private void redirigirAMenu() {
        Intent intent = new Intent(PreguntaActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void actualizarInterfaz(DocumentSnapshot document) {
        // Obtener datos de la pregunta
        String pregunta = document.getString("pregunta");
        String imagenUrl = document.getString("imagen");
        List<String> opciones = (List<String>) document.get("opciones");

        // Actualizar las vistas con los datos de la pregunta
        txtPregunta.setText(pregunta);

        // Cargar la imagen con Picasso
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Picasso.get().load(imagenUrl).into(imgPregunta);
        } else {
            // Si no hay URL de imagen, puedes establecer un marcador de posición
            //imgPregunta.setImageResource(R.drawable.imagen_placeholder);
        }

        rbOpcion1.setText(opciones.get(0));
        rbOpcion2.setText(opciones.get(1));
        rbOpcion3.setText(opciones.get(2));
    }

    private void bonusOro(int opcionCorrecta) {
        if (opcionCorrecta == documento.getLong("correcta").intValue()) {
            // Respuesta Correcta
            Toast.makeText(PreguntaActivity.this, "¡Respuesta Correcta! +25 oro", Toast.LENGTH_SHORT).show();
            cantidadOro += 25;
            respuestasCorrectas++;
        } else {
            // Respuesta Incorrecta
            Toast.makeText(PreguntaActivity.this, "Respuesta Incorrecta. -25 oro", Toast.LENGTH_SHORT).show();
            cantidadOro -= 25;
        }
        actualizarInterfazOro();
    }
}

