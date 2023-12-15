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
                jugar("arte");
            }
        });

        btnEntretenimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("entretenimiento");
            }
        });

        btnGeografia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("geografia");
            }
        });

        btnDeporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("deportes");
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("random");
            }
        });
        btnVolver4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void jugar(String categoria) {
        Intent intent = new Intent(CategoriaPreguntasActivity.this, PreguntaActivity.class);
        intent.putExtra("categoria", categoria);
        startActivity(intent);
    }

}
