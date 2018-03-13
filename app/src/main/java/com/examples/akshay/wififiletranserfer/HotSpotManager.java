package com.examples.akshay.wififiletranserfer;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.examples.akshay.wififiletranserfer.activities.MainActivity;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by ash on 13/3/18.
 */

public class HotSpotManager {

    private static final String TAG = "===HotSpotManager";
    WifiManager wifiManager;
    Context context;
    public HotSpotManager(Context context,WifiManager wifiManager) {
        this.context = context;
        this.wifiManager = wifiManager;
    }
    public ConnectionDetails createHotSpot() {
        ConnectionDetails connectionDetails = new ConnectionDetails();
        WifiConfiguration netConfig = new WifiConfiguration();

        if(wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }

        netConfig.SSID = Constants.SSID;
        //netConfig.preSharedKey = Constants.PASSWORD;
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
        return connectionDetails;
    }

    private void connectToHotSpot() {

        try {
            if(!wifiManager.isWifiEnabled())
            {
                wifiManager.setWifiEnabled(true);
            }

            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" +Constants.SSID + "\"";
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

            int netId = wifiManager.addNetwork(wifiConfiguration);
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
            Log.d(HotSpotManager.TAG,e.toString());
            Toast.makeText(context,"Exception :" + e.toString(),Toast.LENGTH_LONG).show();
        }

    }

    public void stopHotspot() {

    }

    public boolean isEnabled() {
        return true;
    }
}
