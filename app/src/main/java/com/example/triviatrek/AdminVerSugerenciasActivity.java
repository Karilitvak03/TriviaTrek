package com.example.triviatrek;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminVerSugerenciasActivity extends AppCompatActivity {

    private ListView mensajes;
    private FirebaseFirestore db;
    private ImageView iconoVolver;

    private List<SugerenciaClass> sugerenciasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ver_sugerencias);

        db = FirebaseFirestore.getInstance();
        mensajes = findViewById(R.id.listViewMensajes);
        iconoVolver = findViewById(R.id.iconoVolver2);

        sugerenciasList = new ArrayList<>(); // Lista de sugerencias

        iconoVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cargarSugerencias();
    }

    private void cargarSugerencias() {
        db.collection("sugerencias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                SugerenciaClass sugerencia = document.toObject(SugerenciaClass.class);
                                sugerenciasList.add(sugerencia);
                            }

                            mostrarSugerenciasEnListView();
                        } else {
                            // Manejar errores
                        }
                    }
                });
    }

    private void mostrarSugerenciasEnListView() {
        List<String> items = new ArrayList<>();

        for (SugerenciaClass sugerencia : sugerenciasList) {
            // Texto para cada elemento de la lista
            String itemText = "Asunto: " + sugerencia.getAsunto().toUpperCase(Locale.ROOT) +
                    "\nDe: " + sugerencia.getNombre() + " " + sugerencia.getApellido() +"\n";
            items.add(itemText);
        }

        // Creo un ArrayAdapter para mostrar los elementos en el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);

        mensajes.setAdapter(adapter);

        // Al hacaer click en cada sugerencia muestra el detalle
        mensajes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarDetallesSugerencia(position);
            }
        });
    }

    private void mostrarDetallesSugerencia(int position) {
        SugerenciaClass sugerenciaSeleccionada = sugerenciasList.get(position);

        // Mensaje para el AlertDialog
        String mensaje ="De: " + sugerenciaSeleccionada.getNombre() + " " + sugerenciaSeleccionada.getApellido() +
                "\nEmail: " + sugerenciaSeleccionada.getEmail() +
                "\n\n→ " + sugerenciaSeleccionada.getPregunta();

        // AlertDialog con los detalles de la sugerencia
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(sugerenciaSeleccionada.getAsunto().toUpperCase());
        builder.setMessage(mensaje);

        // Botón "Aceptar" en el AlertDialog
        builder.setPositiveButton("Aceptar", null);

        // Botón "Eliminar" en el AlertDialog
        builder.setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Eliminar sugerencia de la lista local
                sugerenciasList.remove(position);

                // Eliminar sugerencia de Firestore
                eliminarSugerenciaDeFirestore(sugerenciaSeleccionada);

                // Notificar al adaptador que los datos han cambiado
                ((ArrayAdapter) mensajes.getAdapter()).notifyDataSetChanged();
            }
        });

        builder.show();
    }

    private void eliminarSugerenciaDeFirestore(SugerenciaClass sugerencia) {
        // Eliminar sugerencia de Firestore
        db.collection("sugerencias")
                .whereEqualTo("nombre", sugerencia.getNombre())
                .whereEqualTo("apellido", sugerencia.getApellido())
                .whereEqualTo("email", sugerencia.getEmail())
                .whereEqualTo("asunto", sugerencia.getAsunto())
                .whereEqualTo("pregunta", sugerencia.getPregunta())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Eliminar el documento correspondiente
                                document.getReference().delete();
                            }
                            Toast.makeText(AdminVerSugerenciasActivity.this,
                                    "Sugerencia eliminada con éxito", Toast.LENGTH_SHORT).show();
                            mostrarSugerenciasEnListView(); // Vuelvo a mostrar la lista actualizada
                        } else {
                            Toast.makeText(AdminVerSugerenciasActivity.this,
                                    "Error al eliminar la sugerencia", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
