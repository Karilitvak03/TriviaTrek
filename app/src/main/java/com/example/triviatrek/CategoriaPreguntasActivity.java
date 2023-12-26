package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class CategoriaPreguntasActivity extends AppCompatActivity {

    private ImageView iconoVolver4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_preguntas);

        ImageView imgArte = findViewById(R.id.imgArte);
        ImageView imgEntretenimiento = findViewById(R.id.imgEntretenimiento);
        ImageView imgGeografia = findViewById(R.id.imgGeografia);
        ImageView imgDeporte = findViewById(R.id.imgDeporte);
        ImageView imgCiencia = findViewById(R.id.imgCiencia);
        ImageView imgTodas = findViewById(R.id.imgTodas);

        iconoVolver4 = findViewById(R.id.iconoVolver4);

        imgArte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("arte");
            }
        });

        imgEntretenimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("entretenimiento");
            }
        });

        imgGeografia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("geografia");
            }
        });

        imgDeporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("deportes");
            }
        });

        imgCiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("ciencia");
            }
        });

        imgTodas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jugar("todas");
            }
        });

        iconoVolver4.setOnClickListener(new View.OnClickListener() {
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
