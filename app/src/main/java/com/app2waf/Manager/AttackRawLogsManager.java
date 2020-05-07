package com.app2waf.Manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app2waf.R;
import com.app2waf.RawLogsActivity;

import java.util.Arrays;


public class AttackRawLogsManager {


    String TAG = "AttackRawLogsManager";






    public void run(final Context context, String apiKey, String ip, String startTime, String endTime) {

        Intent intent = new Intent(context, RawLogsActivity.class);
        //intent.putExtra("RawLogsJson", response);

        intent.putExtra("apiKey", apiKey);
        intent.putExtra("ip", ip);
        intent.putExtra("startTime", startTime);
        intent.putExtra("endTime", endTime);

        //intent.putExtra("extra", new byte[400 * 1024]);
        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();

        context.startActivity(intent);



       /* if (startTime == null || endTime==null){
            Toast.makeText(context, context.getString(R.string.toast_oops), Toast.LENGTH_LONG).show();
            Log.d(TAG, "starttime and endTime == null" );
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://2waf.com/eventsjson/" + apiKey + "/" + ip + "/" + startTime + "/" + endTime;
        Log.d(TAG, "run: url - " + url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("null")){
                            Toast.makeText(context, context.getString(R.string.toast_oops), Toast.LENGTH_LONG).show();
                            return;
                        }

                        int responseSize = response.getBytes().length;
                        Log.d(TAG, "size of response:" + responseSize + "bytes");
                        // проверка на огрничение в 256кб
                        int maxLen = 200000;
                        if (responseSize>maxLen){

                            Toast.makeText(context, context.getString(R.string.toast_raw_logs_limit), Toast.LENGTH_LONG).show();
                            return;
                        }
                            Intent intent = new Intent(context, RawLogsActivity.class);
                            intent.putExtra("RawLogsJson", response);
                            //intent.putExtra("extra", new byte[400 * 1024]);
                            //Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                            context.startActivity(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, context.getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);*/

    }






    /*public void run(Context context, ){

    }*/


}




