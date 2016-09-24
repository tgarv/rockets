package com.thomasgarver.rockets;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tgarver on 9/16/2016.
 */
public class Object {
    public String name;
    /**
     * @var double mass of the object in kilograms
     */
    public double mass;
    public double x;
    public double y;
    public double velocity_x;
    public double velocity_y;
    public Planet orbiting;
    public long lastUpdated = 0;
    private static final double G = 6.67408 * Math.pow(10, -11);
    public static int updatesPerSecond = 40;

    public Object(String name, double mass, double x, double y, Planet orbiting, double velocity_x, double velocity_y) {
        this.name = name;
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.orbiting = orbiting;
        this.velocity_x = velocity_x;
        this.velocity_y = velocity_y;
        this.lastUpdated = System.currentTimeMillis(); // in milliseconds

        // @TODO maybe move this into a separate start() method, in case we don't always want to start right away
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Object.this.update();
            }
        }, 0, 1000 / Object.updatesPerSecond);
    }

    public double calculateGravitationalForce() {
        return this.calculateGravitationalForce(this.orbiting);
    }

    public double calculateGravitationalForce(Planet planet) {
        if (planet == null) {
            return 0.0;
        }
        return (this.G * this.mass * planet.mass) / Math.pow(this.distanceTo(planet),2);
    }

    public double distanceTo(Planet planet) {
        return Math.abs(Math.sqrt(Math.pow(this.x - planet.x, 2) + Math.pow(this.y - planet.y, 2)));
    }

    public void update() {
        int warpFactor = 10; // @TODO make this configurable
        long currentTime = System.currentTimeMillis();
        long dt = (currentTime) - this.lastUpdated;
        this.doTimeStep(dt * warpFactor);
        this.lastUpdated = currentTime;
    }

    // time is in milliseconds
    public void doTimeStep(double time) {
        double timeSeconds = time / 1000; // convert time to seconds
        double dx, dy, theta, force_gravity_x, force_gravity_y, dvx, dvy;
        if (this.orbiting != null) {
            double force_gravity = this.calculateGravitationalForce(this.orbiting);
            double x1 = this.x;
            double y1 = this.y;
            double x2 = this.orbiting.x;
            double y2 = this.orbiting.y;
            double angle = Math.atan2((y2 - y1),(x2 - x1)); //Converts the difference to polar coordinates and returns theta.
            force_gravity_x = Math.cos(angle) * force_gravity; //Converts theta to X/Y
            force_gravity_y = Math.sin(angle) * force_gravity; //velocity values.
            dvx = (force_gravity_x / this.mass) * timeSeconds;
            dvy = (force_gravity_y / this.mass) * timeSeconds;

            this.velocity_x += dvx;
            this.velocity_y += dvy;
            this.x += this.velocity_x * timeSeconds;
            this.y += this.velocity_y * timeSeconds;
        }
    }
}
