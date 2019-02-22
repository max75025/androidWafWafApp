package com.wafwaf.wafwaf.Manager;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wafwaf.wafwaf.R;

import java.util.HashMap;
import java.util.Map;

public class ProtectionSettingManager {
    private Context mContext;
    private RequestQueue queue;
    public static Map<String,String> settingsMap = new HashMap<>();
    static{
        //  none/standard/strong/extreme?
        settingsMap.put("{}","none");
        settingsMap.put("{\"basicrisk\":{\"AttkCount\":5,\"BanTime\":600},\"clientside\":{\"AttkCount\":10,\"BanTime\":300},\"criticalrisk\":{\"AttkCount\":5,\"BanTime\":600},\"database\":{\"AttkCount\":10,\"BanTime\":300},\"fatalrisk\":{\"AttkCount\":5,\"BanTime\":600},\"serverside\":{\"AttkCount\":10,\"BanTime\":300}}","standard");
        settingsMap.put("{\"basicrisk\":{\"AttkCount\":2,\"BanTime\":1200},\"clientside\":{\"AttkCount\":5,\"BanTime\":600},\"criticalrisk\":{\"AttkCount\":2,\"BanTime\":1200},\"database\":{\"AttkCount\":5,\"BanTime\":600},\"fatalrisk\":{\"AttkCount\":2,\"BanTime\":1200},\"serverside\":{\"AttkCount\":5,\"BanTime\":600}}","strong");
        settingsMap.put("{\"basicrisk\":{\"AttkCount\":1,\"BanTime\":1200},\"clientside\":{\"AttkCount\":1,\"BanTime\":1200},\"criticalrisk\":{\"AttkCount\":1,\"BanTime\":1200},\"database\":{\"AttkCount\":1,\"BanTime\":1200},\"fatalrisk\":{\"AttkCount\":1,\"BanTime\":1200},\"serverside\":{\"AttkCount\":1,\"BanTime\":1200}}","extreme");
    }

    String TAG = "ProtectionSettingManager";


    public ProtectionSettingManager(Context context){
        mContext = context;
        queue = Volley.newRequestQueue(context);

    }


    private int getPreset(String json){
        String v= settingsMap.get(json);
        if (v!=null) {
            switch (v) {
                case "none":
                    return 0;
                case "standard":
                    return 1;
                case "strong":
                    return 2;
                case "extreme":
                    return 3;
            }
        }
        return -2;
    }

    private void setPreset(String apiKey, String preset){

        String url = "https://2waf.com/presets/" +apiKey +"/" + preset;
        Log.d(TAG, "run: url - " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext, mContext.getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void showProtectionSettingMenu(Context context, int currentPreset, final String apiKey) {
        // Strings to Show In Dialog with Radio Buttons
        String none = context.getResources().getString(R.string.protection_none) + context.getResources().getString(R.string.protection_none_description);
        String standard = context.getResources().getString(R.string.protection_standard) + context.getResources().getString(R.string.protection_standard_description);
        String strong = context.getResources().getString(R.string.protection_strong) + context.getResources().getString(R.string.protection_strong_description);
        String extreme = context.getResources().getString(R.string.protection_extreme) + context.getResources().getString(R.string.protection_extreme_description);
        //String custom = context.getResources().getString(R.string.protection_custom) + context.getResources().getString(R.string.protection_custom_description);

        final CharSequence[] items = {none, standard, strong, extreme};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select protection Level");
        builder.setSingleChoiceItems(items, currentPreset, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {


                switch (item) {
                    case 0:
                        setPreset(apiKey,"none");
                        break;
                    case 1:
                        setPreset(apiKey,"standard");
                        break;
                    case 2:
                        setPreset(apiKey,"strong");
                        break;
                    case 3:
                       setPreset(apiKey,"extreme");
                        break;


                }
                dialog.dismiss();
            }
        });
        AlertDialog levelDialog = builder.create();
        if (currentPreset == -1){
            Toast.makeText(context, context.getString(R.string.toast_custom_preset_is_set), Toast.LENGTH_LONG).show();
        }
        levelDialog.show();
    }

    //http://wafwaf.tech/static/ban-configs/apiKey
    public void run(final String apiKey){
        String url = "https://2waf.com/static/ban-configs/" + apiKey;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("404 page not found")){
                            Toast.makeText(mContext, mContext.getString(R.string.toast_oops), Toast.LENGTH_LONG).show();
                            return;
                        }
                        int currentPreset = getPreset(response);
                        showProtectionSettingMenu(mContext, currentPreset, apiKey);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext, mContext.getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}
