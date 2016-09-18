package com.thomasgarver.rockets;

/**
 * Created by tgarver on 9/16/2016.
 */
public class Planet {
    public double x;
    public double y;
    public double radius;
    public double gravrange; // @TODO remove this
    public double mass;

    public Planet(double x, double y, double radius, double mass) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mass = mass;
    }
}
