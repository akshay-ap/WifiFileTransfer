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
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();   //Get all declared methods in WifiManager class
        boolean methodFound=false;
        for(Method method: wmMethods){
            if(method.getName().equals("setWifiApEnabled")){
                methodFound=true;
                WifiConfiguration netConfig = new WifiConfiguration();
                connectionDetails.setSsid(Utils.getRandomString());
                connectionDetails.setPassword(Utils.getRandomString());
                netConfig.SSID = connectionDetails.getSsid();
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

                try {
                    boolean apstatus=(Boolean) method.invoke(wifiManager, netConfig,true);
                    for (Method isWifiApEnabledmethod: wmMethods)
                    {
                        if(isWifiApEnabledmethod.getName().equals("isWifiApEnabled")){
                            while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){
                            };
                            for(Method method1: wmMethods){
                                if(method1.getName().equals("getWifiApState")){
                                    int apstate;
                                    apstate=(Integer)method1.invoke(wifiManager);
                                }
                            }
                        }
                    }
                    if(apstatus)
                    {
                       logd("SUCCESSdddd");

                    }else
                    {
                       logd("FAILED");

                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }


       /* ConnectionDetails connectionDetails = ConnectionDetails.getInstance();

        *//*Create new AP and password*//*
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
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
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
        }*/
    }

    public void connectToHotSpot() {
        WifiConfiguration conf = new WifiConfiguration();
        ConnectionDetails connectionDetails = ConnectionDetails.getInstance();
        String ssid = "\"" +connectionDetails.getSsid()+"\"";
        conf.SSID = ssid;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        //WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            //"\""+"TinyBox"+"\""
            if(i.SSID != null && i.SSID.equals(ssid)) {
                try {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    logd("i.networkId " + i.networkId + "\n");
                    wifiManager.reconnect();
                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
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
