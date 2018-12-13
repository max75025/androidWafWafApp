package com.wafwaf.wafwaf;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.wafwaf.wafwaf.WafLibraryPackege.Antivirus;
import com.wafwaf.wafwaf.WafLibraryPackege.WafLibrary;
import com.wafwaf.wafwaf.WafLibraryPackege.sortEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WafBackgroundIntentService extends IntentService {
    private SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Uri notificationUrl = Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME + "/" + R.raw.notification);

    public static final String ACTION_UPDATE="com.wafwaf.wafwaf.UPDATE";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";

    public static final String UPDATE_ATTACK="NEW ATTACK";
    public static final String UPDATE_AV="NEW AV";


    int idNotification = 200;
    boolean work = true;


    public WafBackgroundIntentService() {
        super("WafBackgroundIntentService");
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
            System.out.println("start Background Intent service");
            int endUnixTime = (int) (System.currentTimeMillis() / 1000L);
            int startUnixTime = endUnixTime - 60 * 60 * 24 * 30;
            int time;

            List<sortEvent> listSE = new ArrayList<>();
            List<Antivirus> listAV = new ArrayList<>();

            DatabaseHandler db = new DatabaseHandler(this);
            WafLibrary wafLibrary = new WafLibrary();


            for (Account account : MainActivity.accountList) {
                time = db.getLastAttackTime(account.name);

                if (time == 0) {
                    listSE = wafLibrary.GetSortEvent(account.apiKey, startUnixTime, endUnixTime);
                } else {
                    listSE = wafLibrary.GetSortEvent(account.apiKey, time + 1, endUnixTime);

                }


                for (sortEvent se : listSE) {
                    db.addAttack(se.IpAddr, se.Country, se.StartTime, se.EndTime, se.ResultTypes, account.name);
                }

                time = db.getLastAVTime(account.name);
                if (time == 0) {
                    listAV = wafLibrary.GetAV(account.apiKey, startUnixTime, endUnixTime);

                } else {
                    listAV = wafLibrary.GetAV(account.apiKey, time+1, endUnixTime);
                }


                for (Antivirus av : listAV) {
                    db.addAV(av.EventTime, av.EventType, av.FileName, av.FileExt, av.FilePath, av.SuspiciousType, av.SuspiciousDescription, account.name);
                }


                if(listSE.size()>0 || listAV.size()>0){
                    sendNotification(account.name);
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
