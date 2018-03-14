package com.examples.akshay.wififiletranserfer;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ash on 2/3/18.
 */

public class SocketHolder {
    private static int MODE = -1;
    private static Socket socket;
    private static InputStream inputStream;
    private static OutputStream outputStream;



    public static InputStream getInputStream() {
        return inputStream;
    }

    public static void setInputStream() {
        try {
            SocketHolder.inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static OutputStream getOutputStream() {
        return outputStream;
    }

    public static void setOutputStream() {
        try {
            SocketHolder.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getMODE() {
        return MODE;
    }

    public static void setMODE(int MODE) {
        SocketHolder.MODE = MODE;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket bluetoothSocket) {
        SocketHolder.socket = bluetoothSocket;
    }

    public SocketHolder() {
    }
}
