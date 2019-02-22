package com.wafwaf.wafwaf.Manager;

import com.wafwaf.wafwaf.Model.SortAttack;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AttackCardManager {

    private String GetJsonEvent(String apiKey, int startTime, int endTime){
        String json = null;
        try{
            URL url = new URL("https://2waf.com/eventclient/" + apiKey + "/" + String.valueOf(startTime) + "/" + String.valueOf(endTime));
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

    private List<attack> GetEvent(String apiKey, int startTime, int endTime){
        List<attack> eventList = new ArrayList<>();
        String json = GetJsonEvent(apiKey,startTime, endTime);
        if(json == null || json.equals("null")){ return eventList;}
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0; i<jsonArray.length(); i++){
                attack event = new attack();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                event.Country = jsonObject.getString("Country");
                event.DataTime = jsonObject.getLong("DateTime");
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

    public List<SortAttack> GetAttack(String apiKey, int startTime, int endTime){

        List<SortAttack> resultList         =  new ArrayList<>();
        List<attack> eventList              =  GetEvent(apiKey, startTime, endTime);
        Map<String, sortAttackTemp> sortMap =  new HashMap<>();


        for (attack ev:eventList) {
            sortAttackTemp eventTemp = sortMap.get(ev.IpAddr);
            if (eventTemp == null) {
                eventTemp = new sortAttackTemp();
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

        for (Map.Entry<String,sortAttackTemp> entry: sortMap.entrySet()){
            SortAttack sortEvent = new SortAttack();
            sortEvent.setIpAddr( entry.getKey());
            sortEvent.setCountry( entry.getValue().Country);
            sortEvent.setStartTime(entry.getValue().StartTime);
            sortEvent.setEndTime( entry.getValue().EndTime);
            //String typeTraceString = "";
            /*for( Map.Entry<String,Integer> entryTypeTrace: entry.getValue().TypeTrace.entrySet()){
                typeTraceString += entryTypeTrace.getKey() + " ";
            }
            sortEvent.TypeTrace = typeTraceString +"; ";*/
            String resultTypesString = "";
            for( Map.Entry<String,Integer> entryResultTypes: entry.getValue().ResultTypes.entrySet()){
                resultTypesString += entryResultTypes.getKey() + "["+String.valueOf(entryResultTypes.getValue())+"]; ";
            }
            sortEvent.setResultTypes(resultTypesString);

            resultList.add(sortEvent);

        }


        return resultList;

    }

}
class sortAttackTemp{
    long StartTime;
    long EndTime;
    //HashMap<String,Integer> TypeTrace;
    HashMap<String,Integer> ResultTypes;
    String Country;

    sortAttackTemp(){}
}

 class attack{
    long DataTime;
    List<String> TypeTrace;
    List<String> ResultTypes;
    String       IpAddr;
    String       Country;



    public attack(){}


    public attack( int DataTime, List<String> TypeTrace, List<String> ResultTypes, String  IpAddr, String  Country){
        this.DataTime = DataTime;
        this.TypeTrace = TypeTrace;
        this.ResultTypes = ResultTypes;
        this.IpAddr = IpAddr;
        this.Country = Country;
    }

}
