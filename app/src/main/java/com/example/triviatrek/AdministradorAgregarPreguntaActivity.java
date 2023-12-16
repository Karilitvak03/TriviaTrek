package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdministradorAgregarPreguntaActivity extends AppCompatActivity {

    private EditText txtPonerPregunta, txtOpcion1, txtOpcion2, txtOpcion3;
    private CheckBox cb1, cb2, cb3;
    private Spinner spinnerCategorias;
    private ImageView imgPreguntaElegida;
    private ImageView iconoVolver9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrador_agregar_pregunta);

        txtPonerPregunta = findViewById(R.id.txtPonerPregunta);
        txtOpcion1 = findViewById(R.id.txtOpcion1);
        txtOpcion2 = findViewById(R.id.txtOpcion2);
        txtOpcion3 = findViewById(R.id.txtOpcion3);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        imgPreguntaElegida = findViewById(R.id.imgPreguntaElegida);
        iconoVolver9 = findViewById(R.id.iconoVolver9);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categorias_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adapter);

        Button btnBuscar = findViewById(R.id.btnBuscar);
        Button btnGuardar = findViewById(R.id.btnGuardar);


        iconoVolver9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAtras();
            }
        });

        cb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si cb1 está marcado, desmarcar cb2 y cb3
                if (cb1.isChecked()) {
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                }
            }
        });

        cb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si cb2 está marcado, desmarcar cb1 y cb3
                if (cb2.isChecked()) {
                    cb1.setChecked(false);
                    cb3.setChecked(false);
                }
            }
        });

        cb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si cb3 está marcado, desmarcar cb1 y cb2
                if (cb3.isChecked()) {
                    cb1.setChecked(false);
                    cb2.setChecked(false);
                }
            }
        });

// ...


        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementa la lógica para buscar imágenes aquí...
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    // Aquí debes implementar la lógica para guardar los datos en Firestore.
                    // Utiliza los valores de los campos y realiza la inserción en la colección "preguntas".

                    // Después de guardar, vacía los campos
                    vaciarCampos();
                }
            }
        });
    }

    private void volverAtras() {
        onBackPressed();
    }

    private boolean validarCampos() {
        // Validación de campos
        String pregunta = txtPonerPregunta.getText().toString().trim().toUpperCase();
        String opcion1 = txtOpcion1.getText().toString().trim().toUpperCase();
        String opcion2 = txtOpcion2.getText().toString().trim().toUpperCase();
        String opcion3 = txtOpcion3.getText().toString().trim().toUpperCase();
        boolean cb1Checked = cb1.isChecked();
        boolean cb2Checked = cb2.isChecked();
        boolean cb3Checked = cb3.isChecked();
        String categoria = spinnerCategorias.getSelectedItem().toString();

        // Validación de no vacuidad de campos
        if (pregunta.isEmpty() || opcion1.isEmpty() || opcion2.isEmpty() || opcion3.isEmpty()) {
            mostrarToast("Completa los campos vacíos!");
            return false;
        }

        // Elegir categoría
        List<String> categoriasPermitidas = new ArrayList<>();
        categoriasPermitidas.add("Arte");
        categoriasPermitidas.add("Deportes");
        categoriasPermitidas.add("Geografia");
        categoriasPermitidas.add("Entretenimiento");

        if (!categoriasPermitidas.contains(categoria.toLowerCase())) {
            mostrarToast("Categoría no válida");
            return false;
        }

        // Validación de checkbox
        if (!(cb1Checked || cb2Checked || cb3Checked)) {
            mostrarToast("Selecciona la respuesta correcta");
            return false;
        }

        // Validación de un solo checkbox seleccionado
        if ((cb1Checked && (cb2Checked || cb3Checked)) ||
                (cb2Checked && (cb1Checked || cb3Checked)) ||
                (cb3Checked && (cb1Checked || cb2Checked))) {
            mostrarToast("Selecciona solo una respuesta correcta");
            return false;
        }

        // Si todos los campos están ok = true
        return true;
    }


    private void vaciarCampos() {
        // Vaciar todos los campos después de guardar
        txtPonerPregunta.getText().clear();
        txtOpcion1.getText().clear();
        txtOpcion2.getText().clear();
        txtOpcion3.getText().clear();
        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);
        // También puedes reiniciar el spinner a la posición inicial si es necesario
        spinnerCategorias.setSelection(0);
        // Limpiar la imagen
        imgPreguntaElegida.setImageDrawable(null);
    }

    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
