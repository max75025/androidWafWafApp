package com.wafwaf.wafwaf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wafwaf.wafwaf.Adapter.RawLogsAdapter;
import com.wafwaf.wafwaf.util.UnixTime;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
        String json = getIntent().getStringExtra("RawLogsJson");
        Log.d(TAG, "onCreate: json" +json);
        List<AttackRawLogs> logs = jsonConvert(json);
        rawLogsAdapter.setItems(logs);


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


                log.setAttackTime(UnixTime.toDate(jsonObject.getString("DateTime")));
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




    private Collection<AttackRawLogs> getRawLogs() {
        return Arrays.asList(
                new AttackRawLogs("1547230206", "serverside", "GET /vulnerabilities/fi/?page=/../../ HTTP/1.0\n" +
                        "Host: 127.0.0.1\n" +
                        "Connection: close\n" +
                        "HERE_WAS_FALSEPOISITIVE_RISK_SO_REPLACED\n" +
                        "HERE_WAS_FALSEPOISITIVE_RISK_SO_REPLACED\n" +
                        "HERE_WAS_FALSEPOISITIVE_RISK_SO_REPLACED\n" +
                        "Connection: close\n" +
                        "Cookie: __tawkuuid=e::localhost::hzOZ1YtuTYvjEey H969bLfuKkTKqhAefMIWAMDbycQ4pesGjNRBX6RYrRCG5Axp::2; csrftoken=b500f78e3c3e41dbf84ab2f9db55d3eb7fcc0ef87b916b865515edd879a47c5bc3360e77cfd9bb020392ee26f5986992147bc0a23140c3f2ae8effe9344c4786; PHPSESSID=5r2ivd71e0nn7ikrdkh9ibieef; security=low\n" +
                        "Upgrade-Insecure-Requests: 1\n" +
                        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36\n" +
                        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36\n" +
                        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36\n" +
                        "X-Forwarded-For: 127.0.0.1"),

                new AttackRawLogs("1547230206", "serverside", "GET /vulnerabilities/fi/?page=/../../ HTTP/1.0\n" +
                        "Host: 127.0.0.1\n" +
                        "Connection: close\n" +
                        "HERE_WAS_FALSEPOISITIVE_RISK_SO_REPLACED\n" +
                        "HERE_WAS_FALSEPOISITIVE_RISK_SO_REPLACED\n" +
                        "HERE_WAS_FALSEPOISITIVE_RISK_SO_REPLACED\n" +
                        "Connection: close\n" +
                        "Cookie: __tawkuuid=e::localhost::hzOZ1YtuTYvjEey H969bLfuKkTKqhAefMIWAMDbycQ4pesGjNRBX6RYrRCG5Axp::2; csrftoken=b500f78e3c3e41dbf84ab2f9db55d3eb7fcc0ef87b916b865515edd879a47c5bc3360e77cfd9bb020392ee26f5986992147bc0a23140c3f2ae8effe9344c4786; PHPSESSID=5r2ivd71e0nn7ikrdkh9ibieef; security=low\n" +
                        "Upgrade-Insecure-Requests: 1\n" +
                        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36\n" +
                        "X-Forwarded-For: 127.0.0.1"),
                new AttackRawLogs("1547230206", "serverside", "GET /vulnerabilities/fi/?page=/../../ HTTP/1.0\n" +
                        "Host: 127.0.0.1\n" +
                        "Connection: close\n" +
                        "HERE_WAS_FALSEPOISITIVE_RISK_SO_REPLACED\n" +
                        "HERE_WAS_FALSEPOISITIVE_RISK_SO_REPLACED\n" +
                        "HERE_WAS_FALSEPOISITIVE_RISK_SO_REPLACED\n" +
                        "Connection: close\n" +
                        "Cookie: __tawkuuid=e::localhost::hzOZ1YtuTYvjEey H969bLfuKkTKqhAefMIWAMDbycQ4pesGjNRBX6RYrRCG5Axp::2; csrftoken=b500f78e3c3e41dbf84ab2f9db55d3eb7fcc0ef87b916b865515edd879a47c5bc3360e77cfd9bb020392ee26f5986992147bc0a23140c3f2ae8effe9344c4786; PHPSESSID=5r2ivd71e0nn7ikrdkh9ibieef; security=low\n" +
                        "Upgrade-Insecure-Requests: 1\n" +
                        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36\n" +
                        "X-Forwarded-For: 127.0.0.1")
        );
    }
}
