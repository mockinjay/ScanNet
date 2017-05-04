package com.mockingjay.scan.scannet;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by casian on 23.01.2017.
 *
 * This class is used to store coordinates of a two-dimension point
 */
public class Point implements Serializable{
    private int dimension;
    private double[] coord;

    public Point(int dimension, double[] coord) {
        this.dimension = dimension;
        this.coord = coord;
    }

    public Point(int dimension) {
        this.dimension = dimension;
        coord = new double[dimension];
    }

    public int getDimension() {
        return dimension;

    }


    public double getElement(int i) {
        return coord[i];
    }

    public void setElement(int i, double value) {
        coord[i] = value;
    }

    public double[] getCoord() {
        return coord;
    }

}
