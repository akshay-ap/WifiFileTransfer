package com.examples.akshay.wififiletranserfer;

import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by ash on 2/3/18.
 */

public class Utils {
    private static final String TAG = "===Utils";
    private static void logd(String logMessage) {
        Log.d(Utils.TAG,logMessage);
    }
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    String sAddr = addr.getHostAddress();

                    logd("======== "+sAddr);
                }

                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logd(ex.toString());
        }
        logd("Returning empty IP address");
        return "";
    }

    public static BitMatrix generateQRCodeImage(String text, int width, int height)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return bitMatrix;
    }

    public static String getRandomString() {
        int LENGTH = 10;
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();

        char tempChar;
        for (int i = 0; i < LENGTH; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public static void parseString(String qrString) {

        ConnectionDetails connectionDetails = ConnectionDetails.getInstance();
        try {
            JSONObject jsonObject = new JSONObject(qrString);
            connectionDetails.setIp(jsonObject.getString(Constants.KEY_IP));
            connectionDetails.setPassword(jsonObject.getString(Constants.KEY_PASSWORD));
            connectionDetails.setPort(jsonObject.getInt(Constants.KEY_SERVER_PORT));
            connectionDetails.setSsid(jsonObject.getString(Constants.KEY_SSID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }



}
