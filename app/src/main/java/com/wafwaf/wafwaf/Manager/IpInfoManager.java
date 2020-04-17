package com.wafwaf.wafwaf.Manager;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wafwaf.wafwaf.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IpInfoManager {


    private static final String TAG = "IpInfoManager";


    //{"TimeFirst":"12-10-2018 : 21:50:24","TimeLast":"10-02-2019 : 05:46:49","Countries":["Ukraine"],"TotalAttacks":193,"TotalResults":{"clientside":13,"database":175,"serverside":5}}


    private IpInfo jsonConvert(String json){
        IpInfo info = new IpInfo();
        if (json == null || json.equals("none")) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);

            info.count = jsonObject.getString("TotalAttacks");
            info.timeFirst = jsonObject.getString("TimeFirst");
            info.timeLast = jsonObject.getString("TimeLast");
            info.countries = "";
            JSONArray jsonCountries = jsonObject.getJSONArray("Countries");
            for (int i = 0; i < jsonCountries.length(); i++) {
                info.countries += jsonCountries.getString(i) + "; ";
            }

            info.vectors = "";

            JSONObject jsonVectors = jsonObject.getJSONObject("TotalResults");
            Iterator keysToCopyIterator = jsonVectors.keys();
            List<String> keysList = new ArrayList<String>();
            while (keysToCopyIterator.hasNext()) {
                String key = (String) keysToCopyIterator.next();
                info.vectors += key + "[" + jsonVectors.getString(key) + "]; ";
                //keysList.add(key);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return info;
    }


    private void showIpInfoAlert(final Context context, IpInfo info) {

        String message = context.getString(R.string.attack_count) + info.count + "\n" +
                context.getString(R.string.first_attack) + info.timeFirst + "\n" +
                context.getString(R.string.last_attack) + info.timeLast + "\n" +
                context.getString(R.string.countries) + info.countries + "\n" +
                context.getString(R.string.vectors) + info.vectors + "\n";


        /*AlertDialog.Builder builder = new AlertDialog.Builder(context);*/
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogStyle))
        .setTitle(R.string.ip_info_header)
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


    /**
    * manage and show info about ip address
    */
    public void run(final Context context, String apiKey, String ip) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://2waf.com/logsbyipjson/" + apiKey + "/" + ip ;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       IpInfo info = jsonConvert(response);
                       if (info!=null) {
                           showIpInfoAlert(context, info);
                       }else{
                           Toast.makeText(context, context.getString(R.string.toast_oops), Toast.LENGTH_LONG).show();
                       }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mTextView.setText("That didn't work!");
                Toast.makeText(context, context.getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG).show();
                //massage.show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
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
