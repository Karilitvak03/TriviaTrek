    package com.example.triviatrek;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;
    import android.widget.ImageView;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;

    public class MainActivity extends AppCompatActivity {

        private TextView txtHola;
        private TextView txtCantidadOro;
        private ImageView iconoEditar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Button btnJugar = findViewById(R.id.btnJugar);
            Button btnTop3 = findViewById(R.id.btnTop3);
            Button btnInstrucciones = findViewById(R.id.btnInstrucciones);
            Button btnSalir = findViewById(R.id.btnSalir);
            iconoEditar = findViewById(R.id.iconoEditar);
            txtHola = findViewById(R.id.txtHola);
            txtCantidadOro = findViewById(R.id.txtCantidadOro);

            FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

            if (usuario != null) {
                // UID del chabon logueado
                String uid = usuario.getUid();
                Log.d("Firebase", "UID del usuario: " + uid);

                // Referencia al documento del chabon logueado
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("usuarios").document(uid);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Documento del usuario encontrado, obtén los datos
                                String nombre = document.getString("nombre");
                                int oro = document.getLong("oro").intValue();

                                // Mostrar el nombre y la cantidad de oro en los TextView
                                txtHola.setText("¡Hola, " + nombre + "!");
                                txtCantidadOro.setText("Tienes " + oro + " de oro!");
                            } else {
                                Toast.makeText(MainActivity.this, "El documento del usuario no existe", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error al obtener el documento del usuario: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            btnJugar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, CategoriaPreguntasActivity.class);
                    startActivity(intent);
                }
            });

            btnTop3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, PodioActivity.class);
                    startActivity(intent);
                }
            });

            btnInstrucciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, InstruccionesActivity.class);
                    startActivity(intent);
                }
            });

            iconoEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, EditarPerfilActivity.class);
                    startActivity(intent);

                }
            });

            btnSalir.setOnClickListener(new View.OnClickListener() {
                FirebaseAuth sesion = FirebaseAuth.getInstance();
                @Override
                public void onClick(View view) {
                    sesion.signOut();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            });
        }

        @Override
        protected void onResume() {
            super.onResume();
            actualizarDatosUsuario();
        }

        private void actualizarDatosUsuario() {
            FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

            if (usuario != null) {
                // UID del chabon logueado
                String uid = usuario.getUid();
                Log.d("Firebase", "UID del usuario: " + uid);

                // Referencia al documento del chabon logueado
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("usuarios").document(uid);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Documento del usuario encontrado, obtén los datos
                                String nombre = document.getString("nombre");
                                int oro = document.getLong("oro").intValue();

                                // Mostrar el nombre y la cantidad de oro en los TextView
                                txtHola.setText("¡Hola, " + nombre + "!");
                                txtCantidadOro.setText("Tienes " + oro + " de oro!");
                            } else {
                                Toast.makeText(MainActivity.this, "El documento del usuario no existe", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error al obtener el documento del usuario: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }


    }
