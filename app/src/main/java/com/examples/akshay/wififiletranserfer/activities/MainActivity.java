package com.examples.akshay.wififiletranserfer.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.examples.akshay.wififiletranserfer.Constants;
import com.examples.akshay.wififiletranserfer.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonCreateHotSpot;
    Button buttonConnectToHotSpot;
    WifiManager wifiManager;
    private static final String TAG = "===MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        checkPermissions();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_activity_button_create_hotspot:
                Log.d(MainActivity.TAG,"main_activity_button_create_hotspot CLICK");
                createHotSpot();
                break;
            case R.id.main_activity_button_connect_to_hotspot:
                Log.d(MainActivity.TAG,"main_activity_button_connect_to_hotspot CLICK");
                connectToHotSpot();
            default:
                break;
        }
    }

    private void setupUI() {
        buttonCreateHotSpot = findViewById(R.id.main_activity_button_create_hotspot);
        buttonCreateHotSpot.setOnClickListener(this);

        buttonConnectToHotSpot = findViewById(R.id.main_activity_button_connect_to_hotspot);
        buttonConnectToHotSpot.setOnClickListener(this);
    }

    private void createHotSpot() {
        WifiConfiguration netConfig = new WifiConfiguration();

        if(wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }

        netConfig.SSID = Constants.SSID;
        netConfig.preSharedKey = Constants.PASSWORD;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);


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

    private void connectToHotSpot() {

        try {
            if(!wifiManager.isWifiEnabled())
            {
                wifiManager.setWifiEnabled(true);
            }

            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" +Constants.SSID + "\"";  // This string should have double quotes included while adding.
            wifiConfiguration.preSharedKey = Constants.PASSWORD;
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

//Add the created wifi configuration to device
            int netId = wifiManager.addNetwork(wifiConfiguration);  //Adds to the list of network and returns the network id which can be used to enable it later.
            wifiManager.disconnect();
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + Constants.SSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();

                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.d(MainActivity.TAG,e.toString());
            Toast.makeText(getApplicationContext(),"Exception :" + e.toString(),Toast.LENGTH_LONG).show();
        }

    }

    public void checkPermissions() {
        // Here, thisActivity is the current activity

        boolean read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        boolean write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        //boolean settings = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PERMISSION_GRANTED;
        boolean settings = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            settings = Settings.System.canWrite(getApplication());
        } else {
           settings = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PERMISSION_GRANTED;
        }
        ArrayList<String> arrayListPermissions = new ArrayList<>();

        if (!read) {
            arrayListPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.d(MainActivity.TAG, "READ_EXTERNAL_STORAGE permission needed");
        }
        if (!write) {
            arrayListPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.d(MainActivity.TAG, "WRITE_EXTERNAL_STORAGE permission needed");

        }
        if (!settings) {
            arrayListPermissions.add(Manifest.permission.WRITE_SETTINGS);
            Log.d(MainActivity.TAG, "WRITE_SETTINGS permission needed");
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        }

        if (arrayListPermissions.size() != 0) {

            String[] permissions = new String[arrayListPermissions.size()];
            for (int i = 0; i < arrayListPermissions.size(); i++) {
                permissions[i] = arrayListPermissions.get(i);
            }

            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSIONS_REQUEST);
            Log.d(MainActivity.TAG, "Requesting permissions");
            return;
        }

        buttonConnectToHotSpot.setEnabled(true);
        buttonCreateHotSpot.setEnabled(true );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int all = 0;
        if(requestCode == Constants.PERMISSIONS_REQUEST) {
            Log.d(MainActivity.TAG,"Number of permissions : "+ permissions.length);
            for (int res : grantResults) {
                if(res == PERMISSION_GRANTED) all++;
            }

            if(all == grantResults.length) {
                buttonConnectToHotSpot.setEnabled(true);
                buttonCreateHotSpot.setEnabled(true);
                Log.d(MainActivity.TAG,"All permissions granted");
            } else {
                Log.d(MainActivity.TAG,"permissions denied : " + (grantResults.length-all));
                buttonConnectToHotSpot.setEnabled(false);
                buttonCreateHotSpot.setEnabled(false);
            }

        }
    }



}
