package com.example.diego.mysecondapp;

/**
 * Created by Gaston on 2/11/2016.
 */

        import android.os.Environment;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.net.URL;
        import java.net.URLConnection;


public class HttpHelper {

    //static final String NUKEURL = "http://192.168.10.50:8080";
    //static final String NUKEURL = "http://demo4354017.mockable.io";
    //static final String NUKEURL = "http://demo4413735.mockable.io";

    public static String get (String url) throws IOException {

        // Bloque para leer la IP desde un archivo

        File dir = Environment.getExternalStorageDirectory();
        //Get the text file
        File file = new File(dir,"ip_placa.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();


        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            text.append(line);
        }

        // Fin lectura de IP desde archivo

        //static final String NUKEURL = "http://192.168.10.50:8080";
        // Seteo la IP con lo que lei del archivo "ip_placa.txt"
        final String NUKEURL = text.toString();


        System.setProperty("http.keepAlive", "false");
        URL requestUrl = new URL(NUKEURL+url);
        URLConnection con = requestUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        int cp;
        try {
            while ((cp = in.read()) != -1) {
                sb.append((char) cp);
            }
        }catch(Exception e){
        }
        String json = sb.toString();
        return json;
    }
}

