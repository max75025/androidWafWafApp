package com.wafwaf.wafwaf;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wafwaf.wafwaf.util.CustomTabsHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wafwaf.wafwaf.util.JumpPermissionManagement.GoToSetting;


public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener,RVAttackAdapter.ItemClickListener, AddNewSiteDialogFragment.addSiteDialogListener, deleteAccountDialog.deleteDialogListener {


    static final String wafSite =  "https://wafwaf.tech/statepanel";

    public static String PACKAGE_NAME;
    private static String TAG= "MainActivity";

    public static String FCMtoken = null;
    static String accountName = null;
    static boolean allAccount = true;


    Menu menu;

    SharedPreferences prefs = null;
    //private String[] problemOS = {"Flyme 7" };//"OPM2.17" nexus 5x

    private UpdateBroadcastReceiver mUpdateBroadcastReceiver;

    public static List<Account> accountList = Collections.synchronizedList(new ArrayList<Account>());
    private TabAdapter tbAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private RVAttackAdapter rvAttackAdapter;
    RVAttackAdapter rvAttackAdapter;

    public TabLayout.Tab tabAttack;
    public TabLayout.Tab tabAV;

    DatabaseHandler db;
    int i = 0;

    SentDataToRVAttackAdapter sentDataToRVAttackAdapter;
    SentDataToRVAVAdapter sentDataToRVAVAdapter;

    Intent wafIntentService = null;

    @Override
    public void onDialogDeleteAccountPositiveClick(android.support.v4.app.DialogFragment dialog) {
        String apiKey = db.getApiKey(accountName);
        if (apiKey!=null) {
            if (deleteFCMAccountOnServer(apiKey)) {
                db.deleteAccount(accountName);
                db.deleteAttack(accountName);
                db.deleteAV(accountName);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }else{
                Toast massage = Toast.makeText(this,getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG);
                massage.show();
                return;
            }
        }else {
            Log.e(TAG,"apiKey is null, not found on db");
        }
    }
     @Override
    public void onFinishAddSiteDialog(String name, String apiKey) {
        DatabaseHandler db = new DatabaseHandler(this);
        boolean result = sendApiKeyAndFCMtoken(apiKey);
        if(result){
            result = db.addAccount(name, apiKey);
        }else{
            Toast massage = Toast.makeText(this,getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG);
            massage.show();
            return;
        }
        //System.out.println(result);
        if(result) {
            accountList.add(new Account(name,apiKey));
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            menu.add(R.id.site_list, 1, 0, name);


        }else{
            Toast massage = Toast.makeText(this,getString(R.string.toast_error_add_account), Toast.LENGTH_LONG);
            massage.show();
        }
    }

     public class UpdateBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String update = intent
                    .getStringExtra(WafIntentService.EXTRA_KEY_UPDATE);

            if(allAccount) {

                if (update.equals(WafIntentService.UPDATE_ATTACK)) {
                    List<Attack> attacks = db.getAllAttackByTime(getUnixTimeDaysAgo(1));
                    sentDataToRVAttackAdapter.setData(attacks);
                    tabBaged(tabAttack, "", true);
                }
                if (update.equals(WafIntentService.UPDATE_AV)) {
                    List<AV> avs = db.getAllAVByTime(getUnixTimeDaysAgo(1));
                    sentDataToRVAVAdapter.setData(avs);
                    tabBaged(tabAV, "", true);
                }
            }if(accountName!=null){
                if(update.equals( WafIntentService.UPDATE_ATTACK) ){
                    List<Attack> attacks = db.getAllAccountAttack(accountName);
                    sentDataToRVAttackAdapter.setData(attacks);
                    tabBaged(tabAttack, "", true);
                }
                if(update.equals( WafIntentService.UPDATE_AV) ){
                    List<AV> avs = db.getAllAccountAV(accountName);
                    sentDataToRVAVAdapter.setData(avs);
                    tabBaged(tabAV, "", true);
                }
            }


        }
    }

    public interface SentDataToRVAttackAdapter{
        void setData(List<Attack> data);
        void addAttacks(List<Attack> data);
    }

    public interface SentDataToRVAVAdapter{
        void setData(List<AV> data);
        void addAV(List<AV> data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFCMToken();
        PACKAGE_NAME = getApplicationContext().getPackageName();
        System.out.println(PACKAGE_NAME);

        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
        }

        //set channel for android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri notificationUrl = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.notification);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(getString(R.string.attention_channel_id),
                    getString(R.string.attention_channel_description),
                    NotificationManager.IMPORTANCE_HIGH);


            AudioAttributes notificationAtt = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel.setSound(notificationUrl, notificationAtt);
            channel.enableLights(true);
            channel.setLightColor(Color.argb(255, 75, 0, 130)); //purple color

            mNotificationManager.createNotificationChannel(channel);



        }
        //region old code
        //jobManager for check in background
        //JobManager.create(this).addJobCreator(new SyncJobCreator());
        //SyncJob.schedulePeriodic();


        //white list for android DOZE
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            System.out.println("battery srt");
           Intent intent = new Intent();
            String packageName = getPackageName();
            android.os.PowerManager pm = (android.os.PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }

           *//* if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("важно")
                    .setMessage("добавьте приложение в белый лист для DOZE это поможет доставлять оповещение вовремя")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent();
                            i.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .show();
            }*//*

        }*/
        //endregion

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);



        tbAdapter = new TabAdapter(getSupportFragmentManager());

        rvAttackAdapter = new RVAttackAdapter(this, null);


        Tab1Fragment tab1Fragment = new Tab1Fragment();
        sentDataToRVAttackAdapter = (SentDataToRVAttackAdapter)tab1Fragment;

        Tab2Fragment tab2Fragment = new Tab2Fragment();
        sentDataToRVAVAdapter = (SentDataToRVAVAdapter)tab2Fragment;
        tbAdapter.addFragment(tab1Fragment, getString(R.string.tab1_fragment_name));
        tbAdapter.addFragment(tab2Fragment, getString(R.string.tab2_fragment_name));
        /*adapter.addFragment(new Tab3Fragment(), "Tab 3");*/
        viewPager.setAdapter(tbAdapter);
        tabLayout.setupWithViewPager(viewPager);



        //устанавливаем customView для табов чтоб можно было отобразить красную точку
        tabAttack = tabLayout.getTabAt(0);
        tabAV = tabLayout.getTabAt(1);

        tabAttack.setCustomView(R.layout.baged_tab);
        tabAV.setCustomView(R.layout.baged_tab);
        //tabBaged(tabAttack, "", true);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
               Log.i("TAG", "onTabSelected: " + tab.getPosition());
                tabBaged(tab, "", false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i("TAG", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("TAG", "onTabReselected: " + tab.getPosition());

            }
        });
         db = new DatabaseHandler(this);


        accountList = db.getAllAccount();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        if (accountList.size()>0){

            for(Account account:accountList){
                menu.add(R.id.site_list,1,0, account.name);
            }
            //accountName = accountList.get(0).name;
        }


        menu.findItem(R.id.nav_all_site).setCheckable(true);
        menu.findItem(R.id.nav_all_site).setChecked(true);
        allAccount = true;



        Button siteButton = (Button) navigationView.findViewById(R.id.site_button);
        siteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openSite(wafSite);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);




        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(new Intent(this,WafIntentService.class));
        }else {
            startService(new Intent(this, WafIntentService.class));
        }*/

        wafIntentService = new Intent(this, WafIntentService.class);
        startService(new Intent(this, WafIntentService.class));

        mUpdateBroadcastReceiver = new UpdateBroadcastReceiver();
        IntentFilter updateIntentFilter = new IntentFilter(
                WafIntentService.ACTION_UPDATE);
        updateIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mUpdateBroadcastReceiver, updateIntentFilter);


        //Log.d(TAG,Build.DISPLAY);

        prefs = getSharedPreferences("com.wafwaf.wafwaf", MODE_PRIVATE);
        //attentionAboutProblem(problemOS);
       // addToWhiteList(problemOS);
        //allowRunningInBackgroundOnMeizu(problemOS);
        //syncApiKeysWithFCM();

        getAllowInSetting(this);

    }

    public void showOptionMenu(boolean showMenu){
        if(menu == null)
            return;
        menu.setGroupVisible(R.id.option_menu_group, showMenu);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "you clicked" + rvAttackAdapter.getItem(position) + "on row number" + position ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        showOptionMenu(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_refresh) {
            *//*Toast toast = Toast.makeText(getApplicationContext(),
                    "refresh", Toast.LENGTH_SHORT);
            toast.show();*//*
            if(accountName!=null){
               List<Attack> attacks = db.getAllAttack(accountName);
               List<AV> avs = db.getAllAV(accountName);

                sentDataToRVAttackAdapter.setData(attacks);
                sentDataToRVAVAdapter.setData(avs);
            }

            //return true;
        }*/
        if(id == R.id.action_delete){
            if(accountName!=null){
                deleteAccountDialog dialog = new deleteAccountDialog();
                dialog.show(getSupportFragmentManager(),"Delete");
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_error_find_account), Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();

        if (id!=R.id.nav_add_site){
            item.setCheckable(true);
            item.setChecked(true);
        }

        if (id == R.id.nav_add_site){

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            DialogFragment addDialog = new AddNewSiteDialogFragment();
            addDialog.show(ft,"addSiteDialog");
           /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            menu.add(R.id.site_list,Menu.NONE,0, "testSiteItem");*/
        }

        if (id==R.id.nav_all_site){
            accountName = null;
            allAccount = true;

            //скрываю кнопки в общем меню
            //findViewById(R.id.action_delete).setVisibility(View.INVISIBLE);
            //findViewById(R.id.action_protection_settings).setVisibility(View.INVISIBLE);

            showOptionMenu(false);
            List<Attack> attacks = new ArrayList<>();
            List<AV> avs = new ArrayList<>();
            attacks = db.getAllAttackByTime(getUnixTimeDaysAgo(1));
            avs = db.getAllAVByTime(getUnixTimeDaysAgo(1));

            sentDataToRVAttackAdapter.setData(attacks);
            sentDataToRVAVAdapter.setData(avs);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            onPrepareOptionsMenu(menu);


        }


        if (id==1){

            showOptionMenu(true);
            /*Toast toast = Toast.makeText(getApplicationContext(),
                    item.getTitle(), Toast.LENGTH_SHORT);
            toast.show();*/

            accountName = item.getTitle().toString();
            allAccount = false;
            List<Attack> attacks = new ArrayList<>();
            List<AV> avs = new ArrayList<>();
            attacks = db.getAllAccountAttack(item.getTitle().toString());
            avs = db.getAllAccountAV(item.getTitle().toString());

            sentDataToRVAttackAdapter.setData(attacks);
            sentDataToRVAVAdapter.setData(avs);


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onStart() {
        super.onStart();
        if(wafIntentService!=null){
            startService(wafIntentService);
        }else{
            wafIntentService = new Intent(this, WafIntentService.class);
            startService(wafIntentService);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUpdateBroadcastReceiver);
        //System.out.println("destroy mainActivity");
        Log.d(TAG, "destroy mainActivity");

    }

    @Override
    protected void onStop() {
        super.onStop();
        //System.out.println("stop mainActivity");
        Log.d(TAG, "stop mainActivity");
        if( wafIntentService!=null){
            stopService(wafIntentService);
        }
    }

    protected void closeApp(){
        //stopService( new Intent(this, WafIntentService.class));
        finish();
        System.exit(0);
    }



     public static int getCurrentUnixTime(){
         return (int) (System.currentTimeMillis() / 1000L);
     }

     public static int getUnixTimeDaysAgo(int daysCountAgo){
        return  (int) (System.currentTimeMillis() / 1000L) - 60 * 60 * 24 * daysCountAgo;
     }

    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release +")";
    }

    public static void getFCMToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            System.out.println("getInstanceId failed");
                            return;
                        }

                        // Get new Instance ID token
                         FCMtoken = task.getResult().getToken();

                        // Log and toast
                        String msg = "msg_token_fmt: " + FCMtoken;
                        Log.d(TAG, msg);
                        System.out.println("FCM token: " + FCMtoken);
                        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


                    }
                });

    }

    public static boolean sendApiKeyAndFCMtoken(String apiKey) {
        Log.d(TAG, "sendApiKeyAndFCMtoken: start");
       String stringUrl = "http://91.194.79.90:1313/addNewFCM/";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{


        // Create data variable for sent values to server

        String data = "&" + URLEncoder.encode("ApiKey", "UTF-8") + "="
                + URLEncoder.encode(apiKey, "UTF-8");

        data += "&" + URLEncoder.encode("FCMtoken", "UTF-8")
                + "=" + URLEncoder.encode(FCMtoken, "UTF-8");

        String text = "";
        BufferedReader reader=null;

        // Send data

            // Defined URL  where to send data
            URL url = new URL(stringUrl);

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(500);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String  answer = "";
            String line = null;

            while ((line = reader.readLine()) != null){
                answer+=line;
            }
            reader.close();

            return answer.equals("true");

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        //return true;

    }

    public static boolean deleteFCMAccountOnServer(String apiKey) {
        //Log.d(TAG, "sendApiKeyAndFCMtoken: start");
        String stringUrl = "http://91.194.79.90:1313/deleteFCM/";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            // Create data variable for sent values to server

            String data = "&" + URLEncoder.encode("ApiKey", "UTF-8") + "="
                    + URLEncoder.encode(apiKey, "UTF-8");

            data += "&" + URLEncoder.encode("FCMtoken", "UTF-8")
                    + "=" + URLEncoder.encode(FCMtoken, "UTF-8");

            BufferedReader reader=null;

            // Send data

            // Defined URL  where to send data
            URL url = new URL(stringUrl);

            // Send POST data request
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(500);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String  answer = "";
            String line = null;

            while ((line = reader.readLine()) != null){
                answer+=line;
            }
            reader.close();

            return answer.equals("true");

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

    }

    public static void syncApiKeysWithFCM(){
       // final String json = new Gson().toJson(accountList);
        final String stringUrl = "http://91.194.79.90:1313/syncFCM/";

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            System.out.println("getInstanceId failed");
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try{
                            // Create data variable for sent values to server

                            String data = URLEncoder.encode("FCMtoken", "UTF-8")
                                    + "=" + URLEncoder.encode(FCMtoken, "UTF-8");


                            // Defined URL  where to send data
                            URL url = new URL(stringUrl);

                            // Send POST data request

                            URLConnection conn = url.openConnection();
                            conn.setDoOutput(true);
                            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                            wr.write( data );
                            wr.flush();

                            // Get the server response
                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        }catch(Exception ex)
                        {
                            ex.printStackTrace();

                        }

                        }
                });

}

    public static void tabBaged(TabLayout.Tab tab, String text, boolean visible){

        /*if(tab != null && tab.getCustomView() != null) {
            TextView b = (TextView) tab.getCustomView().findViewById(R.id.badge);
            if(b != null) {
                b.setText( text);
            }
            View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
            if(v != null) {
                v.setVisibility(View.VISIBLE);
            }
        }*/
        if (visible){
            TextView b = (TextView) tab.getCustomView().findViewById(R.id.badge);
            if(b != null) {
                b.setText( text);
            }
            View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
            if(v != null) {
                v.setVisibility(View.VISIBLE);
            }
        }else{
            View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
            if(v != null) {
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void attentionAboutProblem(String[] problemOSVersion){
        if (prefs.getBoolean("firstrun", true)) {
            for(String v: problemOSVersion){
                    if(Build.DISPLAY.contains(v)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.attention)
                                .setMessage(R.string.unstable_os_attention)
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
            }

            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }

    private void addToWhiteList(String[] whiteListOS){
        for(String v:whiteListOS){
            if (Build.DISPLAY.contains(v)){
                //white list for android DOZE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                   Log.d(TAG, "try to add to white list");
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    android.os.PowerManager pm = (android.os.PowerManager) getSystemService(POWER_SERVICE);
                    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                        startActivity(intent);
                    }

                    /*if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("важно")
                                .setMessage("добавьте приложение в белый лист для DOZE это поможет доставлять оповещение вовремя")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent();
                                        i.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                                        startActivity(i);
                                    }
                                })
                                .show();
                    }*/

        }
            }
        }
    }

    private void getAllowInSetting(Activity activity){
        if (prefs.getBoolean("firstrun", true)) {

                        GoToSetting(activity);
                        prefs.edit().putBoolean("firstrun", false).apply();

        }
    }

    private void openSite( String url){
       String browser = CustomTabsHelper.getPackageNameToUse(this);

       if (browser==null){
           Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
           intent.putExtra("url", url);
           startActivity(intent);
       }else{
           CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
           CustomTabsIntent customTabsIntent = builder.build();
           customTabsIntent.launchUrl(this, Uri.parse(url));
       }

    }
}

