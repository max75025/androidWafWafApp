package com.app2waf;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        getSupportActionBar().hide();

        final String url = getIntent().getStringExtra("url");


        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                WebAction(url);
            }
        });

        WebAction(url);


    }

    public void WebAction(String url){
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.loadUrl(url);
        swipe.setRefreshing(true);
        webView.setWebViewClient(new WebViewClient(){

            /*public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.loadUrl("file:///android_assets/error.html");
            }*/

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                swipe.setRefreshing(false);
            }

        });

    }


    @Override
    public void onBackPressed(){

        if (webView.canGoBack()){
            webView.goBack();
        }else {
            finish();
        }
    }
}
