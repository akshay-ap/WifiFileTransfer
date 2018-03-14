package com.examples.akshay.wififiletranserfer.Tasks;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;


import com.examples.akshay.wififiletranserfer.MetaData;
import com.examples.akshay.wififiletranserfer.SocketHolder;
import com.examples.akshay.wififiletranserfer.interfaces.TaskUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ash on 21/2/18.
 * Used to perform sending file in background.
 * Assume Socket is established.
 * Make sure to instantiate only one instance of this class.
 */

public class FileSenderTask extends AsyncTask {

    private static final String TAG = "===FileSenderTask";
    OutputStream outputStream;
    String filePath;
    Socket socket;
    TaskUpdate taskUpdate;
    MetaData metaData;
    public FileSenderTask(String path, TaskUpdate taskUpdate) {

    Log.d(FileSenderTask.TAG,"Object created");
    this.taskUpdate = taskUpdate;

    this.socket = SocketHolder.getSocket();
    this.filePath = path;
        try {
            this.outputStream = socket.getOutputStream();
            Log.d(FileSenderTask.TAG,"obtained outputStream");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(FileSenderTask.TAG,"Failed to obtain outputStream");
            Log.d(FileSenderTask.TAG,e.toString());
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        taskUpdate.TaskStarted();
        if(!SocketHolder.getSocket().isConnected()) {
            Log.d(FileSenderTask.TAG,"Socket is closed... Can't perform file sending Task...");
            return null;
        }

        //Receive metaData
        try {

            Log.d(FileSenderTask.TAG,"obtain file object for : "+ filePath);
            File file = new File(filePath);

            if(!file.exists()) {
                Log.d(FileSenderTask.TAG,"File does not exist");
                return null ;
            } else {
                Log.d(FileSenderTask.TAG,"File exists");
            }

            ObjectOutputStream objectOutputStream;
            objectOutputStream = new ObjectOutputStream(outputStream);

            metaData = new MetaData(file.getParent(),1);
            Log.d(FileSenderTask.TAG,"Trying to send : " + metaData.toString());
            objectOutputStream.writeObject(metaData);


            if(!SocketHolder.getSocket().isConnected()) {
                Log.d(FileSenderTask.TAG,"Socket is closed... Can't perform file sending on stream...");
                return null;
            }

            Log.d(FileSenderTask.TAG,"fileInputStream open: "+ filePath);
            FileInputStream fileInputStream = new FileInputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            int loop = 0;
            long totalRead = 0;
            long toRead = metaData.getDataSize(0);
            while ((bytesRead = fileInputStream.read(buffer)) > 0)
            {
                loop++;
                outputStream.write(buffer, 0, bytesRead);
                totalRead = totalRead + bytesRead;
                if(totalRead % 1024 == 0) {
                    taskUpdate.TaskProgressPublish(metaData.getFname(0) + String.valueOf((float)totalRead/toRead*100) + "%");
                }
            }

            Log.d(FileSenderTask.TAG,"Loop iterations run : " + loop);
            Log.d(FileSenderTask.TAG,"trying to close the fileInputStream...");
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(FileSenderTask.TAG,e.toString());
            taskUpdate.TaskError(e.toString());
        } catch (Exception e) {
            taskUpdate.TaskError(e.toString());
        }

        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        taskUpdate.TaskCompleted(metaData.getFname(0));

        if(SocketHolder.getSocket().isConnected()) {
            Log.d(FileSenderTask.TAG,"BluetoothSocket is connected");

        } else {
            Log.d(FileSenderTask.TAG,"BluetoothSocket is ****NOT*** connected");
        }

        Log.d(FileSenderTask.TAG,"Task execution completed");

    }
}
