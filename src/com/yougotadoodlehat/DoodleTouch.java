package com.yougotadoodlehat;

/**
 * User: m
 * Date: 10/31/14
 * Time: 4:05 AM
 */
public class DoodleTouch {
    float x ;
    float y ;

    public DoodleTouch(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + "," + y + ";";
    }
}
