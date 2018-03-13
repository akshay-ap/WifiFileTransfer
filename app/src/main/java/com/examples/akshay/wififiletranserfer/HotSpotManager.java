package com.examples.akshay.wififiletranserfer;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class HotSpotManager {

    private static final String TAG = "===HotSpotManager";
    WifiManager wifiManager;
    Context context;
    public HotSpotManager(Context context,WifiManager wifiManager) {
        this.context = context;
        this.wifiManager = wifiManager;
    }
    public void createHotSpot() {
        ConnectionDetails connectionDetails = ConnectionDetails.getInstance();

        /*Create new AP an password*/
        connectionDetails.setSsid(Utils.getRandomString());
        connectionDetails.setPassword(Utils.getRandomString());

        logd("Trying to create hotspot will SSID : " + connectionDetails.getSsid() + " Password : " + connectionDetails.getPassword());
        WifiConfiguration netConfig = new WifiConfiguration();

        if(wifiManager.isWifiEnabled())
        {
            logd("wifiManager.isWifiEnabled() == true");

            wifiManager.setWifiEnabled(false);
        }

        if(isHotspotEnabled()) {
            logd("Hotspot is already enabled");
        }
        netConfig.SSID = connectionDetails.getSsid();
        netConfig.preSharedKey = connectionDetails.getPassword();
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

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

        ConnectionDetails connectionDetails = ConnectionDetails.getInstance();
        try {
            if(!wifiManager.isWifiEnabled())
            {
                wifiManager.setWifiEnabled(true);
            }

            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" +connectionDetails.getSsid() + "\"";
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

            int netId = wifiManager.addNetwork(wifiConfiguration);
            wifiManager.disconnect();
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + connectionDetails.getSsid() + "\"")) {
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

    public boolean isHotspotEnabled() {

        Method method = null;
        try {
            method = wifiManager.getClass().getDeclaredMethod("getWifiApState");
            method.setAccessible(true);
            int actualState = (Integer) method.invoke(wifiManager, (Object[]) null);

            if(actualState == WifiManager.WIFI_STATE_ENABLED ) {
                return true;
            }

        } catch (NoSuchMethodException e) {
            logd(e.toString());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logd(e.toString());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            logd(e.toString());
        }

        return false;
    }
    private void logd(String logMessage) {
        Log.d(HotSpotManager.TAG,logMessage);
    }
}
