package com.examples.akshay.wififiletranserfer.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.examples.akshay.wififiletranserfer.Constants;
import com.examples.akshay.wififiletranserfer.R;
import com.examples.akshay.wififiletranserfer.Utils;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class ShowQRCode extends AppCompatActivity {

    private final static String TAG = "===ShowQRCode";
    ImageView imageViewShowQRCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qrcode);
        setupUI();
        try {

            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                makeToast("no data received to show QRCODE");
                return;
            }
            String SSID = extras.getString(Constants.KEY_SSID);
            String PASSWORD = extras.getString(Constants.KEY_PASSWORD);
            String IP = extras.getString(Constants.KEY_IP);
            int PORT = extras.getInt(Constants.KEY_SERVER_PORT,-1);
            logd(SSID + " " + PASSWORD + " " + IP + " " + PORT);

            if (SSID != null && PASSWORD != null && IP != null && PORT != -1 ) {
                makeToast("Valid data received");
                String qrString = bulidString(IP,SSID,PASSWORD,PORT);

                BitMatrix bitMatrix = Utils.generateQRCodeImage(qrString,400,400);
                int height = bitMatrix.getHeight();
                int width = bitMatrix.getWidth();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++){
                    for (int y = 0; y < height; y++){
                        bmp.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                    }
                }
                imageViewShowQRCode.setImageBitmap(bmp);

            } else {
                makeToast("Invalid data received");
            }



        } catch (WriterException e) {
            e.printStackTrace();
            logd(e.toString());
            makeToast(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logd(e.toString());
            makeToast(e.toString());
        }
    }

    private void setupUI() {
        imageViewShowQRCode = findViewById(R.id.activity_show_qrcode_imageView_show_qrcode);
    }

    private void logd(String logMessage) {
        if(logMessage == null) logMessage = "Null";
        Log.d(ShowQRCode.TAG,logMessage);
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(this,toastMessage,Toast.LENGTH_SHORT).show();
    }

    private String bulidString(String ip, String ssid, String password, int port) {
        JSONObject json  = null;

        String result = null;
        try {
            json = new JSONObject();
            json.put(Constants.KEY_IP,ip);
            json.put(Constants.KEY_SSID,ssid);
            json.put(Constants.KEY_PASSWORD,password);
            json.put(Constants.KEY_SERVER_PORT,port);

            result = json.toString();
            makeToast(result);
            logd(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
