package com.wafwaf.wafwaf;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.wafwaf.wafwaf.Manager.AVCardManager;
import com.wafwaf.wafwaf.Manager.AttackCardManager;
import com.wafwaf.wafwaf.Model.Account;
import com.wafwaf.wafwaf.Model.Antivirus;
import com.wafwaf.wafwaf.Model.SortAttack;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class WafIntentService extends IntentService {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Uri notificationUrl = Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME + "/" + R.raw.notification);

    public static final String ACTION_UPDATE="com.wafwaf.wafwaf.UPDATE";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";

    public static final String UPDATE_ATTACK="NEW ATTACK";
    public static final String UPDATE_AV="NEW AV";


    int idNotification = 200;
    boolean work = true;


    public WafIntentService() {
        super("WafIntentService");
    }

    /*@Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        /*android.os.Bundle extras = intent.getExtras();
        String contentText = "проверка наличия новых угроз активна";
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("WafService",
                    "check new data for your protection",
                    NotificationManager.IMPORTANCE_LOW);

            mNotificationManager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this, "WafService")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(1, notification);

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(1, notification);
        }*/

        while (work) {
            int endUnixTime = (int) (System.currentTimeMillis() / 1000L);
            int startUnixTime = endUnixTime - 60 * 60 * 24 * 30;
            int time;

            List<SortAttack> listSE = new ArrayList<>();
            List<Antivirus> listAV = new ArrayList<>();

            DatabaseHandler db = new DatabaseHandler(this);
            //WafLibrary wafLibrary = new WafLibrary();
            AttackCardManager attackManager = new AttackCardManager();
            AVCardManager avManager = new AVCardManager();

            /*listSE = wafLibrary.GetSortEvent(MainActivity.testApiKey, 1538138400,endUnixTime);
            System.out.println(listSE.size());
            System.out.println(wafLibrary.GetJsonEvent(MainActivity.testApiKey, 1538138400,endUnixTime).length());*/
            for (Account account : MainActivity.accountList) {
                //work = false;
                time = db.getLastAttackTime(account.getName());
                //System.out.println("time" + time);
                if (time == 0) {
                    listSE = attackManager.GetAttack(account.getApiKey(), startUnixTime, endUnixTime);
                } else {
                    listSE = attackManager.GetAttack(account.getApiKey(), time + 1, endUnixTime);

                }

                //System.out.println("size" + listSE.size());
                for (SortAttack se : listSE) {
                    db.addAttack(se.getIpAddr(), se.getCountry(), se.getStartTime(), se.getEndTime(), se.getResultTypes(), account.getName());
                }

                //отслыем сообщение обновить данные  в MainActivity
                if(listSE.size()>0 && (account.getName().equals( MainActivity.accountName) || MainActivity.allAccount)){
                    Intent updateIntent = new Intent();
                    updateIntent.setAction(ACTION_UPDATE);
                    updateIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    updateIntent.putExtra(EXTRA_KEY_UPDATE, UPDATE_ATTACK);
                    sendBroadcast(updateIntent);
                    //System.out.println(listSE.size());
                }



                time = db.getLastAVTime(account.getName());
                //System.out.println(time);
                if (time == 0) {
                    listAV = avManager.GetAV(account.getApiKey(), startUnixTime, endUnixTime);

                } else {
                    listAV = avManager.GetAV(account.getApiKey(), time+1, endUnixTime);
                }


                for (Antivirus av : listAV) {
                    db.addAV(av.getEventTimeUnix(), av.getEventType(), av.getFileName(), av.getFileExt(), av.getFilePath(), av.getSuspiciousType(), av.getSuspiciousDescription(), account.getName());
                }
                //отслыем сообщение обновить данные  в MainActivity
                if(listAV.size()>0 && (account.getName().equals( MainActivity.accountName ) || MainActivity.allAccount)){

                    Intent updateIntent = new Intent();
                    updateIntent.setAction(ACTION_UPDATE);
                    updateIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    updateIntent.putExtra(EXTRA_KEY_UPDATE, UPDATE_AV);
                    sendBroadcast(updateIntent);
                }
                //System.out.println(listAV.size());
                if(listSE.size()>0 || listAV.size()>0){
                    //sendNotification(account.name);
                }

            }
            synchronized (this) {
                try {
                    System.out.println("do some task on services");
                    wait(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }



        }


    }

    @Override
    public void onDestroy() {
        work = false;
        super.onDestroy();
    }

    void sendNotification(String account) {

        String contentText = getString(R.string.notification_content_text) + account;

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.attention_channel_id),
                    getString(R.string.attention_channel_description),
                    NotificationManager.IMPORTANCE_HIGH);


            AudioAttributes notificationAtt = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel.setSound(notificationUrl,notificationAtt );



            mNotificationManager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this, "WafAttention")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setShowWhen(true)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(contentIntent);


            Notification notification = builder.build();
            mNotificationManager.notify(idNotification, notification);
            idNotification++;
        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setShowWhen(true)
                    .setWhen(System.currentTimeMillis())
                    //.setDefaults(Notification.DEFAULT_SOUND)
                    .setSound(notificationUrl)
                    .setAutoCancel(true);


            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(contentIntent);

            Notification notification = builder.build();
            mNotificationManager.notify(idNotification, notification);
            idNotification++;
        }
    }
}
