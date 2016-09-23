package com.thomasgarver.rockets;

/**
 * Created by tgarver on 9/16/2016.
 */
public class Planet extends Object {
    public double radius;// In meters
    // @TODO add rotation

    public Planet(String name, double mass, double x, double y, Planet orbiting, double velocity_x, double velocity_y, double radius) {
        super(name, mass, x, y, orbiting, velocity_x, velocity_y);
        this.radius = radius;
    }

    //@TODO add a method to add an object into orbit around this planet. Include things like desired peri/apoapsis and calculate needed values from there
}
