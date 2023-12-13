package com.example.triviatrek;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity {

    private EditText txtNombre, txtApellido, txtEmail, txtClave, txtConfirmarClave;
    private Button btnAceptar, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_main);

        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtEmail = findViewById(R.id.txtEmail2);
        txtClave = findViewById(R.id.txtClave2);
        txtConfirmarClave = findViewById(R.id.txtClave3);

        btnAceptar = findViewById(R.id.btnAceptar);
        btnVolver = findViewById(R.id.btnVovler);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {

                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(txtEmail.getText().toString(), txtConfirmarClave.getText().toString());

                    Toast.makeText(RegistroActivity.this, "Registro trekminado exitosamente", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    //Toast.makeText(RegistroActivity.this, "Error, vuelve a intentarlo porfa!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el botón de Volver para cerrar la actividad
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText().toString().trim();
        String apellido = txtApellido.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String clave = txtClave.getText().toString();
        String confirmarClave = txtConfirmarClave.getText().toString();

        //Validaciones por doquier
        if (TextUtils.isEmpty(nombre)) {
            mostrarMensaje("Ingrese su nombre");
            return false;
        }

        if (TextUtils.isEmpty(apellido)) {
            mostrarMensaje("Ingrese su apellido");
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarMensaje("Ingrese un email válido");
            return false;
        }

        if (TextUtils.isEmpty(clave) || clave.length() < 6) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        if (!clave.equals(confirmarClave)) {
            mostrarMensaje("Las contraseñas no coinciden");
            return false;
        }

        // Si todos los campos estan bien, retorna true :)
        return true;
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
