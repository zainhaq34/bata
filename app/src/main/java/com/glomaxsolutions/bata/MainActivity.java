package com.glomaxsolutions.bata;




import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private static final long ALERT_DIALOG_TIME_OUT = 5000;
    SwipeRefreshLayout refreshLayout;
    WebView webView;

    private static String BASE_URL = "https://www.bata.com.pk/";
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckInternetConnection();

        refreshLayout = findViewById(R.id.refresh_layout);
        webView = findViewById(R.id.web_view);


        webView.loadUrl(BASE_URL);
        webView.setWebViewClient(new MyWebViewClient());
        //Web Setting
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Swipe Refresh
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(BASE_URL);
            }
        });
    }

    private void CheckInternetConnection() {
        if (broadcastReceiver == null) {

            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(final Context context, Intent intent) {

                    Bundle extras = intent.getExtras();

                    NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
                    NetworkInfo.State state = info.getState();

                    final AlertDialog.Builder alertDialog;

                    if (state == NetworkInfo.State.CONNECTED) {
                        //Toast.makeText(getApplicationContext(), "Internet connection is on", Toast.LENGTH_LONG).show();
                        webView.loadUrl(BASE_URL);

                    } else {
                        //Toast.makeText(getApplicationContext(), "Internet connection is Off", Toast.LENGTH_LONG).show();
                        alertDialog = new AlertDialog.Builder(context);

                        alertDialog.setTitle("Internet not available");
                        alertDialog.setMessage("Please Check your internet connection.");
                        alertDialog.setIcon(R.drawable.ic_warning_icon);
                        alertDialog.setCancelable(false);


                        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                                // startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
                                //finish();
                                //  refreshDialog();

                            }
                        });
                        alertDialog.show();

                    }
                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {

            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning_icon)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to close this App?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
            // Auto Close Dialog
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertDialog.cancel();
                }
            }, ALERT_DIALOG_TIME_OUT);
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            refreshLayout.setRefreshing(false);
            BASE_URL = url;
            super.onPageFinished(view, url);
        }
    }
}
