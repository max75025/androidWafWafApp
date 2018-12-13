package com.wafwaf.wafwaf.WafLibraryPackege;

import com.wafwaf.wafwaf.Attack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class WafLibrary {

   public WafLibrary(){}

    public String GetJsonAV(String apiKey, int startTime, int endTime){
        String json = null;
        // String link = "http://wafwaf.tech/eventav/" + apiKey + "/" + String.valueOf(startTime) + "/" + String.valueOf(endTime);


        try{
            URL url = new URL("http://wafwaf.tech/eventav/" + apiKey + "/" + String.valueOf(startTime) + "/" + String.valueOf(endTime));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
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
                av.ApiKey = jsonObject.getString("ApiKey");
                av.EventTime = jsonObject.getInt("EventTime");
                av.EventType = jsonObject.getString("EventType");
                av.FileExt = jsonObject.getString("FileExt");
                av.FileName = jsonObject.getString("FileName");
                av.FilePath = jsonObject.getString("FilePath");
                av.SuspiciousDescription = jsonObject.getString("SuspiciousDescripton");
                av.SuspiciousType = jsonObject.getString("SuspiciousType");
                avList.add(av);
            }

        }catch( Exception e){
            e.printStackTrace();
        }

        return avList;
    }

    public String GetJsonEvent(String apiKey, int startTime, int endTime){
        String json = null;
        try{
            //URL url = new URL("http://wafwaf.tech/eventclient/5a9ebd7d5f7c8cc17f385f2b36b26181a03fb3dfe78c512cb71f538869a7ea8d6b803385245dfcb698d47be097c82d4759eed12ad106021e2cfa646f905cacfc/1535633627/1538225627");
            URL url = new URL("http://wafwaf.tech/eventclient/" + apiKey + "/" + String.valueOf(startTime) + "/" + String.valueOf(endTime));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);

            int code = connection.getResponseCode();

            if(code==HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream(),"utf8"));
                String  answer = "";
                String line = null;

                while ((line = reader.readLine()) != null){
                    answer+=line;
                }
                json = answer;
                reader.close();
            }
            connection.disconnect();


        }catch (Exception e){
            e.printStackTrace();
        }



        return json;
    }


    public List<Event> GetEvent(String apiKey, int startTime, int endTime){
        List<Event> eventList = new ArrayList<>();
        String json = GetJsonEvent(apiKey,startTime, endTime);
        if(json == null || json.equals("null")){ return eventList;}
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0; i<jsonArray.length(); i++){
                Event event = new Event();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                event.Country = jsonObject.getString("Country");
                event.DataTime = jsonObject.getInt("DateTime");
                event.IpAddr = jsonObject.getString("IpAddr");

                String resultTypesJson = jsonObject.getString("ResultTypes");
                JSONArray rta = new JSONArray(resultTypesJson);
                List<String> listResultTypes = new ArrayList<>();
                for(int j=0;j<rta.length();j++){
                    listResultTypes.add(rta.optString(j));
                }
                event.ResultTypes = listResultTypes;

                String typeTraceJson = jsonObject.getString("TypeTrace");
                JSONArray ttja = new JSONArray(typeTraceJson);
                List<String> listTT = new ArrayList<>();
                for(int j=0;j<ttja.length();j++){
                    listTT.add(ttja.optString(j));
                }
                event.TypeTrace = listTT;

                eventList.add(event);
            }

        }catch( Exception e){
            e.printStackTrace();
            System.out.println(e);
        }

        return eventList;
    }

    public List<sortEvent> GetSortEvent(String apiKey, int startTime, int endTime){

      List<sortEvent> resultList         =  new ArrayList<>();
      List<Event> eventList              =  GetEvent(apiKey, startTime, endTime);
      Map<String, sortEventTemp> sortMap =  new HashMap<>();


        for (Event ev:eventList) {
            sortEventTemp eventTemp = sortMap.get(ev.IpAddr);
            if (eventTemp == null) {
                eventTemp = new sortEventTemp();
                //eventTemp.TypeTrace = new HashMap<>();
                eventTemp.ResultTypes = new HashMap<>();
            }
            eventTemp.Country = ev.Country;
            if (eventTemp.StartTime==0){
                eventTemp.StartTime = ev.DataTime;
            }

            if (eventTemp.EndTime<ev.DataTime){
                eventTemp.EndTime = ev.DataTime;
            }

           /* for (String typeTrace:ev.TypeTrace){

                Integer intTypeTrace = eventTemp.TypeTrace.get(typeTrace);
                if( intTypeTrace == null){
                    intTypeTrace = 1;
                    eventTemp.TypeTrace.put(typeTrace,intTypeTrace);
                }else{
                    eventTemp.TypeTrace.put(typeTrace,intTypeTrace+1);
                }
            }*/

            for (String resultTypes:ev.ResultTypes){
                Integer intRT = eventTemp.ResultTypes.get(resultTypes);
                if( intRT == null){
                    intRT = 1;
                    eventTemp.ResultTypes.put(resultTypes,intRT);
                }else{
                    eventTemp.ResultTypes.put(resultTypes,intRT+1);
                }

            }

            sortMap.put(ev.IpAddr, eventTemp);

        }

        for (Map.Entry<String,sortEventTemp> entry: sortMap.entrySet()){
            sortEvent sortEvent = new sortEvent();
            sortEvent.IpAddr = entry.getKey();
            sortEvent.Country = entry.getValue().Country;
            sortEvent.StartTime = entry.getValue().StartTime;
            sortEvent.EndTime = entry.getValue().EndTime;
            //String typeTraceString = "";
            /*for( Map.Entry<String,Integer> entryTypeTrace: entry.getValue().TypeTrace.entrySet()){
                typeTraceString += entryTypeTrace.getKey() + " ";
            }
            sortEvent.TypeTrace = typeTraceString +"; ";*/
            String resultTypesString = "";
            for( Map.Entry<String,Integer> entryResultTypes: entry.getValue().ResultTypes.entrySet()){
                resultTypesString += entryResultTypes.getKey() + "["+String.valueOf(entryResultTypes.getValue())+"]; ";
            }
            sortEvent.ResultTypes = resultTypesString;

            resultList.add(sortEvent);

        }


        return resultList;

    }


}







