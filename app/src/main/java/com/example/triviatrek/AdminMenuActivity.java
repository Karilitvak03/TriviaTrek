package com.example.triviatrek;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AdminMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        Button btnVersugerencia = findViewById(R.id.btnVersugerencia);
        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnEliminar = findViewById(R.id.btnEliminar);
        Button btnProbarJuego = findViewById(R.id.btnProbarJuego);

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

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMenuActivity.this, AdminEliminarPreguntaActivity.class);
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


    }
}