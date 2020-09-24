package com.example.assistcontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.net.ConnectivityManagerCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.*;

public class SigninActivity extends AppCompatActivity {

    private RadioButton hombre, mujer;
    private Button registrarse;
    private Spinner mispinner;
    private EditText fecha_nacimiento;
    private EditText pnombre , snombre, papellido, sapellido, id_personal, pass_personal, id_empresa;
    private String s_pnombre, s_snombre = "Sin información", s_papellido, s_sapellido = "Sin información", s_id_personal,
            s_pass_personal, s_hombre, s_mujer, s_id_empresa, s_fecha_nacimiento = "vacio";
    private int dia, mes, ano;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String tipo;
    private static final String TAG = "SigninActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mispinner = findViewById(R.id.mispinner);
        pnombre = findViewById(R.id.primernombre_signin);
        snombre = findViewById(R.id.segundonombre_signin);
        papellido = findViewById(R.id.primerapellido_signin);
        sapellido = findViewById(R.id.segundoapellido_signin);
        id_empresa = findViewById(R.id.identificador_empresa);
        id_personal = findViewById(R.id.identificador_signin);
        pass_personal = findViewById(R.id.pass_identificador_signin);
        hombre = findViewById(R.id.hombre_signin);
        mujer = findViewById(R.id.mujer_signin);
        fecha_nacimiento = findViewById(R.id.fechanacimiento_signin);
        registrarse = findViewById(R.id.registrarse_signin);
        final Intent intent = new Intent(SigninActivity.this, successActivity.class);

        /*Ocultar el teclado al inicio de la app*/
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        /*--------------------------------------------------------------------------------------------*/

