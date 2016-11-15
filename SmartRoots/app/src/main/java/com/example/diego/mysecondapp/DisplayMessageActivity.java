package com.example.diego.mysecondapp;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.example.diego.mysecondapp.HttpHelper.get;

public class DisplayMessageActivity extends AppCompatActivity {

   public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_message);

            Intent intent = getIntent();
            String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

            TextView textView = new TextView(this);
            textView.setTextSize(30);

            String str_result ="inicializo_str_result";
            try {
                str_result= new HttpAsyncTask().execute(message).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            textView.setText("Respuesta: "+str_result);
            ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
            layout.addView(textView);


    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            //return HttpHelper.get(urls[0]+"/status");
            //return enviarMensaje();

            String result = "Placa no conectada";

            try {
                // Aca llamo a una funcion para hacer el GET, y le mando como parametro el mensaje que ingreso el usuario
                result = enviarMensaje(urls[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            //String str = result;
            //serverResponse = result;
            //textView.setText("Respuesta: "+result);
            //DisplayMessageActivity.
            //setserverResponse(result);
            // NO HAGO NADA, ME ALCANZA CON LO QUE HAGO EN EL doInBackground y el onCreate
        }
    }


    public String enviarMensaje(String mensaje) throws JSONException, IOException{

        String msj_a_enviar = "";

        switch(mensaje) {
            case "temperatura":
                msj_a_enviar = "temperatura";
                break;
            case "encendido":
                msj_a_enviar = "vent_on";
                break;
            case "apagado":
                msj_a_enviar = "vent_off";
                break;
        }

        // Si ingresaron el rango horario, envio eso mismo (por ahora no validamos que el formato del rango este ok)
        if (mensaje.startsWith("rango"))
            msj_a_enviar = mensaje;

        // /JSONObject mainObject = new JSONObject(get("/status"));
        // String result = mainObject.toString();

        // Aca hago el GET a la IP que haya seteado en HttpHelper, agregandole al final "/MENSAJE_QUE_INGRESO_EL_USUARIO"
        // Ese Mensaje, sera lo que debera procesar la placa (Por ej: El status que le mandabamos antes para probar)
        String result = "";
        if (!msj_a_enviar.isEmpty())
            result = get("/"+msj_a_enviar);
        else
            return "La orden enviada no es valida. Intente nuevamente...";

        //if (result!=null && result != "" && result != "null")
        if (!result.equals(null) && !result.equals("") && !result.equals("null"))
            return result;
        else
            return "No hay respuesta de la placa";

    }



}