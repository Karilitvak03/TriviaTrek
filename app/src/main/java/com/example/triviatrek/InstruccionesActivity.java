package com.example.triviatrek;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

public class InstruccionesActivity extends AppCompatActivity {

    private ImageView iconoVolver3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instrucciones_main);


        iconoVolver3 = findViewById(R.id.iconoVolver3);


        iconoVolver3.setOnClickListener(new View.OnClickListener() {
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



