package com.examples.akshay.wififiletranserfer;

import java.io.Serializable;

/**
 * Created by ash on 22/2/18.
 */

public class MetaData implements Serializable {

    private long dataSize;
    private String fname;
    public MetaData(long dataSize, String fname) {
        this.dataSize = dataSize;
        this.fname = fname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public MetaData() {

    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "dataSize=" + dataSize +
                ", fname='" + fname + '\'' +
                '}';
    }
}
