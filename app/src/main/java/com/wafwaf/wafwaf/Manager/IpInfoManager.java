package com.wafwaf.wafwaf.Manager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.wafwaf.wafwaf.R;
import com.wafwaf.wafwaf.WafLibraryPackege.Antivirus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IpInfoManager {


    private static final String TAG = "IpInfoManager" ;

    String GetJson(String apiKey, String ip) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String json = null;

        //https://wafwaf.tech/logsbyipjson/apiKey/ip
        //https://wafwaf.tech/logsbyipjson/170ecd95a094a7c03a5a7bc20a173afd17c0e015db18ccbbc05395f271d979f459e1120fcab10e6cc290a1b77a9aae7236e3277dcd0393a40c0aa0398696ed8c/94.158.152.76
        try {
            URL url = new URL("http://wafwaf.tech/logsbyipjson/" + apiKey + "/" + ip);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);


            int code = connection.getResponseCode();
            Log.d(TAG, "GetJson code: "+code);
            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf8"));
                String answer = "";
                String line = null;

                while ((line = reader.readLine()) != null) {
                    answer += line;
                    Log.d(TAG, "GetJson line: " +line);
                }
                json = answer;
                reader.close();
            }
            connection.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred");
        }
        return json;
    }


    //{"TimeFirst":"12-10-2018 : 21:50:24","TimeLast":"10-02-2019 : 05:46:49","Countries":["Ukraine"],"TotalAttacks":193,"TotalResults":{"clientside":13,"database":175,"serverside":5}}
    private IpInfo getIpInfo(String apiKey, String ip) {
        IpInfo info = new IpInfo();
        String json = GetJson(apiKey, ip);
        //Log.d(TAG, "getIpInfo: " + json);
       // Log.d(TAG, "getIpInfo apiKey: " + apiKey);
       // Log.d(TAG, "getIpInfo ip: " + ip);
        if (json == null || json.equals("null")) {
            //return info;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);

            info.count = jsonObject.getString("TotalAttacks");
            info.timeFirst = jsonObject.getString("TimeFirst");
            info.timeLast = jsonObject.getString("TimeLast");
            info.countries = "";
            JSONArray jsonCountries = jsonObject.getJSONArray("Countries");
            for(int i=0; i<jsonCountries.length();i++){
                info.countries+= jsonCountries.getString(i) +"; ";
            }

            info.vectors = "";

            JSONObject jsonVectors = jsonObject.getJSONObject("TotalResults");
            Iterator keysToCopyIterator = jsonVectors.keys();
            List<String> keysList = new ArrayList<String>();
            while(keysToCopyIterator.hasNext()) {
                String key = (String) keysToCopyIterator.next();
                info.vectors+= key +"[" + jsonVectors.getString(key) +"]; ";
                //keysList.add(key);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return info;
    }

    public void showIpInfoAlert(final Context context, String apiKey, String ip ) {
        IpInfo info = getIpInfo(apiKey, ip);

        String message = context.getString(R.string.attack_count) + info.count + "\n" +
                context.getString(R.string.first_attack) + info.timeFirst + "\n" +
                context.getString(R.string.last_attack) + info.timeLast + "\n" +
                context.getString(R.string.countries) + info.countries + "\n" +
                context.getString(R.string.vectors) + info.vectors + "\n";


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.ip_info_header)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(R.string.positive_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}

class IpInfo {
    String count;
    String timeFirst;
    String timeLast;
    String countries;
    String vectors;

    IpInfo() {
    }

    IpInfo(String count, String timeFirst, String timeLast, String countries, String vectors) {
        this.count = count;
        this.timeFirst = timeFirst;
        this.timeLast = timeLast;
        this.countries = countries;
        this.vectors = vectors;

    }
}
