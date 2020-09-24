package com.example.assistcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

  private TextView registrarse_login;
  private Button boton_iniciar;
  private EditText id_personal, pass_personal;
  private CheckBox b_mant_sesion;
  private String s_id_personal, s_pass_personal, p_id = "adam", p_pass = "00";
  private boolean estado;
  private static final String STRING_PREFERENCES = "save.session.user";
  private  static final String PREFERENCE_ESTADO_BUTTON_SESION = "estado.button.sesion";
  private static final String TAG = "prueba";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    /*Ocultar el teclado al inicio de la app*/
    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    /*--------------------------------------------------------------------------------------------*/

    if (obtener_estado_button()){
      Intent intent = new Intent(LoginActivity.this, MainActivity.class);
      startActivity(intent);
      finish();
    }

    boton_iniciar = findViewById(R.id.iniciar_sesion);
    registrarse_login = findViewById(R.id.registrarse_login);
    id_personal = findViewById(R.id.identificador_login);
    pass_personal = findViewById(R.id.pass_identificador_login);
    b_mant_sesion = findViewById(R.id.mantener_sesion);

    boton_iniciar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        /*Variables Comprobación de internet*/
        Log.d(TAG, "Hay mamita");
        verificacion();
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

          if (tipoConexionWIFI) {
            /*Código si hay conexión*/

            s_id_personal = id_personal.getText().toString();
            s_pass_personal = pass_personal.getText().toString();

            //Toast.makeText(LoginActivity.this, s_pass_personal, Toast.LENGTH_LONG).show();

            if(s_id_personal.isEmpty() && s_pass_personal.isEmpty()){
              Toast.makeText(LoginActivity.this, "Ingrese los Datos.", Toast.LENGTH_SHORT).show();
              return;
            }else if(s_id_personal.isEmpty()) {
              Toast.makeText(LoginActivity.this, "Ingrese el Identificador.", Toast.LENGTH_SHORT).show();
              return;
            }else if (s_pass_personal.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Ingrese la Contraseña.", Toast.LENGTH_SHORT).show();
              return;
            }else if (s_id_personal.equals(p_id) && s_pass_personal.equals(p_pass)){

              guardarestadoCheckBox();

              Intent intent = new Intent(LoginActivity.this, MainActivity.class);
              startActivity(intent);
              finish();

            }else {
              Toast.makeText(LoginActivity.this, "Los datos no coinciden.", Toast.LENGTH_SHORT).show();
              return;
            }
          }

        }else {
          Toast.makeText(LoginActivity.this, "Revise su conexión a Internet.", Toast.LENGTH_SHORT).show();
          return;
        }
      }
    });

      /*Método para el TextView Registrarse*/
      registrarse_login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(LoginActivity.this, SigninActivity.class);
          startActivity(intent);
        }
      });
      /*----------------------------------------------------------------------------------------*/
    }/*Fin onCreate*/

  /*Verificando el identificador y la contraseña*/
  public void verificacion(){
    String colecciones_raiz [] = {"Empresa", "Institución", "ONG"};

    for (int i = 0;i < colecciones_raiz.length;i++){
      Log.d(TAG, "aqui" + colecciones_raiz[i]);
    }
  }
  /*-----------------------------------------------------------------------------------------------*/

  /*Cambiando estado del botón*/
  public static void change_estado_button (Context c, boolean b){
    SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
    preferences.edit().putBoolean(PREFERENCE_ESTADO_BUTTON_SESION, b).apply();
  }
  /*--------------------------------------------------------------------------------------------*/

 /*Guardar sesión usando SharedPreference*/
  public void guardarestadoCheckBox(){
    SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
    preferences.edit().putBoolean(PREFERENCE_ESTADO_BUTTON_SESION, b_mant_sesion.isChecked()).apply();
  }

  public boolean obtener_estado_button (){
    SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
    return preferences.getBoolean(PREFERENCE_ESTADO_BUTTON_SESION, false);
  }
  /*---------------------------------------------------------------------------------------------------*/

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