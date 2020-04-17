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
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.app.FragmentTransaction;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wafwaf.wafwaf.Adapter.RVAttackAdapter;
import com.wafwaf.wafwaf.Manager.ProtectionSettingManager;
import com.wafwaf.wafwaf.Model.AV;
import com.wafwaf.wafwaf.Model.Account;
import com.wafwaf.wafwaf.Model.Attack;
import com.wafwaf.wafwaf.Model.Response;
import com.wafwaf.wafwaf.util.CustomTabsHelper;
import com.wafwaf.wafwaf.util.SnackbarHelper;
import com.wafwaf.wafwaf.util.UnixTime;
import com.wafwaf.wafwaf.util.Web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wafwaf.wafwaf.util.JumpPermissionManagement.GoToSetting;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddNewSiteDialogFragment.addSiteDialogListener, deleteAccountDialog.deleteDialogListener {


    public static String PACKAGE_NAME;
    private static String TAG = "MainActivity";

    public static String FCMtoken = null;
    static String accountName = null;
    static boolean allAccount = true;
    View mainActivityView = null;

    private Context mContext;


    Menu menu;

    SharedPreferences prefs = null;
    //private String[] problemOS = {"Flyme 7" };//"OPM2.17" nexus 5x

    private UpdateBroadcastReceiver mUpdateBroadcastReceiver;


    private int mCurrentAttackItemPosition;

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
    public void onDialogDeleteAccountPositiveClick(androidx.fragment.app.DialogFragment dialog) {
        /*if (!isInternetAvailable()) {
            Toast massage = Toast.makeText(this, getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG);
            massage.show();
            return;
        }*/

        String apiKey = db.getApiKey(accountName);
        if (apiKey != null) {
            deleterFromPushServer(apiKey, FCMtoken, accountName);
        } else {
            SnackbarHelper.showSnackbar(
                    mainActivityView,
                    getString(R.string.toast_error_delete_account),
                    Snackbar.LENGTH_LONG,
                    R.color.colorWhite,
                    R.color.colorAttention);
        }
        /*String apiKey = db.getApiKey(accountName);
        if (apiKey != null) {
            if (deleteFCMAccountOnServer(apiKey)) {
                db.deleteAccount(accountName);
                db.deleteAttack(accountName);
                db.deleteAV(accountName);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                Toast massage = Toast.makeText(this, getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG);
                massage.show();
                return;
            }
        } else {
            Log.e(TAG, "apiKey is null, not found on db");
            Toast massage = Toast.makeText(this, getString(R.string.toast_oops), Toast.LENGTH_LONG);
            massage.show();
        }*/
    }

    @Override
    public void onFinishAddSiteDialog(String name, String apiKey) {
        registerOnPushServer(apiKey, FCMtoken, name);


      /*  if (!isInternetAvailable()) {
            Toast massage = Toast.makeText(this, getString(R.string.toast_error_ethernet_conn), Toast.LENGTH_LONG);
            massage.show();
            return;
        }

        DatabaseHandler db = new DatabaseHandler(this);
        boolean result = sendApiKeyAndFCMtoken(apiKey);
        if (result) {
            result = db.addAccount(name, apiKey);
        } else {
            Toast massage = Toast.makeText(this, getString(R.string.toast_error_server_conn), Toast.LENGTH_LONG);
            massage.show();
            return;
        }
        //System.out.println(result);
        if (result) {
            accountList.add(new Account(name, apiKey));
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            SubMenu smenu = menu.findItem(R.id.site_list_item).getSubMenu();
            smenu.add(R.id.site_list, 1, 0, name);

            //change text in view
            //TextView emptyAttack = findViewById(R.id.empty_attack_text);
            //TextView emptyAV = findViewById(R.id.empty_av_text);
            //emptyAttack.setText(R.string.empty_attack_string);
            //emptyAV.setText(R.string.empty_av_string);


        } else {
            Toast massage = Toast.makeText(this, getString(R.string.toast_error_add_account), Toast.LENGTH_LONG);
            massage.show();
        }*/
    }


    public class UpdateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String update = intent
                    .getStringExtra(WafIntentService.EXTRA_KEY_UPDATE);

            if (allAccount) {

                if (update.equals(WafIntentService.UPDATE_ATTACK)) {
                    List<Attack> attacks = db.getAllAttackByTime(UnixTime.getUnixTimeDaysAgo(1));
                    sentDataToRVAttackAdapter.setData(attacks);
                    tabBaged(tabAttack, "", true);
                }
                if (update.equals(WafIntentService.UPDATE_AV)) {
                    List<AV> avs = db.getAllAVByTime(UnixTime.getUnixTimeDaysAgo(1));
                    sentDataToRVAVAdapter.setData(avs);
                    tabBaged(tabAV, "", true);
                }
            }
            if (accountName != null) {
                if (update.equals(WafIntentService.UPDATE_ATTACK)) {
                    List<Attack> attacks = db.getAllAccountAttack(accountName);
                    sentDataToRVAttackAdapter.setData(attacks);
                    tabBaged(tabAttack, "", true);
                }
                if (update.equals(WafIntentService.UPDATE_AV)) {
                    List<AV> avs = db.getAllAccountAV(accountName);
                    sentDataToRVAVAdapter.setData(avs);
                    tabBaged(tabAV, "", true);
                }
            }


        }
    }

    public interface SentDataToRVAttackAdapter {
        void setData(List<Attack> data);

        void addAttacks(List<Attack> data);
    }

    public interface SentDataToRVAVAdapter {
        void setData(List<AV> data);

        void addAV(List<AV> data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getFCMToken();
        PACKAGE_NAME = getApplicationContext().getPackageName();
        //System.out.println(PACKAGE_NAME);
        mContext = this;
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
            channel.setLightColor(Color.argb(255, 255, 0, 255)); //purple color

            mNotificationManager.createNotificationChannel(channel);


        }


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);


        tbAdapter = new TabAdapter(getSupportFragmentManager());


        rvAttackAdapter = new RVAttackAdapter(this, null);


        Tab1Fragment tab1Fragment = new Tab1Fragment();
        sentDataToRVAttackAdapter = tab1Fragment;

        Tab2Fragment tab2Fragment = new Tab2Fragment();
        sentDataToRVAVAdapter = tab2Fragment;
        tbAdapter.addFragment(tab1Fragment, getString(R.string.tab1_fragment_name));
        tbAdapter.addFragment(tab2Fragment, getString(R.string.tab2_fragment_name));
        viewPager.setAdapter(tbAdapter);
        tabLayout.setupWithViewPager(viewPager);


        //устанавливаем customView для табов чтоб можно было отобразить красную точку
        tabAttack = tabLayout.getTabAt(0);
        tabAV = tabLayout.getTabAt(1);


        tabAttack.setCustomView(R.layout.baged_tab);
        tabAV.setCustomView(R.layout.baged_tab);


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

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem sm = menu.findItem(R.id.site_list_item);
        SpannableString s = new SpannableString(sm.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.SubMenuTextStyle), 0, s.length(), 0);
        sm.setTitle(s);

        SubMenu smenu = menu.findItem(R.id.site_list_item).getSubMenu();
        if (accountList.size() > 0) {

            for (Account account : accountList) {
                smenu.add(R.id.site_list, 1, 0, account.getName());
            }
        }


        menu.findItem(R.id.nav_all_site).setCheckable(true);
        menu.findItem(R.id.nav_all_site).setChecked(true);
        allAccount = true;




        Button siteButton = navigationView.findViewById(R.id.site_button);
        siteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openSite(getString(R.string.link_panel));
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);




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

        //showDebugWindow(this);

        mainActivityView = navigationView;
        //Snackbar.make(view, "hello world", Snackbar.LENGTH_LONG).show();
        //SnackbarHelper.showSnackbar(view, "hello world", Snackbar.LENGTH_LONG, R.color.colorWhite, R.color.colorAccentDark);



        if (prefs.getBoolean("firstRun", true)) {
            showLicenseAgreement();
        }else {
            if (accountList.size() == 0) {
                showAddSiteDialog();
            }
        }

    }

    private void showAddSiteDialog(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment addDialog = new AddNewSiteDialogFragment();
        addDialog.show(ft, "addSiteDialog");
    }


    public void showOptionMenu(boolean showMenu) {
        if (menu == null)
            return;
        menu.setGroupVisible(R.id.option_group_settings, showMenu);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        switch (item.getItemId()) {
            case R.id.action_delete:
                if (accountName != null) {
                    deleteAccountDialog dialog = new deleteAccountDialog();
                    dialog.show(getSupportFragmentManager(), "Delete");
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_error_find_account), Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.action_protection_settings:
                new ProtectionSettingManager(mContext).run(new DatabaseHandler(mContext).getApiKey(accountName));
                //showRawLogs();
                break;
            case R.id.action_chat:
                openSite(getString(R.string.link_tawk_chat));
                break;
            case R.id.action_telegram:
                openSite(getString(R.string.link_telegram));
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    /*private void showProtectionSettingMenu() {
        // Strings to Show In Dialog with Radio Buttons
        String none = getResources().getString(R.string.protection_none) + getResources().getString(R.string.protection_none_description);
        String standard = getResources().getString(R.string.protection_standard) + getResources().getString(R.string.protection_standard_description);
        String strong = getResources().getString(R.string.protection_strong) + getResources().getString(R.string.protection_strong_description);
        String extreme = getResources().getString(R.string.protection_extreme) + getResources().getString(R.string.protection_extreme_description);


        final CharSequence[] items = {none, standard, strong, extreme};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select protection Level");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {


                switch (item) {
                    case 0:
                        // Your code when first option seletced
                        break;
                    case 1:
                        // Your code when 2nd  option seletced

                        break;
                    case 2:
                        // Your code when 3rd option seletced
                        break;

                }
                dialog.dismiss();
            }
        });
        AlertDialog levelDialog = builder.create();
        levelDialog.show();
    }*/

    private void showRawLogs() {
        Intent intent = new Intent(MainActivity.this, RawLogsActivity.class);
        startActivity(intent);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();

        if (id != R.id.nav_add_site) {
            item.setCheckable(true);
            item.setChecked(true);
        }

        if (id == R.id.nav_add_site) {

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            DialogFragment addDialog = new AddNewSiteDialogFragment();
            addDialog.show(ft, "addSiteDialog");
           /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            menu.add(R.id.site_list,Menu.NONE,0, "testSiteItem");*/
        }

        if (id == R.id.nav_all_site) {
            accountName = null;
            allAccount = true;

            //скрываю кнопки в общем меню
            //findViewById(R.id.action_delete).setVisibility(View.INVISIBLE);
            //findViewById(R.id.action_protection_settings).setVisibility(View.INVISIBLE);

            showOptionMenu(false);
            List<Attack> attacks = new ArrayList<>();
            List<AV> avs = new ArrayList<>();
            attacks = db.getAllAttackByTime(UnixTime.getUnixTimeDaysAgo(1));
            avs = db.getAllAVByTime(UnixTime.getUnixTimeDaysAgo(1));

            sentDataToRVAttackAdapter.setData(attacks);
            sentDataToRVAVAdapter.setData(avs);

            NavigationView navigationView = findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            onPrepareOptionsMenu(menu);


        }


        if (id == 1) {

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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (wafIntentService != null) {
            startService(wafIntentService);
        } else {
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
        if (wafIntentService != null) {
            stopService(wafIntentService);
        }
    }

    protected void closeApp() {
        //stopService( new Intent(this, WafIntentService.class));
        finish();
        System.exit(0);
    }


    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android  " + release + " (SDK:" + sdkVersion + ")";
    }

    public static void getFCMToken() {
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

        String stringUrl = "http://r.2waf.com:8877/addNewFCM/";
        //final String stringUrl = Resources.getSystem().getString(R.string.link_add_fcm);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {


            // Create data variable for sent values to server

            String data = "&" + URLEncoder.encode("ApiKey", "UTF-8") + "="
                    + URLEncoder.encode(apiKey, "UTF-8");

            data += "&" + URLEncoder.encode("FCMtoken", "UTF-8")
                    + "=" + URLEncoder.encode(FCMtoken, "UTF-8");

            String text = "";
            BufferedReader reader = null;

            // Send data

            // Defined URL  where to send data
            URL url = new URL(stringUrl);

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(500);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String answer = "";
            String line = null;

            while ((line = reader.readLine()) != null) {
                answer += line;
            }
            reader.close();

            return answer.equals("true");

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        //return true;

    }

    public static boolean deleteFCMAccountOnServer(String apiKey) {
        //Log.d(TAG, "sendApiKeyAndFCMtoken: start");
        String stringUrl = "http://r.2waf.com:8877/deleteFCM/";
        //final String stringUrl = Resources.getSystem().getString(R.string.link_delete_fcm);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            // Create data variable for sent values to server

            String data = "&" + URLEncoder.encode("ApiKey", "UTF-8") + "="
                    + URLEncoder.encode(apiKey, "UTF-8");

            data += "&" + URLEncoder.encode("FCMtoken", "UTF-8")
                    + "=" + URLEncoder.encode(FCMtoken, "UTF-8");

            BufferedReader reader = null;

            // Send data

            // Defined URL  where to send data
            URL url = new URL(stringUrl);

            // Send POST data request
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(500);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String answer = "";
            String line = null;

            while ((line = reader.readLine()) != null) {
                answer += line;
            }
            reader.close();

            return answer.equals("true");

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public static void syncApiKeysWithFCM() {
        // final String json = new Gson().toJson(accountList);
        //final String stringUrl = Resources.getSystem().getString(R.string.link_sync_fcm);
        final String stringUrl = "http://r.2waf.com:8877/syncFCM/";

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
                        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            // Create data variable for sent values to server

                            String data = URLEncoder.encode("FCMtoken", "UTF-8")
                                    + "=" + URLEncoder.encode(FCMtoken, "UTF-8");


                            // Defined URL  where to send data
                            URL url = new URL(stringUrl);

                            // Send POST data request

                            URLConnection conn = url.openConnection();
                            conn.setDoOutput(true);
                            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                            wr.write(data);
                            wr.flush();

                            // Get the server response
                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }*/

                    }
                });

    }

    public static void tabBaged(TabLayout.Tab tab, String text, boolean visible) {

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
        if (visible) {
            TextView b = (TextView) tab.getCustomView().findViewById(R.id.badge);
            if (b != null) {
                b.setText(text);
            }
            View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
            if (v != null) {
                v.setVisibility(View.VISIBLE);
            }
        } else {
            View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
            if (v != null) {
                v.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void showLicenseAgreement() {
        //if (prefs.getBoolean("firstrun", true)) {
            final TextView message = new TextView(mContext);
            message.setText(R.string.license_text);
            message.setTextColor(getResources().getColor(R.color.colorWhite));
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            message.setLayoutParams(params);
            message.setMovementMethod(LinkMovementMethod.getInstance());
            FrameLayout container = new FrameLayout(mContext);
            container.addView(message);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
            builder.setTitle(R.string.license_title)
                    //.setMessage(R.string.license_text)
                    .setIcon(R.drawable.ic_notification_icon_v2)
                    .setView(container)
                    .setCancelable(false)
                    .setNegativeButton(R.string.positive_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    showAddSiteDialog();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();
        //}
       prefs.edit().putBoolean("firstRun", false).apply();

    }

    private void attentionAboutProblem(String[] problemOSVersion) {
        if (prefs.getBoolean("firstrun", true)) {
            for (String v : problemOSVersion) {
                if (Build.DISPLAY.contains(v)) {
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

    private void addToWhiteList(String[] whiteListOS) {
        for (String v : whiteListOS) {
            if (Build.DISPLAY.contains(v)) {
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

    private void getAllowInSetting(Activity activity) {
        if (prefs.getBoolean("firstrun", true)) {

            GoToSetting(activity);
            prefs.edit().putBoolean("firstrun", false).apply();

        }
    }

    private void openSite(String url) {
        String browser = CustomTabsHelper.getPackageNameToUse(this);

        if (browser == null) {
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        } else {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(url));
        }

    }


    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model + " (" + android.os.Build.PRODUCT + ")";
        }
    }

    private void showDebugWindow(final Context context) {

        class DebugTask extends AsyncTask<Void, Void, Void> {
            private String message;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                while (FCMtoken == null) {
                }

                String androidVersion = getAndroidVersion();

                String wafCode, testAdd, testDelete;
                String testApiKey = "170ecd95a094a7c03a5a7bc20a173afd17c0e015db18ccbbc05395f271d979f459e1120fcab10e6cc290a1b77a9aae7236e3277dcd0393a40c0aa0398696ed8c";

                if (sendApiKeyAndFCMtoken(testApiKey)) {
                    testAdd = "success";
                } else {
                    testAdd = "error";
                }

                if (deleteFCMAccountOnServer(testApiKey)) {
                    testDelete = "success";
                } else {
                    testDelete = "error";
                }


                try {
                    URL url = new URL(getString(R.string.link_home));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    wafCode = String.valueOf(connection.getResponseCode());

                } catch (Exception e) {
                    wafCode = "error";
                }

                message = "hardware model: " + getDeviceName() + "\n" +
                        "android: " + androidVersion + "\n" +
                        "2waf conn. code: " + wafCode + "\n" +
                        "test request add: " + testAdd + "\n" +
                        "test request delete: " + testDelete + "\n";
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogStyle))
                        .setTitle("debug info")
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
        }

        DebugTask dbTask = new DebugTask();
        dbTask.execute();


    }

    private void registerOnPushServer(String apiKey, String FCMtoken, String name) {

        class regNewSite extends AsyncTask<Void, Void, Response> {
            private String apiKey, token, name;


            public regNewSite(String apiKey, String FCMtoken, String name) {
                super();
                this.apiKey = apiKey;
                this.token = FCMtoken;
                this.name = name;
            }

            @Override
            protected Response doInBackground(Void... voids) {
                try {

                    if (!Web.isInternetAvailable(getResources().getString(R.string.link_home))) {
                        System.out.println("i'm inside");
                        return new Response(404, "err connection time out ");
                    }


                    String stringUrl = getResources().getString(R.string.link_add_fcm);


                    // Create data variable for sent values to server
                    String data = "&" + URLEncoder.encode("ApiKey", "UTF-8") + "="
                            + URLEncoder.encode(apiKey, "UTF-8");

                    data += "&" + URLEncoder.encode("FCMtoken", "UTF-8")
                            + "=" + URLEncoder.encode(token, "UTF-8");

                    return Web.postRequest(stringUrl, data);
                } catch (Exception e) {
                    return new Response(0, e.getMessage());

                }

            }

            @Override
            protected void onPostExecute(Response response) {
                super.onPostExecute(response);

                if (response.StatusCode == 404) {
                    SnackbarHelper.showSnackbar(
                            mainActivityView,
                            getString(R.string.toast_error_ethernet_conn),
                            Snackbar.LENGTH_LONG,
                            R.color.colorWhite,
                            R.color.colorAttention);
                    return;
                }

                DatabaseHandler db = new DatabaseHandler(mContext);
                boolean result = response.StatusCode == 200;
                if (result) {
                    result = db.addAccount(name, apiKey);
                } else {

                    SnackbarHelper.showSnackbar(
                            mainActivityView,
                            getString(R.string.toast_error_server_conn),
                            Snackbar.LENGTH_LONG,
                            R.color.colorWhite,
                            R.color.colorAttention);
                    return;
                }

                if (result) {
                    accountList.add(new Account(name, apiKey));
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    Menu menu = navigationView.getMenu();
                    SubMenu smenu = menu.findItem(R.id.site_list_item).getSubMenu();
                    smenu.add(R.id.site_list, 1, 0, name);

                } else {
                    SnackbarHelper.showSnackbar(
                            mainActivityView,
                            getString(R.string.toast_error_add_account),
                            Snackbar.LENGTH_LONG,
                            R.color.colorWhite,
                            R.color.colorAttention);
                    return;
                }


                SnackbarHelper.showSnackbar(
                        mainActivityView,
                        getString(R.string.toast_site_success_add),
                        Snackbar.LENGTH_SHORT,
                        R.color.colorWhite,
                        R.color.colorAccentDark);
            }
        }

        new regNewSite(apiKey, FCMtoken, name).execute();
    }


    private void deleterFromPushServer(String apiKey, String FCMtoken, String accountName) {

        class deleteSite extends AsyncTask<Void, Void, Response> {
            private String apiKey, token, name;


            public deleteSite(String apiKey, String FCMtoken, String AccountName) {
                super();
                this.apiKey = apiKey;
                this.token = FCMtoken;
                this.name = AccountName;
            }

            @Override
            protected Response doInBackground(Void... voids) {
                try {

                    if (!Web.isInternetAvailable(getResources().getString(R.string.link_home))) {
                        System.out.println("i'm inside");
                        return new Response(404, "err connection time out ");
                    }


                    String stringUrl = getResources().getString(R.string.link_delete_fcm);


                    // Create data variable for sent values to server
                    String data = "&" + URLEncoder.encode("ApiKey", "UTF-8") + "="
                            + URLEncoder.encode(apiKey, "UTF-8");

                    data += "&" + URLEncoder.encode("FCMtoken", "UTF-8")
                            + "=" + URLEncoder.encode(token, "UTF-8");

                    return Web.postRequest(stringUrl, data);
                } catch (Exception e) {
                    return new Response(0, e.getMessage());

                }


            }

            @Override
            protected void onPostExecute(Response response) {
                super.onPostExecute(response);

                if (response.StatusCode == 404) {
                    SnackbarHelper.showSnackbar(
                            mainActivityView,
                            getString(R.string.toast_error_ethernet_conn),
                            Snackbar.LENGTH_LONG,
                            R.color.colorWhite,
                            R.color.colorAttention);
                    return;
                }


                if (response.StatusCode == 200) {
                    db.deleteAccount(name);
                    db.deleteAttack(name);
                    db.deleteAV(name);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else {
                    SnackbarHelper.showSnackbar(
                            mainActivityView,
                            getString(R.string.toast_error_server_conn),
                            Snackbar.LENGTH_LONG,
                            R.color.colorWhite,
                            R.color.colorAttention);
                    return;
                }

                SnackbarHelper.showSnackbar(
                        mainActivityView,
                        getString(R.string.toast_site_success_delete),
                        Snackbar.LENGTH_SHORT,
                        R.color.colorWhite,
                        R.color.colorAccentDark);
            }
        }

        new deleteSite(apiKey, FCMtoken, accountName).execute();
    }


}

