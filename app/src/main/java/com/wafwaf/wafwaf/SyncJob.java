package com.wafwaf.wafwaf;

import android.annotation.SuppressLint;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.wafwaf.wafwaf.WafLibraryPackege.Antivirus;
import com.wafwaf.wafwaf.WafLibraryPackege.WafLibrary;
import com.wafwaf.wafwaf.WafLibraryPackege.sortEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class SyncJob extends Job {


    static final String TAG = "sync_job_tag";

    private SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Uri notificationUrl = Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME + "/" + R.raw.notification);

    public static final String ACTION_UPDATE="com.wafwaf.wafwaf.UPDATE";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";

    public static final String UPDATE_ATTACK="NEW ATTACK";
    public static final String UPDATE_AV="NEW AV";

    int idNotification = 300;




    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        MainActivity.syncApiKeysWithFCM();
        return Result.SUCCESS;
    }

    static void schedulePeriodic() {
        new JobRequest.Builder(SyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(true)// авто создание новой задачи после выполения предыдущей
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
               // .setRequirementsEnforced(true)//проверяет что все требования выполняются
                //.startNow()
                .build()
                .schedule();
    }


}





