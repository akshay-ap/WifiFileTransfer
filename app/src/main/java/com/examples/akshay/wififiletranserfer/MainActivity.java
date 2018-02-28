package com.examples.akshay.wififiletranserfer;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonCreateHotSpot;
    WifiManager wifiManager;
    private static final String TAG = "===MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_activity_button_create_hotspot:
                Log.d(MainActivity.TAG,"main_activity_button_create_hotspot CLICK");
                createHotSpot();
                break;
            default:
                break;

        }
    }

    private void setupUI() {
        buttonCreateHotSpot = findViewById(R.id.main_activity_button_create_hotspot);
        buttonCreateHotSpot.setOnClickListener(this);
    }

    private void createHotSpot() {
        WifiConfiguration netConfig = new WifiConfiguration();

        netConfig.SSID = "MyAP";
        netConfig.preSharedKey = "12345678";
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        try{
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apstatus=(Boolean) setWifiApMethod.invoke(wifiManager, netConfig,true);

            Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
            while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){};
            Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
            int apstate=(Integer)getWifiApStateMethod.invoke(wifiManager);
            Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            netConfig=(WifiConfiguration)getWifiApConfigurationMethod.invoke(wifiManager);
            Log.d("CLIENT", "\nSSID:"+netConfig.SSID+"\nPassword:"+netConfig.preSharedKey+"\n");

        } catch (Exception e) {
            Log.d(this.getClass().toString(), "", e);
        }
    }


}
