package com.thomasgarver.rockets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by tgarver on 9/22/2016.
 */
public class Rocket extends Object {
    public double angle = 0; // Angle from "up" in absolute coordinate plane
    public double fuelMass;
    public double fuelCapacity;
    public double oxidizerMass; // kg
    public double fuelConsumption; // kg/s
    public double specificImpulse;  // (s) @TODO Add atmospheric and vacuum
    public double height = 68.4;
    public double width = 3.66;
    private double throttle = 1.0;
    private boolean isThrusting = false;
    private ArrayList<Rocket> stages = new ArrayList<Rocket>();
    private double startX;
    private double startY;

    public Rocket(String name, double mass, double x, double y, Planet orbiting, double velocity_x, double velocity_y) {
        super(name, mass, x, y, orbiting, velocity_x, velocity_y);
        this.startX = x;
        this.startY = y;
        // @TODO calculate angle based on coordinates vs planet's coordinates
    }

    public void addStage(Rocket rocket) {
        this.stages.add(rocket);
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
            thrust *= this.throttle;
//            System.out.println("Applying thrust " + deltaT + ", " + thrust);
            double dvx = 0.0, dvy = 0.0;
            dvx = thrust * Math.cos(this.angle) / this.getMass();
            dvy = thrust * Math.sin(this.angle) / this.getMass();
            // @TODO double check signs
            this.velocity_x -= dvx;
            this.velocity_y -= dvy;
            this.fuelMass -= this.fuelConsumption * this.throttle * (deltaT / 1000);
//            System.out.println("Fuel left: " + this.fuelMass);
        } else {
            this.isThrusting = false;
        }
    }

    // time is in milliseconds
    public void doTimeStep(double time) {
        super.doTimeStep(time);
        this.applyThrust(time);
        for (Rocket rocket : this.stages) {
            // @TODO do the time step for each stage. Add to rocket.x and rocket.y, but need to take rocket angle into account.
        }
    }

    public double getMass() {
        double total = this.mass + this.fuelMass;
        for (Rocket rocket : this.stages) {
            total += rocket.getMass();
        }
        return total;
    }

    public double getDownrangeDistance () {
        // @TODO this assumes we start at a particular spot. Subtract iniitial angle from this.
        double dx = Math.abs(this.x - this.orbiting.x);
        double dy = Math.abs(this.y - this.orbiting.y);
        double angle = Math.atan2(dx, dy);
        return angle * this.orbiting.radius;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.rotate((float)(Math.toDegrees(this.angle + Math.PI/2.0)), (float)this.x, (float)this.y);
        canvas.drawRect((float)(this.x - this.width/2), (float) (this.y - this.height/2), (float)(this.x + this.width/2), (float) (this.y + this.height/2), paint);
        canvas.scale(0.001f, 0.001f); //@TODO This doesn't seem to work (trying to draw 1000 pixels per meter)
        canvas.drawRect((float)((this.x - this.width/2)*1000), (float) ((this.y - this.height/2)*1000), (float)((this.x + this.width/2)*1000), (float) ((this.y + this.height/2)*1000), paint);
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
        for (Rocket rocket : this.stages) {
            // Draw each stage
//            canvas.save();
//            canvas.restore();
        }

        canvas.restore();
    }

    public double getThrottle() {
        return this.throttle;
    }

    public void setThrottle(double throttle) {
        this.throttle = throttle;
    }

    public double getPercentageFuelRemaining() {
        return this.fuelMass / this.fuelCapacity;
    }

    public double getAltitude() {
        return Math.sqrt(Math.pow((this.x - this.orbiting.x), 2) + Math.pow((this.y - this.orbiting.y), 2)) - this.orbiting.radius;
    }

    public double getMaximumAltitude() {
        double velocity = this.getVelocity();
        double dx = Math.abs(this.x - this.orbiting.x);
        double dy = Math.abs(this.y - this.orbiting.y);
        double theta1 = Math.atan2(dx, dy);
        double theta2 = Math.atan2(this.velocity_y, this.velocity_x);
        double angle = theta1 + theta2;
        double sinAngle = Math.sin(angle);
        double g = (this.orbiting.mass * Object.G) / (this.distanceTo(this.orbiting) * this.distanceTo(this.orbiting));
//        System.out.println(theta1 + "," + theta2 + "," + angle);
//        System.out.println("V:" + velocity + ", angle:" + Math.sin(angle));
        // @TODO This isn't quite right for long duration flights with angles (it overestimates)
        return this.getAltitude() + ((velocity * velocity) * sinAngle * sinAngle / (2 * g)); // @TODO make g a constant
    }

    public double getVelocity() {
        return Math.sqrt(Math.pow(this.velocity_x, 2) + Math.pow(this.velocity_y, 2));
    }

    public double angleToPlanet() {
        return Math.atan2(this.y - this.orbiting.y, this.x - this.orbiting.x) - Math.PI/2;
    }
}
