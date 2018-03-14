package com.examples.akshay.wififiletranserfer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ash on 22/2/18.
 */

public class MetaData implements Serializable {

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    private String directoryName;

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    private int numberOfFiles;
    private long[] dataSizes;
    private String[] fnames;
    public MetaData(String directoryName,int numberOfFiles) {
        this.directoryName = directoryName;
        this.numberOfFiles = numberOfFiles;
        dataSizes = new long[this.numberOfFiles];
        fnames = new String[this.numberOfFiles];
    }

    public void addFileMetaData(int index,long dataSize,String fname) {
        dataSizes[index] = dataSize;
        fnames[index] = fname;
    }

    public String[] getFnames() {
        return fnames;
    }

    public void setFnames(String[] fname) {
        this.fnames = fname;
    }

    public MetaData() {

    }
    public long[] getDataSizes() {
        return dataSizes;
    }

    public void setDataSize(long[] dataSizes) {
        this.dataSizes = dataSizes;
    }

    public long getDataSize(int index) {
        return dataSizes[index];
    }

    public String getFname(int index) {
        return fnames[index];
    }


    @Override
    public String toString() {
        return "MetaData{" +
                "directoryName=" + directoryName +
                ", numberOfFiles='" + numberOfFiles + '\'' +
                '}';
    }
}
