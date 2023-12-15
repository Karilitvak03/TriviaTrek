package com.example.triviatrek;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Button btnVolver3 = findViewById(R.id.btnVolver3);
        Button btnAceptar2 = findViewById(R.id.btnAceptar2);

        btnVolver3.setOnClickListener(new View.OnClickListener() {
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
