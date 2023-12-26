package com.example.triviatrek;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class InstruccionesActivity extends AppCompatActivity {

    private ImageView iconoVolver3;
    private TextView txtTitulo;
    private TextView[] titulos;
    private TextView[] descripciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrucciones);

        iconoVolver3 = findViewById(R.id.iconoVolver3);
        txtTitulo =  findViewById(R.id.txtTitulo);

        titulos = new TextView[]{
                findViewById(R.id.titulo1),
                findViewById(R.id.titulo2),
                findViewById(R.id.titulo3),
                findViewById(R.id.titulo4),
                findViewById(R.id.titulo5)
        };

        descripciones = new TextView[]{
                findViewById(R.id.descripcion1),
                findViewById(R.id.descripcion2),
                findViewById(R.id.descripcion3),
                findViewById(R.id.descripcion4),
                findViewById(R.id.descripcion5)
        };

        iconoVolver3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtTitulo.setText("Bienvenido a TriviaTrek, el emocionante juego de preguntas y respuestas donde tu conocimiento puede llevarte a la gloria o dejarte en la bancarrota.");
        titulos[0].setText("Inicio ↷");
        descripciones[0].setText("Comienzas con 100 unidades de oro.");
        titulos[1].setText("Categorías Específicas ↷");
        descripciones[1].setText("Elige entre las categorias de las preguntas:\n\n" +
                "arte, ciencia, deporte, entretenimiento,y geografia.\n\n" +
                "Al responder correctamente ganarás 10 de oro.\n\n" +
                "AL responder incorrectamente perderás 10 de oro.");
        titulos[2].setText("Categoria 'todas' ↷");
        descripciones[2].setText("Al elegir esta opcion te tocaran preguntas variadas de cualquier categoria.\n\n" +
                "Al responder correctamente ganarás 25 de oro.\n\n" +
                "AL responder incorrectamente perderás 25 de oro.");
        titulos[3].setText("Perdida de oro ↷");
        descripciones[3].setText("TE quedarás sin oro al llegar a 0.\n\n" +
                    "No pierdes más oro al responder incorrectamente.\n\n" +
                    "Vuelve a sumar oro al responder correctamente.");
        titulos[4].setText("Fin del juego ↷");
        descripciones[4].setText("Juega hasta que decidas terminar el juego.\n\n" +
                "Acumula oro para una puntuación alta.");
    }

    public void mostrarDescripcion(View view) {
        int descId = 0; // Identificador de la descripción que se mostrará o ocultará

        for (TextView descripcion : descripciones) {
            descripcion.setVisibility(View.GONE); // Oculto todas las descripciones
        }

        // Obtengo el  ID de la descripción correspondiente al título clickeado
        for (int i = 0; i < titulos.length; i++) {
            if (view.getId() == titulos[i].getId()) {
                descId = descripciones[i].getId();
                break;
            }
        }

        // Con el ID obtenido creo un textView para mostrar el texto
        TextView txtDescripcion = findViewById(descId);

        // activo y deactivo la visibiliadad el texto
        if (txtDescripcion.getVisibility() == View.VISIBLE) {
            txtDescripcion.setVisibility(View.GONE); // Cuando es invisible no ocupa espacio
        } else {
            txtDescripcion.setVisibility(View.VISIBLE);
        }
    }

}
