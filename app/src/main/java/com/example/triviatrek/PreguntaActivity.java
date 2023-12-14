package com.example.triviatrek;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import com.squareup.picasso.Picasso;

public class PreguntaActivity extends AppCompatActivity {

    private TextView txtPregunta;
    private ImageView imgPregunta;
    private RadioGroup radioGroup;
    private RadioButton rbOpcion1;
    private RadioButton rbOpcion2;
    private RadioButton rbOpcion3;
    private Button btnContinuar;

    private FirebaseFirestore db;

    private DocumentSnapshot documento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        db = FirebaseFirestore.getInstance();  // Inicio el coso de Firestore

        String categoria = getIntent().getStringExtra("categoria"); // Me guardo la categoria que se selecciono

        //Vistas
        txtPregunta = findViewById(R.id.txtPregunta);
        imgPregunta = findViewById(R.id.imgPregunta);
        radioGroup = findViewById(R.id.radioGroup1);
        rbOpcion1 = findViewById(R.id.rbOpcion1);
        rbOpcion2 = findViewById(R.id.rbOpcion2);
        rbOpcion3 = findViewById(R.id.rbOpcion3);
        btnContinuar = findViewById(R.id.btnContinuar);

        obtenerPregunta(categoria); // Traigo la pregunta segun la categoria seleccionadaaaa

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == -1) { // No hay opcion seleccionada
                    Toast.makeText(PreguntaActivity.this, "Selecciona una opcion che!", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton = findViewById(selectedId);

                    int opcionElegida = radioGroup.indexOfChild(selectedRadioButton);

                    // Guardo la respuesta correcta de la bd en la variable "correcta", correcto?
                    int correcta = documento.getLong("correcta").intValue();

                    // Comparo la opcion con la respuesta correcta para obviamante saber si es o no correcta xD
                    if (opcionElegida == correcta) {
                        // Respuesta Correcta, mostrar Toast
                        Toast.makeText(PreguntaActivity.this, "Respuesta Correcta", Toast.LENGTH_SHORT).show();
                    } else {
                        // Respuesta Incorrecta, mostrar Toast
                        Toast.makeText(PreguntaActivity.this, "Respuesta Incorrecta", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void obtenerPregunta(String categoria) {
        // Busco una pregunta de la categoria elegida :)
        db.collection("preguntas")
                .whereEqualTo("categoria", categoria)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            // Obtener la pregunta desde el primer documento
                            documento = task.getResult().getDocuments().get(0);

                            actualizarInterfaz(documento); // Actualizo la interfaz con la pregunta, imagen y opciones
                        } else {
                            // Manejar el caso en el que no se obtuvieron preguntas
                        }
                    }
                });
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
}

