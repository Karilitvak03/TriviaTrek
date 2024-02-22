package com.example.triviatrek;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class AdminMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        Button btnVersugerencia = findViewById(R.id.btnVersugerencia);
        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnPreguntas = findViewById(R.id.btnPreguntas);
        Button btnProbarJuego = findViewById(R.id.btnProbarJuego);
        Button btnSalir = findViewById(R.id.btnSalir2);

        btnVersugerencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMenuActivity.this, AdminVerSugerenciasActivity.class);
                startActivity(intent);
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMenuActivity.this, AdminAgregarPreguntaActivity.class);
                startActivity(intent);
            }
        });

        btnPreguntas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMenuActivity.this, AdminListaPreguntasActivity.class);
                startActivity(intent);
            }
        });

        btnProbarJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            FirebaseAuth sesion = FirebaseAuth.getInstance();
            @Override
            public void onClick(View view) {
                sesion.signOut();

                Intent intent = new Intent(AdminMenuActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        });


    }
}