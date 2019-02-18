package com.wafwaf.wafwaf.Manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.wafwaf.wafwaf.AttackRawLogs;
import com.wafwaf.wafwaf.R;
import com.wafwaf.wafwaf.RawLogsActivity;
import com.wafwaf.wafwaf.util.UnixTime;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AttackRawLogsManager {


    String TAG = "AttackRawLogsManager";




    //https://wafwaf.tech/eventsjson/apiKey/ip/start/end

    public void run(final Context context, String apiKey, String ip, String startTime, String endTime) {
        if (startTime == null || endTime==null){
            Toast.makeText(context, context.getString(R.string.toast_oops), Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://wafwaf.tech/eventsjson/" + apiKey + "/" + ip + "/" + startTime + "/" + endTime;
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

                        Intent intent = new Intent(context, RawLogsActivity.class);
                        intent.putExtra("RawLogsJson",response);
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onResponse: " +response);
                        context.startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, context.getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }



    /*public void run(Context context, ){

    }*/


}




