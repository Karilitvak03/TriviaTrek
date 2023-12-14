package com.example.triviatrek;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AdministradorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrador_agregar_pregunta);
        setContentView(R.layout.administrador_eliminar_pregunta);
        setContentView(R.layout.administrador_sugerencia_pregunta);
    }

}
