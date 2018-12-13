package com.wafwaf.wafwaf.WafLibraryPackege;

import android.content.Context;
import android.os.AsyncTask;

import com.wafwaf.wafwaf.Account;
import com.wafwaf.wafwaf.Attack;
import com.wafwaf.wafwaf.DatabaseHandler;
import com.wafwaf.wafwaf.MainActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsyncWafLibrary extends AsyncTask<Void,Void,Void> {

    private Context mContext;

    @Override
    protected Void doInBackground(Void... params) {
       int endUnixTime = (int)(System.currentTimeMillis()/ 1000L);
        int startUnixTime = endUnixTime - 60*60*24*30;
        int time;
        //System.out.println(startUnixTime);
        //System.out.println(endUnixTime);

        List<sortEvent>  listSE = new ArrayList<>();
        List<Antivirus> listAV = new ArrayList<>();

        DatabaseHandler db = new DatabaseHandler(mContext);
        WafLibrary wafLibrary = new WafLibrary();
        //System.out.println(MainActivity.accountList);
        for(Account account: MainActivity.accountList){
            time = db.getLastAttackTime(account.name);
                if( time==0) {
                    listSE = wafLibrary.GetSortEvent(account.apiKey, startUnixTime, endUnixTime);
                } else{
                    listSE =   wafLibrary.GetSortEvent(account.apiKey,time, endUnixTime);
                }
            for(sortEvent se:listSE){
                //System.out.println(se.StartTime);
                db.addAttack(se.IpAddr,se.Country,se.StartTime,se.EndTime,se.ResultTypes,account.name);
            }
            time = db.getLastAVTime(account.name);
            if( time==0) {
                listAV = wafLibrary.GetAV(account.apiKey, startUnixTime, endUnixTime);
            }else {
                listAV = wafLibrary.GetAV(account.apiKey,time,endUnixTime);
            }

            for(Antivirus av:listAV){
                db.addAV(av.EventTime,av.EventType,av.FileName,av.FileExt,av.FilePath,av.SuspiciousType,av.SuspiciousDescription,account.name);
            }


        }




        /*

                    sortEvent se = new WafLibrary().GetSortEvent("5a9ebd7d5f7c8cc17f385f2b36b26181a03fb3dfe78c512cb71f538869a7ea8d6b803385245dfcb698d47be097c82d4759eed12ad106021e2cfa646f905cacfc",1535633627,1538225627).get(0);
                    System.out.println(se.IpAddr);
                    System.out.println(se.Country);
                    System.out.println(se.StartTime);
                    System.out.println(se.EndTime);
                    System.out.println("resultTypes: "+se.ResultTypes);*/

        //db.close();
        return null;
    }
    public AsyncWafLibrary(Context context){
        mContext = context;
    }
}
