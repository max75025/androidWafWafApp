package com.wafwaf.wafwaf.Manager;



import com.wafwaf.wafwaf.Model.Antivirus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class AVCardManager {
     String GetJsonAV(String apiKey, int startTime, int endTime){
        String json = null;


        try{
            URL url = new URL("https://2waf.com/eventav/" + apiKey + "/" + String.valueOf(startTime) + "/" + String.valueOf(endTime));
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);


            int code = connection.getResponseCode();

            if(code==HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream(),"utf8"));
                String  answer = "";
                String line = null;

                while ((line = reader.readLine()) != null){
                    answer+=line;
                    //System.out.println(line);
                }
                json = answer;
                reader.close();
            }
            connection.disconnect();


        }catch (Exception e){
            //e.printStackTrace();
            System.out.println("Exception occurred");
        }
        return json;
    }

    public List<Antivirus> GetAV(String apiKey, int startTime, int endTime){
        List<Antivirus> avList = new ArrayList<>();
        String json = GetJsonAV(apiKey,startTime, endTime);
        if(json == null || json.equals("null")){ return avList;}
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0; i<jsonArray.length(); i++){
                Antivirus av = new Antivirus();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                av.setApiKey( jsonObject.getString("ApiKey"));
                av.setEventTime( jsonObject.getLong("EventTime"));
                av.setEventType(jsonObject.getString("EventType"));
                av.setFileExt(jsonObject.getString("FileExt"));
                av.setFileName( jsonObject.getString("FileName"));
                av.setFilePath( jsonObject.getString("FilePath"));
                av.setSuspiciousDescription( jsonObject.getString("SuspiciousDescripton"));
                av.setSuspiciousType( jsonObject.getString("SuspiciousType"));
                avList.add(av);
            }

        }catch( Exception e){
            e.printStackTrace();
        }

        return avList;
    }
}
