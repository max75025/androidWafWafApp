package com.app2waf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app2waf.Adapter.RawLogsAdapter;
import com.app2waf.Model.AttackRawLogs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RawLogsActivity extends AppCompatActivity {

    String TAG = "RawLogsActivity";

    private RecyclerView logsRecyclerView;
    private RawLogsAdapter rawLogsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_logs);
        getSupportActionBar().hide();
        initRecyclerView();
        //loadRawLogs();
        //String json = getIntent().getStringExtra("RawLogsJson");
        //Log.d(TAG, "onCreate: json" +json);

        String apiKey = getIntent().getStringExtra("apiKey");
        String ip = getIntent().getStringExtra("ip");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");

        getAndSetRawLogs(this, apiKey, ip, startTime,endTime);

       // List<AttackRawLogs> logs = jsonConvert(json);
       // rawLogsAdapter.setItems(logs);


    }


    public void getAndSetRawLogs(final Context context , String apiKey, String ip, String startTime, String endTime){
        if (startTime == null || endTime==null){
            Toast.makeText(context, context.getString(R.string.toast_oops), Toast.LENGTH_LONG).show();
            Log.d(TAG, "starttime and endTime == null" );
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://2waf.com/eventsjson/" + apiKey + "/" + ip + "/" + startTime + "/" + endTime;
        Log.d(TAG, "run: url - " + url);
        String resultResponse;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //resultResponse =  response;

                        List<AttackRawLogs> logs = jsonConvert(response);
                        rawLogsAdapter.setItems(logs);


                        ProgressBar progressBar =  findViewById(R.id.pb_raw_logs);
                        RecyclerView rv =  findViewById(R.id.logs_recycler_view);
                        progressBar.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);

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



    private void initRecyclerView() {
        logsRecyclerView = findViewById(R.id.logs_recycler_view);
        logsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rawLogsAdapter = new RawLogsAdapter();
        logsRecyclerView.setAdapter(rawLogsAdapter);
    }

    /*private void loadRawLogs() {
        Collection<AttackRawLogs> logs = getRawLogs();
        rawLogsAdapter.setItems(logs);
    }*/

    private String encodeBase64(String data) {
        byte[] d = Base64.decode(data, Base64.DEFAULT);
        String text = new String(d, StandardCharsets.UTF_8);
        return text;
    }

    /*
     * {"DateTime":1548941600,
     * "TypeTrace":["union","select","unionselect","database"], -  техническая информация, не показывать
     * "ResultTypes":["database"],
     * "Request":"R0VUIC8ndW5pb24gc2VsZWN0IEhUVFAvMS4wDQpIb3N0OiBraXBpay5vbmFmdC5lZHUudWENCkNvbm5lY3Rpb246IGNsb3NlDQpIRVJFX1dBU19GQUxTRVBPSVNJVElWRV9SSVNLX1NPX1JFUExBQ0VECkhFUkVfV0FTX0ZBTFNFUE9JU0lUSVZFX1JJU0tfU09fUkVQTEFDRUQKSEVSRV9XQVNfRkFMU0VQT0lTSVRJVkVfUklTS19TT19SRVBMQUNFRApDYWNoZS1Db250cm9sOiBtYXgtYWdlPTANCkNvbm5lY3Rpb246IGNsb3NlDQpDb29raWU6IF9nYT1HQTEuMy4xOTg2MTg0MDEyLjE1NDQ5NDA2MzE7IDU5YjU0NTJmZDRjMDc4MjYxMDcyZTliZGM5ZDdiYTBhPTJ1YTZsYXVmbm1mZGFsbTkxbm42bTJ2NXA1OyByZWRpcmVjdD1wYXNzZWQ7IG5vdGJvdD1mZWI1MzQxZWMxYzc5YTMyNTUxMTFiMzA3OTdiYmRhOQ0KUmVmZXJlcjogaHR0cDovL2tpcGlrLm9uYWZ0LmVkdS51YS8ndW5pb24gc2VsZWN0DQpVcGdyYWRlLUluc2VjdXJlLVJlcXVlc3RzOiAxDQpVc2VyLUFnZW50OiBNb3ppbGxhLzUuMCAoTWFjaW50b3NoOyBJbnRlbCBNYWMgT1MgWCAxMF8xM182KSBBcHBsZVdlYktpdC81MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBDaHJvbWUvNzEuMC4zNTc4Ljk4IFNhZmFyaS81MzcuMzYNClgtRm9yd2FyZGVkLUZvcjogOTQuMTU4LjE1Mi43Ng0KDQo=",
     * "IpAddr":"94.158.152.76",
     * "Country":"Ukraine"}
     * */
    private List<AttackRawLogs> jsonConvert(String json) {
        ArrayList<AttackRawLogs> logs = new ArrayList<>();
        if (json == null || json.equals("null")) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray(json);


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AttackRawLogs log = new AttackRawLogs();


                log.setAttackTime(jsonObject.getLong("DateTime"));
                JSONArray jsonTypes = jsonObject.getJSONArray("ResultTypes");
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < jsonTypes.length(); j++) {
                    sb.append(jsonTypes.getString(j)+"; ");

                }
                log.setAttackDirection(sb.toString());
                log.setAttackPackage(encodeBase64(jsonObject.getString("Request")));
                logs.add(log);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return logs;
    }





}
