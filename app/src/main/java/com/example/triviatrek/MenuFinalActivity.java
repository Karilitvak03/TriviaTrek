package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuFinalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_final);

        Button btnSeguir = findViewById(R.id.btnSeguir);
        Button btnSugerir = findViewById(R.id.btnSugerir);
        Button btnTerminar = findViewById(R.id.btnTerminar);

        btnSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuFinalActivity.this, CategoriaPreguntasActivity.class);
                startActivity(intent);
            }
        });

        btnSugerir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuFinalActivity.this, SugerenciaActivity.class);
                startActivity(intent);
            }
        });

        btnTerminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuFinalActivity.this, ResultadosActivity.class);
                startActivity(intent);
            }
        });
    }

}

