package com.mockingjay.scan.scannet;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mockingjay on 5/1/17.
 */

public class File {
    private String name;
    private String position;
    private int thumbnail;
    private InputStream input;

    public File(){ }

    public File(String name, int thumbnail, String position) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public InputStream getInput() { return this.input; }

    public void setInput(InputStream input) { this.input = input; }

    public void closeInput() {
        if (this.input != null)
            try {
                input.close();
            } catch (IOException e) {
                System.err.println("Error: Cannot close file input stream!");
                e.printStackTrace();
                System.exit(1);
            }
    }

}


//public class Album {
//    private String name;
//    private int numOfSongs;
//    private int thumbnail;
//
//    public Album() {
//    }
//
//    public Album(String name, int numOfSongs, int thumbnail) {
//        this.name = name;
//        this.numOfSongs = numOfSongs;
//        this.thumbnail = thumbnail;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getNumOfSongs() {
//        return numOfSongs;
//    }
//
//    public void setNumOfSongs(int numOfSongs) {
//        this.numOfSongs = numOfSongs;
//    }
//
//    public int getThumbnail() {
//        return thumbnail;
//    }
//
//    public void setThumbnail(int thumbnail) {
//        this.thumbnail = thumbnail;
//    }
//}