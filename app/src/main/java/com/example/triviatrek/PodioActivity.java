package com.example.triviatrek;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class PodioActivity extends AppCompatActivity {

    private ImageView iconoVolver6;
    private TextView txtPrimerPuesto;
    private TextView txtSegundoPuesto;
    private TextView txtTercerPuesto;
    private TextView txtOro1;
    private TextView txtOro2;
    private TextView txtOro3;
    private TextView txtPuesto;
    private Button btnJugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podio);

        iconoVolver6 = findViewById(R.id.iconoVolver6);

        txtPrimerPuesto = findViewById(R.id.txtPrimerPuesto);
        txtSegundoPuesto = findViewById(R.id.txtSegundoPuesto);
        txtTercerPuesto = findViewById(R.id.txtTercerPuesto);

        txtOro1 = findViewById(R.id.txtOro1);
        txtOro2 = findViewById(R.id.txtOro2);
        txtOro3 = findViewById(R.id.txtOro3);

        txtPuesto = findViewById(R.id.txtPuesto);

        btnJugar = findViewById(R.id.btnJugar2);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // El usuario está autenticado, obtén su correo electrónico
            String emailUsuarioActual = currentUser.getEmail();

            // Mostrar el podio y el mensaje del puesto
            mostrarUsuarios(emailUsuarioActual);
        }

        iconoVolver6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PodioActivity.this, CategoriaPreguntasActivity.class);
                startActivity(intent);
                finish();
            };
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
