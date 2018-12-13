package com.wafwaf.wafwaf;
import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class WafJobService extends com.firebase.jobdispatcher.JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        System.out.println("work start job");
        //startService(new Intent(getApplicationContext(), WafBackgroundIntentService.class));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
