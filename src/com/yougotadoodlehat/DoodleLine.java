package com.yougotadoodlehat;

import java.util.ArrayList;

/**
 * User: m
 * Date: 10/31/14
 * Time: 4:05 AM
 */
public class DoodleLine extends ArrayList<DoodleTouch> {

    public void appendToStringBuilder(StringBuilder sb) {
        for (DoodleTouch touch : this) {
            sb.append(touch.toString());
        }
        sb.append("|");
    }
}
