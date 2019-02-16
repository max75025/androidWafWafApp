package com.wafwaf.wafwaf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wafwaf.wafwaf.Adapter.RawLogsAdapter;

import java.util.Arrays;
import java.util.Collection;

public class RawLogsActivity extends AppCompatActivity {

    private RecyclerView logsRecyclerView;
    private RawLogsAdapter rawLogsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_logs);
        getSupportActionBar().hide();
        initRecyclerView();
        loadRawLogs();
    }



    private void initRecyclerView() {
        logsRecyclerView = findViewById(R.id.logs_recycler_view);
        logsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rawLogsAdapter = new RawLogsAdapter();
        logsRecyclerView.setAdapter(rawLogsAdapter);
    }

    private void loadRawLogs() {
        Collection<AttackRawLogs> tweets = getRawLogs();
        rawLogsAdapter.setItems(tweets);
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
