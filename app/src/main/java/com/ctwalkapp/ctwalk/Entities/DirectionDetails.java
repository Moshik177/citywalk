package com.ctwalkapp.ctwalk.Entities;

/**
 * Created by david on 1.12.2015.
 */
public class DirectionDetails
{
    public double distance;
    public int sign;
    public String interval;
    public String text;
    public int time;

    public DirectionDetails(double distance, int sign, String interval, String text, int time) {
        this.distance = distance;
        this.sign = sign;
        this.interval = interval;
        this.text = text;
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
