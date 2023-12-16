package com.example.triviatrek;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.os.Bundle;

public class PodioActivity extends AppCompatActivity {

    private ImageView iconoVolver6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podio);

        Button btnJugar2 = findViewById(R.id.btnJugar2);
        iconoVolver6 = findViewById(R.id.iconoVolver6);

        btnJugar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PodioActivity.this, CategoriaPreguntasActivity.class);
                startActivity(intent);
            }
        });
        iconoVolver6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAtras();
            }
        });
    }
    private void volverAtras() {
        onBackPressed();
    }
}

