package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    private TextView txtHola;
    private TextView txtCantidadOro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnJugar = findViewById(R.id.btnJugar);
        Button btnTop3 = findViewById(R.id.btnTop3);
        Button btnInstrucciones = findViewById(R.id.btnInstrucciones);
        Button btnEditar = findViewById(R.id.btnEditar);
        Button btnSalir = findViewById(R.id.btnSalir);

        txtHola = findViewById(R.id.txtHola);
        txtCantidadOro = findViewById(R.id.txtCantidadOro);

        // Obtener el chabon actual de Firebase
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            // UID del chabon logueado
            String uid = usuario.getUid();

            // Referencia al documento del chabon en Firestore
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("usuarios").document(uid);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Documento del usuario encontrado, obtén los datos
                            String nombre = document.getString("nombre");
                            int oro = document.getLong("oro").intValue();

                            // Mostrar el nombre y la cantidad de oro en los TextView
                            txtHola.setText("Hola, " + nombre);
                            txtCantidadOro.setText("Oro: " + oro);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "no estas", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoriaPreguntasActivity.class);
                startActivity(intent);
            }
        });

        btnTop3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PodioActivity.class);
                startActivity(intent);
            }
        });

        btnInstrucciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InstruccionesActivity.class);
                startActivity(intent);
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
