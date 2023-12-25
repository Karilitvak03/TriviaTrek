package com.example.triviatrek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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

public class AdminPreguntaActivity extends AppCompatActivity {

    private EditText txtPregunta;
    private EditText txtOpcion1;
    private EditText txtOpcion2;
    private EditText txtOpcion3;
    private ImageView imgPregunta;
    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private Spinner spCategoria;


    private FirebaseFirestore db;
    private String idPregunta;
    private String pregunta;
    private ArrayList<String> opciones;
    private int correcta;
    private String categoria;
    private String imagen;
    private Drawable drawable;

    private ImageView btnVolver;
    private Button btnBuscar;
    private Button btnGuardar;
    private Button btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pregunta);

        db = FirebaseFirestore.getInstance();

        imgPregunta = findViewById(R.id.imgPreguntaElegida3);
        imgPregunta.setVisibility(View.INVISIBLE);
        txtPregunta = findViewById(R.id.txtPonerPregunta3);
        txtOpcion1 = findViewById(R.id.txtOpcion7);
        txtOpcion2 = findViewById(R.id.txtOpcion8);
        txtOpcion3 = findViewById(R.id.txtOpcion9);
        cb1 = findViewById(R.id.cb6);
        cb2 = findViewById(R.id.cb7);
        cb3 = findViewById(R.id.cb8);
        spCategoria = findViewById(R.id.spinnerCategorias4);

        btnVolver = findViewById(R.id.iconoVolver11);
        btnBuscar = findViewById(R.id.btnBuscar3);
        btnGuardar = findViewById(R.id.btnGuardar3);
        btnEliminar = findViewById(R.id.btnEliminar2);

        // Recupero los datos de la otra activity
        Intent intent = getIntent();
        idPregunta = intent.getStringExtra("idPregunta");
        pregunta = intent.getStringExtra("pregunta");
        opciones = intent.getStringArrayListExtra("opciones");
        correcta = intent.getIntExtra("correcta", -1);
        categoria = intent.getStringExtra("categoria");
        imagen = intent.getStringExtra("imagen");

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

        btnVolver. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
            public void onClick(View v) { guardar(); }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categorias_array, R.layout.style_spinner_header);
        adapter.setDropDownViewResource(R.layout.style_spinner_items);
        spCategoria.setAdapter(adapter);
        categoria = categoria.substring(0, 1).toUpperCase() + categoria.substring(1);
        int position = adapter.getPosition(categoria);
        spCategoria.setSelection(position);

        mostrar();
    }

    private void mostrar() {
        imgPregunta.setVisibility(View.INVISIBLE);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagen);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Cargar y mostrar la imagen utilizando Glide
            Glide.with(this)
                    .load(uri)
                    .into(imgPregunta);
        });

        // Lógica para mostrar la pregunta, opciones, checkboxes y la imagen
        txtPregunta.setText(pregunta);
        txtOpcion1.setText(opciones.get(0));
        txtOpcion2.setText(opciones.get(1));
        txtOpcion3.setText(opciones.get(2));

        // Marcar el checkbox correspondiente
        if (correcta == 0) {
            cb1.setChecked(true);
        } else if (correcta == 1) {
            cb2.setChecked(true);
        } else if (correcta == 2) {
            cb3.setChecked(true);
        }

        imgPregunta.setVisibility(View.VISIBLE);
    }

    private void guardar() {
        // Desactiva temporalmente App Check para evitar la advertencia
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.setTokenAutoRefreshEnabled(false);

        if (validarCampos()) {
            // Obtén una instancia de FirebaseStorage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Obtén una referencia a la carpeta en Storage donde se guardarán las imágenes
            StorageReference imgPreguntasRef = storageRef.child("imgPreguntas");

            String pregunta = txtPregunta.getText().toString().trim().toUpperCase();
            String opcion1 = txtOpcion1.getText().toString().trim().toUpperCase();
            String opcion2 = txtOpcion2.getText().toString().trim().toUpperCase();
            String opcion3 = txtOpcion3.getText().toString().trim().toUpperCase();
            boolean cb1Checked = cb1.isChecked();
            boolean cb2Checked = cb2.isChecked();
            boolean cb3Checked = cb3.isChecked();
            String categoria = spCategoria.getSelectedItem().toString().toLowerCase();

            // Crear un mapa con los datos
            Map<String, Object> preguntaMap = new HashMap<>();
            preguntaMap.put("pregunta",pregunta);
            preguntaMap.put("opciones", Arrays.asList(opcion1, opcion2, opcion3));
            preguntaMap.put("categoria", categoria);

            // Determinar la opción correcta y agregarla al mapa
            int respuestaCorrecta = 0;
            if (cb2Checked) respuestaCorrecta = 1;
            else if (cb3Checked) respuestaCorrecta = 2;

            DocumentReference preguntaRef = db.collection("preguntas").document(idPregunta);
            // Subir la imagen a Firebase Storage y luego guardar o actualizar en Firestore
            guardarImagen(imgPreguntasRef, preguntaRef, preguntaMap, respuestaCorrecta);
        }
        firebaseAppCheck.setTokenAutoRefreshEnabled(true);
    }

    private boolean validarCampos() {
        // Validación de campos
        String pregunta = txtPregunta.getText().toString().trim().toUpperCase();
        String opcion1 = txtOpcion1.getText().toString().trim().toUpperCase();
        String opcion2 = txtOpcion2.getText().toString().trim().toUpperCase();
        String opcion3 = txtOpcion3.getText().toString().trim().toUpperCase();
        boolean cb1Checked = cb1.isChecked();
        boolean cb2Checked = cb2.isChecked();
        boolean cb3Checked = cb3.isChecked();
        String categoria = spCategoria.getSelectedItem().toString().toLowerCase();

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

    private void guardarImagen(StorageReference storageRef, DocumentReference preguntaRef, Map<String, Object> preguntaMap, int respuestaCorrecta) {

        drawable = imgPregunta.getDrawable();
        // Convierte la imagen del ImageView a bytes
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Obtén el nombre del archivo
        String nombreArchivo = System.currentTimeMillis() + ".jpg";

        // Obtén la referencia a la imagen en Storage
        StorageReference imagenRef = storageRef.child(nombreArchivo);

        // Sube la imagen a Storage
        UploadTask uploadTask = imagenRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            // Manejar errores en caso de fallo en la carga de la imagen
            mostrarToast("Error al subir la imagen. Intenta de nuevo.");
        }).addOnSuccessListener(taskSnapshot -> {
            // Si la carga de la imagen es exitosa, obten la URL de descarga
            imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Agrega la URL de la imagen al mapa
                preguntaMap.put("imagen", "imgPreguntas/" + nombreArchivo);

                // Agrega la opción correcta al mapa
                preguntaMap.put("correcta", respuestaCorrecta);

                // Actualiza el documento existente con los nuevos datos
                preguntaRef.set(preguntaMap, SetOptions.merge())
                        .addOnSuccessListener(documentReference -> {
                            // Éxito al actualizar el documento
                            mostrarToast("Pregunta actualizada con éxito");
                            vaciarCampos();
                            setResult(RESULT_OK);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // Error al actualizar el documento
                            mostrarToast("Error al actualizar la pregunta. Intenta de nuevo");
                        });
            });
        });

    }


    private void vaciarCampos() {
        // Vaciar todos los campos después de guardar
        txtPregunta.getText().clear();
        txtOpcion1.getText().clear();
        txtOpcion2.getText().clear();
        txtOpcion3.getText().clear();
        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);
        // También puedes reiniciar el spinner a la posición inicial si es necesario
        spCategoria.setSelection(0);
        // Limpiar la imagen
        imgPregunta.setImageDrawable(null);
    }

    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void eliminar() {
        if (imagen != null && !imagen.isEmpty()) {
            if (pregunta != null && !pregunta.isEmpty()) {
                // Obtén una referencia al documento que deseas eliminar en Firestore
                DocumentReference preguntaRef = db.collection("preguntas").document(idPregunta);

                // Elimina el documento
                preguntaRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                // Intenta obtener la referencia de la imagen y elimínala desde Firebase Storage
                                try {
                                    Log.d("imagen:", imagen);
                                    String urlImagen = "gs://triviatrek-2023.appspot.com/" + imagen;
                                    StorageReference imagenRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlImagen);
                                    eliminarImagenFirebaseStorage(imagenRef);

                                    // Éxito al eliminar la pregunta
                                    Toast.makeText(AdminPreguntaActivity.this, "Pregunta eliminada exitosamente", Toast.LENGTH_SHORT).show();

                                    setResult(RESULT_OK);
                                    finish();
                                } catch (IllegalArgumentException e) {
                                    // Error al obtener la referencia de la imagen
                                    Log.e("AdminPreguntaActivity", "Error al obtener la referencia de la imagen", e);
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Fallo al eliminar la pregunta
                                Log.w("AdminPreguntaActivity", "Error al eliminar la pregunta", e);
                                Toast.makeText(AdminPreguntaActivity.this, "Error al eliminar la pregunta", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(AdminPreguntaActivity.this, "No se puede eliminar, pregunta no válida", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("AdminPreguntaActivity", "La URL de la imagen es nula o vacía");
        }
    }

    private void eliminarImagenFirebaseStorage(StorageReference imagenRef) {
        // Elimina la imagen desde Firebase Storage
        imagenRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AdminPreguntaActivity", "Imagen eliminada");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AdminPreguntaActivity", "Error al eliminar la imagen", e);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Obtiene la URI de la imagen seleccionada
            Uri selectedImageUri = data.getData();

            try {
                // Convierte la URI a un InputStream y luego a un Drawable
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Drawable selectedImageDrawable = Drawable.createFromStream(inputStream, selectedImageUri.toString());

                // Muestra la imagen en el ImageView
                imgPregunta.setImageDrawable(selectedImageDrawable);
                imgPregunta.getScaleType();
                imgPregunta.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                Log.e("AdminPreguntaActivity", "Error al cargar la imagen desde la galería", e);
                Toast.makeText(this, "Error al cargar la imagen desde la galería", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

