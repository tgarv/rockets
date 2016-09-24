package com.thomasgarver.rockets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by tgarver on 9/22/2016.
 */
public class Rocket extends Object {
    public double angle = Math.PI/2; // Angle from "up" in absolute coordinate plane
    public double fuelMass;
    public double oxidizerMass; // kg
    public double fuelConsumption; // kg/s
    public double specificImpulse;  // (s) @TODO Add atmospheric and vacuum
    public double height = 68.4;
    public double width = 3.66;
    private boolean isThrusting = false;

    public Rocket(String name, double mass, double x, double y, Planet orbiting, double velocity_x, double velocity_y) {
        super(name, mass, x, y, orbiting, velocity_x, velocity_y);
        // @TODO calculate angle based on coordinates vs planet's coordinates
    }

    // deltaT is in milliseconds
    public double getThrustForce(double deltaT) {
        double timeInSeconds = deltaT/1000;
        double force = 0.0;
        force = this.specificImpulse * this.fuelConsumption * timeInSeconds;
        return force;
        // @TODO double check units
    }

    public void applyThrust(double deltaT) {
        if (this.fuelMass > 0) {
            this.isThrusting = true;
            // @TODO handle final step where there's less than a full step of fuel
            double thrust = this.getThrustForce(deltaT);
//            System.out.println("Applying thrust " + deltaT + ", " + thrust);
            double dvx = 0.0, dvy = 0.0;
            dvx = thrust * Math.cos(this.angle) / this.getMass();
            dvy = thrust * Math.sin(this.angle) / this.getMass();
            // @TODO double check signs
            this.velocity_x -= dvx;
            this.velocity_y -= dvy;
            this.fuelMass -= this.fuelConsumption * (deltaT / 1000);
            System.out.println("Fuel left: " + this.fuelMass);
        } else {
            this.isThrusting = false;
            System.out.println("Out of fuel");
        }
    }

    // time is in milliseconds
    public void doTimeStep(double time) {
        super.doTimeStep(time);
        this.applyThrust(time);
    }

    public double getMass() {
        return this.mass + this.fuelMass;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect((float)(this.x - this.width/2), (float) (this.y - this.height/2), (float)(this.x + this.width/2), (float) (this.y + this.height/2), paint);
        if (this.isThrusting) {
            // Draw the engine bell
//            paint.setColor(Color.RED);
//            Path path = new Path();
//            path.reset();
//            path.moveTo((float)(this.x - this.width/2), (float)(this.y + this.height/2));
//            path.lineTo((float)(this.x - this.width/2) - 1, (float)(this.y + this.height/2 + 2));
//            path.lineTo((float)(this.x + this.width/2) + 1, (float)(this.y + this.height/2 + 2));
//            path.lineTo((float)(this.x + this.width/2), (float)(this.y + this.height/2));
//            path.lineTo((float)(this.x - this.width/2), (float)(this.y + this.height/2));
//            canvas.drawPath(path, paint);
//            canvas.drawCircle((float) this.x, (float) (this.y + this.height / 2), 15.0f, paint);
        }
    }
}
