package com.wafwaf.wafwaf.util;

import android.os.StrictMode;
import android.util.Log;

import com.wafwaf.wafwaf.Model.Response;
import com.wafwaf.wafwaf.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Web {

    public static Response postRequest(String url_string, String data){

        try {
            URL url = new URL(url_string);
            // Send POST data request
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(500);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String answer = "";
            String line = null;

            while ((line = reader.readLine()) != null) {
                answer += line;
            }
            reader.close();

            return new Response(conn.getResponseCode(),answer);

        }catch (Exception e){
            return new Response(0, e.getMessage());

        }
    }

    public static boolean isInternetAvailable(String url_string){
        try {
            URL url = new URL(url_string);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1500);
            System.out.println(conn.getResponseCode());
            return conn.getResponseCode() == HttpURLConnection.HTTP_OK;

        } catch (Exception e){
            e.printStackTrace();
            Log.e("WEB", "checkInternetConnection: error" + e );
            return false;
        }
    }
}
