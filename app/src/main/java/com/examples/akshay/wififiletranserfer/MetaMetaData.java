package com.examples.akshay.wififiletranserfer;

import java.io.Serializable;

/**
 * Created by ash on 22/2/18.
 */

public class MetaMetaData implements Serializable {

    private int numberOfFiles;

    public MetaMetaData(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }
    public int getNumberOfForms() {
        return  numberOfFiles;
    }
    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }


    @Override
    public String toString() {
        return "MetaMetaData{" +
                "numberOfFiles='" + numberOfFiles + '\'' +
                '}';
    }
}
