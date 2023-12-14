package com.example.triviatrek;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PodioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podio);

        Button btnJugar2 = findViewById(R.id.btnJugar2);
        Button btnVolver2 = findViewById(R.id.btnVolver2);

        btnJugar2 .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PodioActivity.this, CategoriaPreguntasActivity.class);
                startActivity(intent);
            }
        });
        btnVolver2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void volverAtras() {
        onBackPressed();
    }
}