package com.example.triviatrek;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MenuAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);

        Button btnVersugerencia = findViewById(R.id.btnVersugerencia);
        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnEliminar = findViewById(R.id.btnEliminar);

        btnVersugerencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdminActivity.this, AdministradorVerSugerenciaActivity.class);
                startActivity(intent);
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdminActivity.this, AdministradorAgregarPreguntaActivity.class);
                startActivity(intent);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdminActivity.this, AdministradorEliminarPreguntaActivity.class);
                startActivity(intent);
            }
        });


    }
}