package com.example.triviatrek;

import java.util.List;
import com.google.firebase.firestore.DocumentSnapshot;

public class PreguntaClass {
    private String pregunta;
    private List<String> opciones;
    private String imagen;
    private String categoria;
    private int correcta; // Cambiado a tipo int

    public PreguntaClass(DocumentSnapshot document) {
        // Extraer datos del documento y asignarlos a las variables de instancia
        this.pregunta = document.getString("pregunta");
        this.opciones = (List<String>) document.get("opciones");
        this.imagen = document.getString("imagen");
        this.categoria = document.getString("categoria");
        // Parsear la respuesta correcta como un entero
        this.correcta = document.getLong("correcta").intValue();
    }

    public String getPregunta() {
        return pregunta;
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public String getImagen() {
        return imagen;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getCorrecta() {
        return correcta;
    }
}
