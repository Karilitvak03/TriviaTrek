package com.example.triviatrek;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MenuFinalActivity extends AppCompatActivity {

    private String categoriaElegida;
    private int cantidadCorrectas;

    private TextView txtOro1;
    private TextView txtOro2;
    private TextView txtOro3;

    private TextView txtPrimerPuesto;
    private TextView txtSegundoPuesto;
    private TextView txtTercerPuesto;

    private TextView txtPuesto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_final);

        // Obtener datos de la actividad anterior (PreguntaActivity)
        Intent intent = getIntent();
        categoriaElegida = intent.getStringExtra("categoria");
        cantidadCorrectas = intent.getIntExtra("correctas", 0);

        txtOro1 = findViewById(R.id.txtOro4);
        txtOro2 = findViewById(R.id.txtOro5);
        txtOro3 = findViewById(R.id.txtOro6);

        txtPrimerPuesto = findViewById(R.id.txtPrimerPuesto2);
        txtSegundoPuesto = findViewById(R.id.txtSegundoPuesto2);
        txtTercerPuesto = findViewById(R.id.txtTercerPuesto2);

        txtPuesto = findViewById(R.id.txtPuesto2);

        findViewById(R.id.iconoVolver10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

                Intent intent = new Intent(MenuFinalActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        TextView txtCategoriaElegida = findViewById(R.id.txtCaegoriaElegida);
        if(categoriaElegida == "todas"){
            txtCategoriaElegida.setText("Preguntas de todas las categorias");
        } else {
            txtCategoriaElegida.setText("Preguntas de " + categoriaElegida);
        }

        // Rrespuestas correctas
        TextView txtTotalCorrectas = findViewById(R.id.txtTotalCorrectas);
        txtTotalCorrectas.setText("Respuestas correctas: " + cantidadCorrectas);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // El usuario está autenticado, obtén su correo electrónico
            String emailUsuarioActual = currentUser.getEmail();

            // Mostrar el podio y el mensaje del puesto
            mostrarUsuarios(emailUsuarioActual);
        } else {
            // El usuario no está autenticado, puedes manejar esta situación según tus necesidades
        }


        Button btnSeguir = findViewById(R.id.btnSeguir);
        btnSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuFinalActivity.this, CategoriaPreguntasActivity.class);
                startActivity(intent);
            }
        });

        // Configurar el botón "DEJAR SUGERENCIA"
        Button btnSugerir = findViewById(R.id.btnSugerir);
        btnSugerir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuFinalActivity.this, SugerenciaActivity.class);
                startActivity(intent);
            }
        });
    }

    private void mostrarUsuarios(String emailUsuarioActual) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Consulta de los chabones ordenados por cantidad de oro
        db.collection("usuarios")
                .orderBy("oro", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documentos = task.getResult().getDocuments();

                        // Muestra el podio después de obtener la lista de documentos
                        mostrarPodio(documentos);

                        // Agrega esta llamada para mostrar el mensaje en txtPuesto
                        mostrarMensajePuesto(documentos, emailUsuarioActual);
                    }
                });
    }

    private void mostrarMensajePuesto(List<DocumentSnapshot> documentos, String emailUsuarioActual) {
        // Buscar la posición del usuario actual en el podio por email
        for (int i = 0; i < documentos.size(); i++) {
            String email = documentos.get(i).getString("email");
            Log.d("DEBUG", "Email en la posición " + i + ": " + email);

            if (email != null && email.equals(emailUsuarioActual)) {
                // Mostrar información del usuario actual
                Log.d("DEBUG", "Usuario encontrado en la posición " + i);

                String nombreUsuarioActual = documentos.get(i).getString("nombre");
                String apellidoUsuarioActual = documentos.get(i).getString("apellido");
                int posicionUsuario = i + 1;  // Sumo 1 porque los índices comienzan desde 0

                String mensaje = nombreUsuarioActual + " " + apellidoUsuarioActual +
                        " estás en el puesto n°: " + posicionUsuario;
                Log.d("DEBUG", "Mensaje a mostrar: " + mensaje);

                txtPuesto.setText(mensaje);
                break;  // Asegúrate de que este break se ejecute después de establecer el texto en txtPuesto
            }
        }

    }


    private void mostrarPodio(List<DocumentSnapshot> documentos) {
        // Verificar la cantidad de documentos obtenidos
        if (documentos.size() >= 1) {
            String nombrePrimerPuesto = documentos.get(0).getString("nombre");
            int oroPrimerPuesto = documentos.get(0).getLong("oro").intValue();
            txtPrimerPuesto.setText(nombrePrimerPuesto);
            txtOro1.setText(String.valueOf(oroPrimerPuesto + " "));
            agregarImagenOro(txtOro1);
        }

        if (documentos.size() >= 2) {
            String nombreSegundoPuesto = documentos.get(1).getString("nombre");
            int oroSegundoPuesto = documentos.get(1).getLong("oro").intValue();
            txtSegundoPuesto.setText(nombreSegundoPuesto);
            txtOro2.setText(String.valueOf(oroSegundoPuesto + " "));
            agregarImagenOro(txtOro2);
        }

        if (documentos.size() >= 3) {
            String nombreTercerPuesto = documentos.get(2).getString("nombre");
            int oroTercerPuesto = documentos.get(2).getLong("oro").intValue();
            txtTercerPuesto.setText(nombreTercerPuesto);
            txtOro3.setText(String.valueOf(oroTercerPuesto + " "));
            agregarImagenOro(txtOro3);
        }
    }

    private void agregarImagenOro(TextView textView) {
        // Pongo imagen de oro y la achico por q es gigante xD
        Drawable imagenOro = getResources().getDrawable(R.drawable.oro);
        int ancho = getResources().getDimensionPixelSize(R.dimen.ancho_icono_oro);
        int alto = getResources().getDimensionPixelSize(R.dimen.alto_icono_oro);
        imagenOro.setBounds(0, 0, ancho, alto);
        textView.setCompoundDrawables(null, null, imagenOro, null);
    }

}
