package com.example.triviatrek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminAgregarPreguntaActivity extends AppCompatActivity {

    private EditText txtPonerPregunta, txtOpcion1, txtOpcion2, txtOpcion3;
    private CheckBox cb1, cb2, cb3;
    private Spinner spinnerCategorias;
    private ImageView btnVolver ,imgPreguntaElegida;
    private Uri uriImagenSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_agregar_pregunta);

        txtPonerPregunta = findViewById(R.id.txtPonerPregunta);
        txtOpcion1 = findViewById(R.id.txtOpcion1);
        txtOpcion2 = findViewById(R.id.txtOpcion2);
        txtOpcion3 = findViewById(R.id.txtOpcion3);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        btnVolver = findViewById(R.id.iconoVolver9);

        imgPreguntaElegida = findViewById(R.id.imgPreguntaElegida);
        imgPreguntaElegida.setVisibility(View.INVISIBLE);

        FirebaseApp.initializeApp(this);

        // Desactiva temporalmente App Check para evitar la advertencia
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.setTokenAutoRefreshEnabled(false);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categorias_array, R.layout.style_spinner_header);
        adapter.setDropDownViewResource(R.layout.style_spinner_items);
        spinnerCategorias.setAdapter(adapter);

        Button btnBuscar = findViewById(R.id.btnBuscar);
        Button btnGuardar = findViewById(R.id.btnGuardar);


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si cb1 está marcado, desmarcar cb2 y cb3
                if (cb1.isChecked()) {
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                }
            }
        });

        cb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si cb2 está marcado, desmarcar cb1 y cb3
                if (cb2.isChecked()) {
                    cb1.setChecked(false);
                    cb3.setChecked(false);
                }
            }
        });

        cb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si cb3 está marcado, desmarcar cb1 y cb2
                if (cb3.isChecked()) {
                    cb1.setChecked(false);
                    cb2.setChecked(false);
                }
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir la galería
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 1);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Desactiva temporalmente App Check para evitar la advertencia
                FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
                firebaseAppCheck.setTokenAutoRefreshEnabled(false);

                if (validarCampos()) {
                    // Obtén una instancia de FirebaseStorage
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    // Obtén una referencia a la carpeta en Storage donde se guardarán las imágenes
                    StorageReference imgPreguntasRef = storageRef.child("imgPreguntas");

                    // Obtén los valores de los campos
                    String pregunta = txtPonerPregunta.getText().toString().trim().toUpperCase();
                    String opcion1 = txtOpcion1.getText().toString().trim().toUpperCase();
                    String opcion2 = txtOpcion2.getText().toString().trim().toUpperCase();
                    String opcion3 = txtOpcion3.getText().toString().trim().toUpperCase();
                    boolean cb1Checked = cb1.isChecked();
                    boolean cb2Checked = cb2.isChecked();
                    boolean cb3Checked = cb3.isChecked();
                    String categoria = spinnerCategorias.getSelectedItem().toString().toLowerCase();

                    // Crear un mapa con los datos
                    Map<String, Object> preguntaMap = new HashMap<>();
                    preguntaMap.put("pregunta", pregunta);
                    preguntaMap.put("opciones", Arrays.asList(opcion1, opcion2, opcion3));
                    preguntaMap.put("categoria", categoria);

                    // Determinar la opción correcta y agregarla al mapa
                    int respuestaCorrecta = 0;
                    if (cb2Checked) respuestaCorrecta = 1;
                    else if (cb3Checked) respuestaCorrecta = 2;

                    // Subir la imagen a Firebase Storage
                    guardarImagen(imgPreguntasRef, preguntaMap, respuestaCorrecta);
                }
                firebaseAppCheck.setTokenAutoRefreshEnabled(true);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imgPreguntaElegida.setVisibility(View.INVISIBLE);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Obtiene la URI de la imagen seleccionada
            uriImagenSeleccionada = data.getData();

            // Muestra la imagen seleccionada en el ImageView
            imgPreguntaElegida.setImageURI(uriImagenSeleccionada);
            imgPreguntaElegida.setVisibility(View.VISIBLE);
        }
    }

    private boolean validarCampos() {
        // Validación de campos
        String pregunta = txtPonerPregunta.getText().toString().trim().toUpperCase();
        String opcion1 = txtOpcion1.getText().toString().trim().toUpperCase();
        String opcion2 = txtOpcion2.getText().toString().trim().toUpperCase();
        String opcion3 = txtOpcion3.getText().toString().trim().toUpperCase();
        boolean cb1Checked = cb1.isChecked();
        boolean cb2Checked = cb2.isChecked();
        boolean cb3Checked = cb3.isChecked();
        String categoria = spinnerCategorias.getSelectedItem().toString().toLowerCase();

        // Validación de no vacuidad de campos
        if (pregunta.isEmpty() || opcion1.isEmpty() || opcion2.isEmpty() || opcion3.isEmpty()) {
            mostrarToast("Completa los campos vacíos!");
            return false;
        }

        // Elegir categoría
        List<String> categoriasPermitidas = new ArrayList<>();
        categoriasPermitidas.add("arte");
        categoriasPermitidas.add("ciencia");
        categoriasPermitidas.add("deportes");
        categoriasPermitidas.add("entretenimiento");
        categoriasPermitidas.add("geografia");


        if (!categoriasPermitidas.contains(categoria.toLowerCase())) {
            mostrarToast("Falta la categoría che!");
            return false;
        }

        // Validación de checkbox
        if (!(cb1Checked || cb2Checked || cb3Checked)) {
            mostrarToast("Selecciona la respuesta correcta");
            return false;
        }

        // Validación de un solo checkbox seleccionado
        if ((cb1Checked && (cb2Checked || cb3Checked)) ||
                (cb2Checked && (cb1Checked || cb3Checked)) ||
                (cb3Checked && (cb1Checked || cb2Checked))) {
            mostrarToast("Selecciona solo una respuesta correcta");
            return false;
        }

        // Si todos los campos están ok = true
        return true;
    }

    private void guardarImagen(StorageReference storageRef, Map<String, Object> preguntaMap, int respuestaCorrecta) {
        // Verifica si se seleccionó una imagen
        if (uriImagenSeleccionada != null) {
            // Obtén el nombre del archivo
            String nombreArchivo = System.currentTimeMillis() + ".jpg";

            // Obtén la referencia a la imagen en Storage
            StorageReference imagenRef = storageRef.child(nombreArchivo);

            // Convierte la imagen a bytes
            try {
                InputStream inputStream = getContentResolver().openInputStream(uriImagenSeleccionada);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                // Sube la imagen a Storage
                UploadTask uploadTask = imagenRef.putBytes(data);
                uploadTask.addOnFailureListener(exception -> {
                    // Manejar errores en caso de fallo en la carga de la imagen
                    mostrarToast("Error al subir la imagen. Intenta de nuevo.");
                }).addOnSuccessListener(taskSnapshot -> {
                    // Si la carga de la imagen es exitosa, obten la URL de descarga
                    imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Agrega la URL de la imagen al mapa
                        preguntaMap.put("imagen", "imgPreguntas/"+ nombreArchivo);

                        // Agrega la opción correcta al mapa
                        preguntaMap.put("correcta", respuestaCorrecta);

                        // Obtiene una instancia de FirebaseFirestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // Obtiene una referencia a la colección "preguntas" en Firestore
                        String uid = FirebaseAuth.getInstance().getUid();
                        if (uid != null) {
                            db.collection("preguntas").add(preguntaMap)
                                    .addOnSuccessListener(documentReference -> {
                                        // Éxito al agregar el documento
                                        mostrarToast("Pregunta guardada con éxito");
                                        vaciarCampos();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error al agregar el documento
                                        mostrarToast("Error al guardar la pregunta. Intenta de nuevo");
                                    });
                        } else {
                            // El usuario no está autenticado
                            mostrarToast("Error: Usuario no autenticado.");
                        }
                    });
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mostrarToast("Error al cargar la imagen. Intenta de nuevo.");
            }
        } else {
            // No se seleccionó ninguna imagen
            // Agrega la opción correcta al mapa
            preguntaMap.put("correcta", respuestaCorrecta);

            // Obtiene una instancia de FirebaseFirestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Obtiene una referencia a la colección "preguntas" en Firestore
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid != null) {
                db.collection("preguntas").add(preguntaMap)
                        .addOnSuccessListener(documentReference -> {
                            // Éxito al agregar el documento
                            mostrarToast("Pregunta guardada con éxito");
                            vaciarCampos();
                            imgPreguntaElegida.setVisibility(View.INVISIBLE);
                        })
                        .addOnFailureListener(e -> {
                            // Error al agregar el documento
                            mostrarToast("Error al guardar la pregunta. Intenta de nuevo.");
                        });

            } else {
                // El usuario no está autenticado
                mostrarToast("Error: Usuario no autenticado.");
            }
        }
    }

    private void vaciarCampos() {
        // Vaciar todos los campos después de guardar
        txtPonerPregunta.getText().clear();
        txtOpcion1.getText().clear();
        txtOpcion2.getText().clear();
        txtOpcion3.getText().clear();
        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);
        // También puedes reiniciar el spinner a la posición inicial si es necesario
        spinnerCategorias.setSelection(0);
        // Limpiar la imagen
        imgPreguntaElegida.setImageDrawable(null);
    }

    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
