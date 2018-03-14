package com.examples.akshay.wififiletranserfer.Tasks;

import android.os.AsyncTask;
import android.util.Log;


import com.examples.akshay.wififiletranserfer.MetaData;
import com.examples.akshay.wififiletranserfer.MetaMetaData;
import com.examples.akshay.wififiletranserfer.SocketHolder;
import com.examples.akshay.wififiletranserfer.interfaces.TaskUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ash on 21/2/18.
 * Used to perform sending file in background.
 * Assume Socket is established.
 * Make sure to instantiate only one instance of this class.
 */

public class MultiFileSenderTask extends AsyncTask {

    private static final String TAG = "===MultiFileSenderTask";
    OutputStream outputStream;
    Socket socket;
    TaskUpdate taskUpdate;
    ArrayList<String> arrayListFormPaths;
    MetaMetaData metaMetaData;
    MetaData metaData;
    public MultiFileSenderTask(TaskUpdate taskUpdate, ArrayList<String> arrayListFormPaths) {

    Log.d(MultiFileSenderTask.TAG,"Object created");
    this.taskUpdate = taskUpdate;
    this.arrayListFormPaths = arrayListFormPaths;
    this.metaMetaData = new MetaMetaData(arrayListFormPaths.size());
    this.socket = SocketHolder.getSocket();
        try {
            this.outputStream = socket.getOutputStream();
            Log.d(MultiFileSenderTask.TAG,"obtained outputStream");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(MultiFileSenderTask.TAG,"Failed to obtain outputStream");
            Log.d(MultiFileSenderTask.TAG,e.toString());
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        taskUpdate.TaskStarted();
        if(!SocketHolder.getSocket().isConnected()) {
            Log.d(MultiFileSenderTask.TAG,"Socket is closed... Can't perform file sending Task...");
            return null;
        }

        //Receive metaData
        try {

            ObjectOutputStream objectOutputStream;
            objectOutputStream = new ObjectOutputStream(outputStream);

            Log.d(MultiFileSenderTask.TAG,"Trying to send : " + metaMetaData.toString());
            objectOutputStream.writeObject(metaMetaData);

            Log.d(MultiFileSenderTask.TAG,"Number of files to send :" + metaMetaData.getNumberOfFiles());

            for(int i =0 ;i < metaMetaData.getNumberOfFiles();i++) {
                String xmlFilePath = arrayListFormPaths.get(i);
                Log.d(MultiFileSenderTask.TAG,"Trying to send file number :" + i);

                Log.d(MultiFileSenderTask.TAG,"obtain file object for : "+ xmlFilePath);
                File xmlFile = new File(xmlFilePath);

                if(!xmlFile.exists()) {
                    Log.d(MultiFileSenderTask.TAG,"File does not exist");
                    taskUpdate.TaskError("File does not exist : " + xmlFilePath);

                    return null ;
                } else {
                    Log.d(MultiFileSenderTask.TAG,"File exists");
                }

                File parentDirectory = new File(xmlFile.getParent());
                Log.d(MultiFileSenderTask.TAG,"Parent directory path : " + parentDirectory);

                File[] files = parentDirectory.listFiles();

                metaData = new MetaData(parentDirectory.getName(),files.length);

                Log.d(MultiFileSenderTask.TAG,"No of files with the form " + "Size: "+ files.length);
                for (int j = 0; j < files.length; j++)
                {
                    metaData.addFileMetaData(j,files[j].length(),files[j].getName());
                    Log.d("Files", "FileName:" + files[j].getName());
                }




                Log.d(MultiFileSenderTask.TAG,"Trying to send : " + metaData.toString());
                objectOutputStream.writeObject(metaData);

                //Turn this in loop
                for (int formFileNumber =0; formFileNumber < metaData.getNumberOfFiles(); formFileNumber++ ) {
                    if(!SocketHolder.getSocket().isConnected()) {
                        Log.d(MultiFileSenderTask.TAG,"Socket is closed... Can't perform file sending on stream...");
                        return null;
                    }
                    String filePath = parentDirectory +"/"+ metaData.getFname(formFileNumber);
                    Log.d(MultiFileSenderTask.TAG,"fileInputStream open: "+ filePath);
                    File file = new File(filePath);
                    FileInputStream fileInputStream = new FileInputStream(file);

                    int bytesRead = 0;
                    byte[] buffer = new byte[1024];
                    int loop = 0;
                    long totalRead = 0;
                    long toRead = metaData.getDataSize(formFileNumber);
                    while ((bytesRead = fileInputStream.read(buffer)) > 0)
                    {
                        loop++;
                        outputStream.write(buffer, 0, bytesRead);
                        totalRead = totalRead + bytesRead;
                        if(totalRead % 1024 == 0) {
                            taskUpdate.TaskProgressPublish(metaData.getFname(formFileNumber) + String.valueOf((float)totalRead/toRead*100) + "%");
                        }
                    }
                    Log.d(MultiFileSenderTask.TAG,"Loop iterations run : " + loop);
                    Log.d(MultiFileSenderTask.TAG,"trying to close the fileInputStream...");
                    fileInputStream.close();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
            taskUpdate.TaskError(e.toString());
            Log.d(MultiFileSenderTask.TAG,e.toString());
        } catch (Exception e) {
            taskUpdate.TaskError(e.toString());
        }

        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        taskUpdate.TaskCompleted(this.getClass().getSimpleName()+": Completed");

        if(SocketHolder.getSocket().isConnected()) {
            Log.d(MultiFileSenderTask.TAG,"Socket is connected");

        } else {
            Log.d(MultiFileSenderTask.TAG,"Socket is ****NOT*** connected");
        }

        Log.d(MultiFileSenderTask.TAG,"Task execution completed");

    }
}
