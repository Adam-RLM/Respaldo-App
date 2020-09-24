package com.example.assistcontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.assistcontrol.R.drawable.png_male;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView tview_minombre, tview_sexo, tview_edad, tview_institucion, tview_identificador, tview_conectadoa, tview_ip;
    ImageView imsexo;
    Adaptor_de_los_Fragmentos adaptador_de_los_fragmentos;
    AppCompatImageButton item1, item2, item3;
    Button b_entrada, b_salida, btn_close_sesion;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String s_hora, s_ano, s_mes, s_dia, s_fecha;
    int ano = 0, mes = 0, dia = 0;
    private WifiManager wifi;
    private static final String TAG = "MainActivity";
    private DocumentReference noterefe = db.collection("Institución").document("UNAN, León")
            .collection("Personal").document("adanlo96");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        item1 = findViewById(R.id.registros);
        item2 = findViewById(R.id.marcar);
        item3 = findViewById(R.id.perfil);
        viewPager = findViewById(R.id.contenedor_de_fragmentos);

        adaptador_de_los_fragmentos = new Adaptor_de_los_Fragmentos(getSupportFragmentManager());
        viewPager.setAdapter(adaptador_de_los_fragmentos);

        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPageSelected(int posicion) {
                onChangeTab(posicion);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    } /*-------fin OnCreate--------*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onChangeTab(int posicion) {

        tview_minombre = (TextView)findViewById(R.id.nombre_completo);
        tview_sexo = findViewById(R.id.tv_sexo);
        tview_edad = findViewById(R.id.tv_edad);
        tview_institucion = findViewById(R.id.tv_institucion);
        tview_identificador = findViewById(R.id.tv_identificador);
        tview_conectadoa = findViewById(R.id.red_conectado);
        tview_ip = findViewById(R.id.red_ip);
        imsexo = findViewById(R.id.iv_sexo);
        btn_close_sesion = findViewById(R.id.btn_cerrar_sesion);

        if (posicion == 0){
            item1.setImageDrawable(getDrawable(R.drawable.ic_ico_registro_selec));
            item1.setBackground(getDrawable(R.drawable.color_item_seleccionado));

            item2.setImageDrawable(getDrawable(R.drawable.ic_ico_marcar));
            item2.setBackground(getDrawable(R.drawable.estilo_item_bar));

            item3.setImageDrawable(getDrawable(R.drawable.ic_ico_perfil));
            item3.setBackground(getDrawable(R.drawable.estilo_item_bar));
        }
        if (posicion == 1){

            item1.setImageDrawable(getDrawable(R.drawable.ic_ico_registro));
            item1.setBackground(getDrawable(R.drawable.estilo_item_bar));

            item2.setImageDrawable(getDrawable(R.drawable.ic_ico_marcar_selec));
            item2.setBackground(getDrawable(R.drawable.color_item_seleccionado));

            item3.setImageDrawable(getDrawable(R.drawable.ic_ico_perfil));
            item3.setBackground(getDrawable(R.drawable.estilo_item_bar));
        }
        if (posicion == 2){
            item1.setImageDrawable(getDrawable(R.drawable.ic_ico_registro));
            item1.setBackground(getDrawable(R.drawable.estilo_item_bar));

            item2.setImageDrawable(getDrawable(R.drawable.ic_ico_marcar));
            item2.setBackground(getDrawable(R.drawable.estilo_item_bar));

            item3.setImageDrawable(getDrawable(R.drawable.ic_ico_perfil_selec));
            item3.setBackground(getDrawable(R.drawable.color_item_seleccionado));

            btn_close_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                    LoginActivity.change_estado_button(MainActivity.this, false);
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            });

            /*Obteniendo datos de firebase*/
            noterefe.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String p_nombre = document.get("PrimerNombre").toString();
                            String s_nombre = document.get("SegundoNombre").toString();
                            String p_apellido = document.get("PrimerApellido").toString();
                            String s_apellido = document.get("SegundoApellido").toString();
                            String s_sexo = document.get("Sexo").toString();
                            String s_edad = document.get("FechaNacimiento").toString();
                            String s_institucion = db.collection("Institución").document("UNAN, León").getId().toString();
                            String s_id = document.get("Identificador").toString();
                            Log.d(TAG, s_sexo);
             /*----------------------------------------------------------------------------------------------------------------------------------------*/

                            /*Calcular edad*/
                            String [] fecha_nacimiento = s_edad.split("/");
                            int dia = Integer.parseInt(fecha_nacimiento[0]);
                            int mes = Integer.parseInt(fecha_nacimiento[1]);
                            int anio = Integer.parseInt(fecha_nacimiento[2]);
                            String s_edaduser;

                            Calendar c = Calendar.getInstance();
                            int diaA = c.get(Calendar.DAY_OF_MONTH);
                            int mesA = c.get(Calendar.MONTH);
                            int anioA = c.get(Calendar.YEAR);

                            int edade = anioA - anio;
                            if (mes > mesA){
                                edade--;
                            }else if (mesA == mes){
                                if (dia > diaA){
                                    edade--;
                                }
                            }
                            s_edaduser = String.valueOf(edade);
                            /*------------------------------------------------------------------------*/

                            /*Obtener IP y SSID de la red WIFI*/
                            wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                            try
                            {
                                //Método que devuelve el identificador de servicio de la red Wifi 802.11
                                String red_wifi = wifi.getConnectionInfo().getSSID();
                                int my_ip = wifi.getConnectionInfo().getIpAddress();
                                String my_ipAddress = Formatter.formatIpAddress(my_ip);
                                String msg = "Sin red";

                                if(red_wifi == null)
                                {
                                    tview_conectadoa.setText(msg);
                                }else
                                {
                                    tview_conectadoa.setText(red_wifi);
                                    tview_ip.setText(my_ipAddress);
                                }
                            }catch (Exception e) {
                                e.getMessage();
                            }
                            /*------------------------------------------------------------------------*/

                            /*Mostrando datos en el perfil*/
                            if (s_sexo.equals("Hombre")){
                                tview_sexo.setText("Hombre");
                                imsexo.setImageResource(R.drawable.png_male);
                            }else if (s_sexo.equals("Mujer")){
                                tview_sexo.setText("Mujer");
                                imsexo.setImageResource(R.drawable.png_woman);
                            }

                            tview_identificador.setText(s_id);
                            tview_institucion.setText(s_institucion);
                            tview_edad.setText(s_edaduser + " años");
                            tview_minombre.setText(p_nombre + " " + s_nombre + " " + p_apellido + " " + s_apellido);
                            /*----------------------------------------------------------------------------------------*/
                        } else {
                            Log.d(TAG, "No existe el documento");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

    }

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
    public void met_marca_entrada (View view){
        b_entrada = findViewById(R.id.boton_marcar_entrada);
        b_salida = findViewById(R.id.boton_marcar_salida);

        /*SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat anosd = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat mesd = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat diad = new SimpleDateFormat("dd", Locale.getDefault());
        Date date = new Date();

        s_hora = hora.format(date);
        s_ano = anosd.format(date);
        s_mes = mesd.format(date);
        s_dia = diad.format(date);

        ano = Integer.parseInt(s_ano);
        mes = Integer.parseInt(s_mes);
        dia = Integer.parseInt(s_dia);

        s_fecha = s_dia + s_mes + s_ano;

        Toast toast = Toast.makeText(MainActivity.this, s_hora+"\n"+" "+s_dia+" / "+s_mes+" / "+s_ano, Toast.LENGTH_SHORT);
        toast.show();

        Map<String, Object> entrada = new HashMap<>();
        entrada.put("Año", ano);
        entrada.put("Mes", mes);
        entrada.put("Dia", dia);
        entrada.put("Hora", s_hora);

        db.collection("Institución").document("UNAN, León").collection("Personal")
                .document("adanlo96").collection("Entrada").document(s_fecha)
                .set(entrada)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Marca Entrada Éxito", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "Error al añadir Entrada", e);
            }
        });*/
        Toast toast = Toast.makeText(MainActivity.this, "Marca Entrada", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,450);
        toast.show();

        b_entrada.setVisibility(View.INVISIBLE);
        b_salida.setVisibility(View.VISIBLE);

    }

    public void met_marca_salida (View view){

        b_entrada = findViewById(R.id.boton_marcar_entrada);
        b_salida = findViewById(R.id.boton_marcar_salida);

        /*SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat anosd = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat mesd = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat diad = new SimpleDateFormat("dd", Locale.getDefault());
        Date date = new Date();

        s_hora = hora.format(date);
        s_ano = anosd.format(date);
        s_mes = mesd.format(date);
        s_dia = diad.format(date);

        ano = Integer.parseInt(s_ano);
        mes = Integer.parseInt(s_mes);
        dia = Integer.parseInt(s_dia);

        s_fecha = s_dia + s_mes + s_ano;

        Map<String, Object> salida = new HashMap<>();
        salida.put("Año", ano);
        salida.put("Mes", mes);
        salida.put("Dia", dia);
        salida.put("Hora", s_hora);

        db.collection("Institución").document("UNAN, León").collection("Personal")
                .document("adanlo96").collection("Salida").document(s_fecha)
                .set(salida)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Marca Salida Éxito", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "Error al añadir Salida", e);
            }
        });

        Toast toast = Toast.makeText(MainActivity.this, s_hora+"\n"+" "+s_dia+" / "+s_mes+" / "+s_ano, Toast.LENGTH_SHORT);
        toast.show();*/
        Toast toast = Toast.makeText(MainActivity.this, "Marca Salida", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,450);
        toast.show();

        b_entrada.setVisibility(View.VISIBLE);
        b_salida.setVisibility(View.INVISIBLE);
    }

    /*Función para actualizar los datos de FIREBASE en tiempo real*/

    /*@Override protected void onStart(){
        super.onStart();
        noterefe.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                 String ejemplo = documentSnapshot.getString(campo de la base de datos);
                 Textview.setText("" + ejemplo);
                }else if (e != null){
                    Log.w(TAG, "Hay una excepción", e);
                }
            }
        });
    }*/
    /*------------------------------------------------------------------------------------------------*/
}
