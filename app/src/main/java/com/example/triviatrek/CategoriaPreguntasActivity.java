package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class CategoriaPreguntasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_preguntas);

        ImageButton btnArte = findViewById(R.id.btnArte);
        ImageButton btnEntretenimiento = findViewById(R.id.btnEntretenimiento);
        ImageButton btnGeografia = findViewById(R.id.btnGeografia);
        ImageButton btnDeporte = findViewById(R.id.btnDeporte);
        Button btnRandom = findViewById(R.id.btnRandom);
        Button btnVolver4 = findViewById(R.id.btnVolver4);

        btnArte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreguntaActivity("arte");
            }
        });

        btnEntretenimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreguntaActivity("entretenimiento");
            }
        });

        btnGeografia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreguntaActivity("geografia");
            }
        });

        btnDeporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreguntaActivity("deporte");
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreguntaActivity("random");
            }
        });
        btnVolver4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void volverAtras() {
        onBackPressed();
    }
    private void startPreguntaActivity(String categoria) {
        Intent intent = new Intent(CategoriaPreguntasActivity.this, PreguntaActivity.class);
        intent.putExtra("categoria", categoria);
        startActivity(intent);
    }

}
