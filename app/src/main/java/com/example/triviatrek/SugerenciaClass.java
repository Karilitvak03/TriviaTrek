package com.example.triviatrek;

public class SugerenciaClass {
    private String nombre;
    private String apellido;
    private String email;
    private String asunto;
    private String pregunta;

    // Constructor vacío requerido para Firestore
    public SugerenciaClass() {
    }

    public SugerenciaClass(String nombre, String apellido, String email, String asunto, String pregunta) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.asunto = asunto;
        this.pregunta = pregunta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }
}