        /*Obtener fecha de nacimiento*/
        fecha_nacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                ano = calendario.get(Calendar.YEAR);
                mes = calendario.get(Calendar.MONTH);
                dia = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog fecha = new DatePickerDialog(SigninActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fecha_nacimiento.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                        s_fecha_nacimiento = fecha_nacimiento.getText().toString();
                    }
                }, ano, mes, dia);
                fecha.show();
            }
        });
        /*---------------------------------------------------------------------------------------------------------------------------*/

        ArrayList<String> tipos = new ArrayList<>();
        tipos.add("Empresa");
        tipos.add("Institución");
        tipos.add("ONG");

        ArrayAdapter adp = new ArrayAdapter(SigninActivity.this,R.layout.spinner_item_signin, tipos);
        mispinner.setAdapter(adp);

        mispinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipo = (String) mispinner.getAdapter().getItem(position);

                switch (tipo) {
                    case "Empresa":
                        id_empresa.setHint("Identificador de Empresa");
                        break;
                    case "Institución":
                        id_empresa.setHint("Identificador de Institución");
                        break;
                    case "ONG":
                        id_empresa.setHint("Identificador de ONG");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*----------------MÉTODO REGISTRARSE-----------------*/

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Variables Comprobación de internet*/
                ConnectivityManager cm;
                NetworkInfo ni;
                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                ni = cm.getActiveNetworkInfo();
                boolean tipoConexionWIFI = false;
                /*--------------------------------------------------*/
                if (ni != null) {
                    ConnectivityManager connManager1 = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                    if (mWifi.isConnected()) {
                        tipoConexionWIFI = true;
                    }
                    if (tipoConexionWIFI == true){

                        s_pnombre = pnombre.getText().toString();
                        s_snombre = snombre.getText().toString();
                        s_papellido = papellido.getText().toString();
                        s_sapellido = sapellido.getText().toString();
                        s_id_empresa = id_empresa.getText().toString();
                        s_id_personal = id_personal.getText().toString();
                        s_pass_personal = pass_personal.getText().toString();
                        s_hombre = hombre.getText().toString();
                        s_mujer = mujer.getText().toString();

                        /*----------Si noCheked hombre----------*/
                        if (s_pnombre.isEmpty()){
                            Toast.makeText(SigninActivity.this, "Ingrese su Primer Nombre", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (s_papellido.isEmpty()){
                            Toast.makeText(SigninActivity.this, "Ingrese su Primer Apellido", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (s_fecha_nacimiento == "vacio"){
                            Toast.makeText(SigninActivity.this, "Ingrese su Fecha de Nacimiento", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(s_id_empresa.isEmpty()){
                            Toast.makeText(SigninActivity.this, "Ingrese el ID del Tipo", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (s_id_personal.isEmpty()){
                            Toast.makeText(SigninActivity.this, "Ingrese el ID Personal", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (s_pass_personal.isEmpty()){
                            Toast.makeText(SigninActivity.this, "Ingrese su Contraseña", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (hombre.isChecked() == false && mujer.isChecked() == false){
                            Toast.makeText(SigninActivity.this, "Seleccione su Sexo", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(hombre.isChecked()){

                            Map<String, Object> trabajador = new HashMap<>();
                            trabajador.put("PrimerNombre", s_pnombre);
                            trabajador.put("SegundoNombre", s_snombre);
                            trabajador.put("PrimerApellido", s_papellido);
                            trabajador.put("SegundoApellido", s_sapellido);
                            trabajador.put("FechaNacimiento", s_fecha_nacimiento);
                            trabajador.put("Sexo", s_hombre);
                            trabajador.put("Identificador", s_id_personal);
                            trabajador.put("Password", s_pass_personal);

                            switch (tipo) {

                                //Añadiendo nuevo trabajador a Empresa
                                case "Empresa":

                                    db.collection("Empresa").document(s_id_empresa)
                                            .collection("Personal").document(s_id_personal).set(trabajador)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SigninActivity.this, "Registrado con Éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Error al añadir Trabajador", e);
                                                }
                                            });
                                    break;

                                //Añadiendo nuevo trabajador a Institución
                                case "Institución":

                                    db.collection("Institución").document(s_id_empresa)
                                            .collection("Personal").document(s_id_personal).set(trabajador)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SigninActivity.this, "Registrado con Éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Error al añadir Trabajador", e);
                                                }
                                            });
                                    break;

                                //Añadiendo nuevo trabajador a ONG
                                case "ONG":

                                    db.collection("ONG").document(s_id_empresa)
                                            .collection("Personal").document(s_id_personal).set(trabajador)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SigninActivity.this, "Registrado con Éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Error al añadir Trabajador", e);
                                                }
                                            });
                                    break;
                            }
                            startActivity(intent);
                            finish();

                        }else if (mujer.isChecked()){

                            Map<String, Object> trabajador = new HashMap<>();
                            trabajador.put("PrimerNombre", s_pnombre);
                            trabajador.put("SegundoNombre", s_snombre);
                            trabajador.put("PrimerApellido", s_papellido);
                            trabajador.put("SegundoApellido", s_sapellido);
                            trabajador.put("FechaNacimiento", s_fecha_nacimiento);
                            trabajador.put("Sexo", s_mujer);
                            trabajador.put("Identificador", s_id_personal);
                            trabajador.put("Password", s_pass_personal);

                            switch (tipo) {

                                //Añadiendo nuevo trabajador a Empresa
                                case "Empresa":

                                    db.collection("Empresa").document(s_id_empresa)
                                            .collection("Personal").document(s_id_personal).set(trabajador)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SigninActivity.this, "Registrado con Éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Error al añadir Trabajador", e);
                                                }
                                            });
                                    break;

                                //Añadiendo nuevo trabajador a Institución
                                case "Institución":

                                    db.collection("Institución").document(s_id_empresa)
                                            .collection("Personal").document(s_id_personal).set(trabajador)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SigninActivity.this, "Registrado con Éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Error al añadir Trabajador", e);
                                                }
                                            });
                                    break;

                                //Añadiendo nuevo trabajador a ONG
                                case "ONG":

                                    db.collection("ONG").document(s_id_empresa)
                                            .collection("Personal").document(s_id_personal).set(trabajador)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SigninActivity.this, "Registrado con Éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Error al añadir Trabajador", e);
                                                }
                                            });
                                    break;
                            }
                            startActivity(intent);
                            finish();
                        }
                    }
                }else {
                    Toast.makeText(SigninActivity.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }/*Fin onCreate*/

    /*Función para ocultar la barra de navegación*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        /*| View.SYSTEM_UI_FLAG_FULLSCREEN);*/
    }
    /*----------------------------------------------------------------------*/
}