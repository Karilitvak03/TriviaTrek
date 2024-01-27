    package com.example.triviatrek;


    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.net.Uri;
    import android.os.Bundle;
    import android.util.Log;
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
    import com.google.android.gms.tasks.Tasks;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.QueryDocumentSnapshot;
    import com.google.firebase.firestore.QuerySnapshot;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import com.bumptech.glide.Glide;

    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.LinkedList;
    import java.util.List;
    import java.util.Set;
    import java.util.stream.Collectors;

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
        private int correctas = 0;
        private int intentosRestantes = 3;
        private int cantidadOro;
        private PreguntaClass preguntaActual;

        private LinkedList<PreguntaClass> preguntasMostradas = new LinkedList<>();
        private String categoriaElegida;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pregunta);

            db = FirebaseFirestore.getInstance();

            Intent intent = getIntent();
            categoriaElegida = intent.getStringExtra("categoria");

            txtPregunta = findViewById(R.id.txtPregunta);
            imgPregunta = findViewById(R.id.imgPregunta);
            radioGroup = findViewById(R.id.radioGroup1);
            rbOpcion1 = findViewById(R.id.txtOpcion1);
            rbOpcion2 = findViewById(R.id.txtOpcion2);
            rbOpcion3 = findViewById(R.id.txtOpcion3);
            btnContinuar = findViewById(R.id.btnContinuar);

            txtOro = findViewById(R.id.txtOro);
            txtUsuario = findViewById(R.id.txtusuario);

            imgPregunta.setVisibility(View.INVISIBLE);

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
                                String nombre = document.getString("nombre");
                                String apellido = document.getString("apellido");

                                String nombreUsuario = nombre + " " + apellido;
                                int oroUsuario = document.getLong("oro").intValue();
                                cantidadOro = oroUsuario;

                                // Muestro los datos del chabon logueado xD
                                txtUsuario.setText(nombreUsuario);
                                txtOro.setText(String.valueOf(oroUsuario));

                            }
                        }
                    }
                });
            }

            buscarPregunta(preguntasMostradas);

            btnContinuar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Verificar la respuesta seleccionada
                    int respuestaId = radioGroup.getCheckedRadioButtonId();
                    RadioButton respuestaSeleccionada = findViewById(respuestaId);

                    if (respuestaSeleccionada != null) {
                        // Obtener el índice de la opción seleccionada (0, 1 o 2)
                        int indiceRespuesta = radioGroup.indexOfChild(respuestaSeleccionada);

                        // Comparar el índice de la respuesta seleccionada con el valor correcto
                        if (indiceRespuesta == preguntaActual.getCorrecta()) {
                            // Respuesta Correcta
                            correctas ++;
                            Toast.makeText(PreguntaActivity.this, "Respuesta Correcta", Toast.LENGTH_SHORT).show();

                            // Actualizar la cantidad de oro
                            if (categoriaElegida.equals("todas")) {
                                // Si la categoría es "todas", ganar 25 de oro
                                cantidadOro += 25;
                            } else {
                                // Si la categoría no es "todas", ganar 10 de oro
                                cantidadOro += 10;
                            }
                        } else {
                            // Respuesta Incorrecta
                            Toast.makeText(PreguntaActivity.this, "Respuesta Incorrecta", Toast.LENGTH_SHORT).show();

                            // Actualizar la cantidad de oro
                            if (categoriaElegida.equals("todas")) {
                                // Si la categoría es "todas", restar 25 de oro
                                cantidadOro = Math.max(0, cantidadOro - 25);
                            } else {
                                // Si la categoría no es "todas", restar 10 de oro
                                cantidadOro = Math.max(0, cantidadOro - 10);
                            }
                        }

                        // Actualizar la cantidad de oro en el TextView
                        txtOro.setText(String.valueOf(cantidadOro));

                        // Actualizar el campo "oro" del usuario en Firestore
                        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                        if (usuario != null) {
                            String uid = usuario.getUid();
                            DocumentReference usuarioRef = FirebaseFirestore.getInstance().collection("usuarios").document(uid);

                            usuarioRef.update("oro", cantidadOro);
                        }


                        cargarPreguntaAleatoria();

                    } else {
                        // El chabon olvidó poner una respuesta xD
                        Toast.makeText(PreguntaActivity.this, "Selecciona una respuesta", Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }//---- FIN DE ON CREATE ----

        private void cargarPreguntaAleatoria() {
            radioGroup.clearCheck(); // Vacio los checks seleccionados en los radio butoms

            if(intentosRestantes  != 0) {
                // Verificar si la categoría elegida es "todas"
                if (categoriaElegida.equals("todas")) {
                    // Pregunta aleatoria de cualquier categoría
                    db.collection("preguntas")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                DocumentSnapshot document = queryDocumentSnapshots.getDocuments()
                                        .get((int) (Math.random() * queryDocumentSnapshots.size()));

                                // Procesar los datos y actualizar la interfaz de usuario
                                preguntaActual = new PreguntaClass(document);
                                mostrarPregunta(preguntaActual);
                                preguntasMostradas.add(preguntaActual);
                            });
                } else {
                    // Cargo pregunta aleatoria de una categoría elegida
                    cargarPreguntaAleatoriaPorCategoria();
                }
                intentosRestantes--;
            } else {
                // Luego de 3 preguntas mando al chabon al menu final
                Intent intentMenu = new Intent(PreguntaActivity.this, MenuFinalActivity.class);
                intentMenu.putExtra("categoria", categoriaElegida);
                intentMenu.putExtra("correctas", correctas);

                startActivity(intentMenu);
                finish();  // Cierra la activity pa que el chabon no vuelva a atras
            }
        }

        private void cargarPreguntaAleatoriaPorCategoria() {
            db.collection("preguntas")
                    .whereEqualTo("categoria", categoriaElegida)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments()
                                .get((int) (Math.random() * queryDocumentSnapshots.size()));

                        // Procesar los datos y actualizar la interfaz de usuario
                        preguntaActual = new PreguntaClass(document);
                        mostrarPregunta(preguntaActual);
                        preguntasMostradas.add(preguntaActual);
                    });
        }

        private void mostrarPregunta(PreguntaClass pregunta) {
            // Bajo los datos de Firestore
            String preguntaTexto = pregunta.getPregunta();
            List<String> opciones = pregunta.getOpciones();
            String imagenPath = pregunta.getImagen();

            // Actualizo la info de los campos visibles
            txtPregunta.setText(preguntaTexto);
            rbOpcion1.setText(opciones.get(0));
            rbOpcion2.setText(opciones.get(1));
            rbOpcion3.setText(opciones.get(2));

            // Cargar imagen desde Firebase Storage
            cargarImagen(imagenPath);
        }


        private void cargarImagen(String imagenPath) {
            // Pongo el ImgaeView invisible para q no se vea feo antes de cargar la imagen
            imgPregunta.setVisibility(View.INVISIBLE);

            // Verificar si la actividad aún está en un estado válido
            if (!isDestroyed()) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagenPath);
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Verificar nuevamente si la actividad aún está en un estado válido
                    if (!isDestroyed()) {
                        // Cargar la imagen con Glide
                        Glide.with(this)
                                .load(uri)
                                .into(imgPregunta);
                        imgPregunta.setVisibility(View.VISIBLE); // Hago visible la imageView
                    }
                });
            }
        }

        private void buscarPregunta(LinkedList<PreguntaClass> preguntas) {
            if (preguntas.isEmpty()) {
                cargarPreguntaAleatoria();
            } else {
                List<PreguntaClass> preguntasNoMostradas = preguntas.stream()
                        .filter(pregunta -> !preguntasMostradas.contains(pregunta))
                        .collect(Collectors.toList());

                if (!preguntasNoMostradas.isEmpty()) {
                    // Si hay preguntas no mostradas, seleccionar una aleatoria
                    int indicePregunta = (int) (Math.random() * preguntasNoMostradas.size());
                    preguntaActual = preguntasNoMostradas.get(indicePregunta);
                    mostrarPregunta(preguntaActual);
                    preguntasMostradas.add(preguntaActual);
                } else {
                    // Si todas las preguntas han sido mostradas, cargar una pregunta aleatoria
                    cargarPreguntaAleatoria();
                }
            }
        }

    }

