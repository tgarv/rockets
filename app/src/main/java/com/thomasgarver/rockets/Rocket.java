package com.thomasgarver.rockets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by tgarver on 9/22/2016.
 */
public class Rocket extends Object {
    public double angle = 0; // Angle from "positive" in absolute coordinate plane
    public double initialAngle = 0; // Starting angle
    public double fuelMass;
    public double fuelCapacity;
    public double oxidizerMass; // kg
    public double fuelConsumption; // kg/s
    public double specificImpulse;  // (s) @TODO Add atmospheric and vacuum
    public double height = 68.4;    // @TODO should be renamed length
    public double width = 30.66;     // @TODO should be renamed diameter
    private double throttle = 1.0;
    private boolean isThrusting = false;
    private ArrayList<Rocket> stages = new ArrayList<Rocket>();
    public double startX;
    public double startY;

    private double impactTolerance = 15.0; // velocity of impact that this Rocket can tolerate when landing -- units are m/s

    public Rocket(String name, double mass, double x, double y, Planet orbiting, double velocity_x, double velocity_y) {
        super(name, mass, x, y, orbiting, velocity_x, velocity_y);
        this.startX = x;
        this.startY = y;
        // @TODO calculate angle based on coordinates vs planet's coordinates
    }

    /**
     * Add a stage (another Rocket that's currently inactive) to this Rocket
     * @param rocket The Rocket to add
     */
    public void addStage(Rocket rocket) {
        rocket.angle = this.angle;
        rocket.initialAngle = this.initialAngle;
        this.stages.add(rocket);
    }

    /**
     * Get the force generated by this rocket's thrust over the given time period
     * @param deltaT The amount of time that has elapsed in this step (in milliseconds)
     * @return The force (in Newtons)
     */
    public double getThrustForce(double deltaT) {
        double timeInSeconds = deltaT/1000;
        double force = this.specificImpulse * this.fuelConsumption * timeInSeconds * this.throttle;
        return force;
    }

    /**
     * Apply thrust for the given time period to update velocity_x and velocity_y
     * @param deltaT The amount of time that has elapsed (in milliseconds)
     */
    public void applyThrust(double deltaT) {
        if (this.fuelMass > 0) {
            this.isThrusting = true;
            // @TODO handle final step where there's less than a full step of fuel
            double thrust = this.getThrustForce(deltaT);
            double dvx = thrust * Math.cos(this.angle) / this.getMass();
            double dvy = thrust * Math.sin(this.angle) / this.getMass();
            this.velocity_x += dvx;
            this.velocity_y += dvy;
            this.fuelMass -= this.fuelConsumption * this.throttle * (deltaT / 1000);
        } else {
            this.isThrusting = false;
        }
    }

    /**
     * Perform a step in time of the given duration
     * @param time The amount of time that has elapsed since the last time step (in milliseconds)
     */
    public void doTimeStep(double time) {
        this.applyThrust(time);
        if (!this.willCollideNextStep(time)) {
            // If we're not going to collide from our current motion, continue stepping through time
            // @TODO could we step first and then check collision? I think there was a reason we had to look ahead, but can't remember it now.
            super.doTimeStep(time);
        } else {
            if (this.getVelocityRelativeToSurface() > this.impactTolerance) {
                System.out.println("Crashed at velocity " + this.getVelocityRelativeToSurface());
            }
            // Otherwise, we're about to collide. Set our position to the surface of the planet
            this.landAtCurrentAngle();
        }
        for (Rocket rocket : this.stages) {
            // Draw the next stage on top of this stage. This assumes stages are stacked linearly on top of each other.
            rocket.x = this.x + Math.cos(this.angle) * (this.height/2 + rocket.height/2);
            rocket.y = this.y + Math.sin(this.angle) * (this.height/2 + rocket.height/2);
            rocket.velocity_x = this.velocity_x;
            rocket.velocity_y = this.velocity_y;
        }
    }

    /**
     * Get the mass of this Rocket, including all of its stages
     * @return The mass in kilograms
     */
    public double getMass() {
        double total = this.mass + this.fuelMass;
        for (Rocket rocket : this.stages) {
            total += rocket.getMass();
        }
        return total;
    }

    /**
     * Get the "downrange" distance, e.g. the distance over the surface that this Rocket has traveled from where it launched
     * @return The distance in meters
     */
    public double getDownrangeDistance () {
        if (this.orbiting != null) {
            // @TODO this assumes we start at a particular spot. Subtract initial angle from this.
            double dx = Math.abs(this.x - this.orbiting.x);
            double dy = Math.abs(this.y - this.orbiting.y);
            double angle = Math.atan2(dx, dy);
            return angle * this.orbiting.radius;
        }

        return 0.0;
    }

    /**
     * Draw this Rocket on the specified Canvas with the specified Paint
     * @param canvas The Canvas to draw on
     * @param paint The Paint to draw with
     */
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.rotate((float)(Math.toDegrees(this.angle + Math.PI/2.0)), (float)this.x, (float)this.y);
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
        canvas.restore();
        for (Rocket rocket : this.stages) {
            // Draw each stage
//            canvas.save();
//            canvas.restore();
//            canvas.drawCircle((float)rocket.x * 1000, (float)rocket.y * 1000, 500, paint);
            rocket.draw(canvas, paint);
        }

    }

    /**
     * Get the throttle percentage for this Rocket
     * @return The throttle percentage (between 0 and 1)
     */
    public double getThrottle() {
        return this.throttle;
    }

    /**
     * Set the throttle percentage for this Rocket
     * @param throttle The percentage (between 0 and 1)
     */
    public void setThrottle(double throttle) {
        this.throttle = throttle;
    }

    /**
     * Get the percentage of fuel remaining in this Rocket
     * @return The percentage
     */
    public double getPercentageFuelRemaining() {
        return this.fuelMass / this.fuelCapacity;
    }

    /**
     * Get the altitude of this Rocket above the Object it's orbiting. If it's not orbiting an Object, return 0.0
     * @return The altitude in meters
     */
    public double getAltitude() {
        if (this.orbiting != null) {
            return Math.sqrt(Math.pow((this.x - this.orbiting.x), 2) + Math.pow((this.y - this.orbiting.y), 2)) - this.orbiting.radius;
        }

        return 0.0;
    }

    /**
     * Get the maximum altitude (i.e. Apoapsis) that this rocket will reach at its current velocity
     * @return The maximum altitude in meters
     */
    public double getMaximumAltitude() {
        if (this.orbiting != null) {
            double velocity = this.getVelocity();
            double sinAngle = Math.sin(this.getProgradeAngle() + Math.PI / 2);
            double g = (this.orbiting.mass * Object.G) / (this.distanceTo(this.orbiting) * this.distanceTo(this.orbiting));
            // @TODO This isn't quite right for long duration flights with angles (it overestimates)
            return this.getAltitude() + ((velocity * velocity) * sinAngle * sinAngle / (2 * g)); // @TODO make g a constant
        }

        // If we're not orbiting, we don't have an altitude
        return 0.0;
    }

    /**
     * Get the velocity of this Rocket (combining x and y velocities)
     * @return The velocity in meters/second
     */
    public double getVelocity() {
        return Math.sqrt(Math.pow(this.velocity_x, 2) + Math.pow(this.velocity_y, 2));
    }

    /**
     * Get the angle (in the absolute coordinate plane) between this Rocket and the Object that it's orbiting
     * @return
     */
    public double angleToPlanet() {
        if (this.orbiting != null) {
            // Weird bit of math here -- since the coordinate system in canvas has y inverted, we need to invert the y coordinates
            return Math.atan2((-this.y) - (-this.orbiting.y), this.x - this.orbiting.x);
        }

        // If we're not orbiting, there's nothing to compute an angle relative to
        return 0.0;
    }

    /**
     * Gets the angle in which this Rocket is moving, also known as the "prograde" angle.
     * @return The angle
     */
    public double getProgradeAngle() {
        return this.angleToPlanet() + Math.atan2(this.velocity_y, this.velocity_x);
    }

    /**
     * Sets the angle of this Rocket. Also sets the angle of all stages within this rocket.
     * @param angle The angle to set the Rocket to
     */
    public void setAngle(double angle) {
        this.angle = angle;
        for (Rocket rocket : this.stages) {
            rocket.setAngle(angle);
        }
    }

    /**
     * Activates the next stage in this rocket, and return the newly activated Rocket
     * @return The newly activated Rocket, or null if there isn't one
     */
    public Rocket stage() {
        if (this.stages.size() > 0) {
            Rocket newStage = this.stages.remove(0);
            newStage.start();
            return newStage;
        }
        return null;
    }

    /**
     * Checks if the Rocket will collide with the Object it's orbiting on the next step (with its current velocity)
     * @param time The time elapsed for the next step (in milliseconds)
     * @return True if the Rocket will collide, False otherwise
     */
    public boolean willCollideNextStep(double time) {
        if (this.orbiting != null) {
            // @TODO factor in the angle of the Rocket to check if it's actually going to touch the ground
            double timeInSeconds = time/1000;
            double tempX = this.x;
            double tempY = this.y;

            tempX += this.velocity_x * timeInSeconds;
            tempY += this.velocity_y * timeInSeconds;

            double altitude = Math.sqrt(Math.pow((tempX - this.orbiting.x), 2) + Math.pow((tempY - this.orbiting.y), 2)) - this.orbiting.radius;
            return altitude < this.height/2;
        }

        return false;
    }

    /**
     * Sets the rocket down so it's sitting on the edge of the Object that it's orbiting (i.e. "land" it on the ground)
     */
    public void landAtCurrentAngle() {
        // Set the rocket's position to be on the surface
        double angleFromNormal = -(this.angle - this.angleToPlanet());
        double sinAngle = Math.sin(this.angleToPlanet());
        double cosAngle = Math.cos(this.angleToPlanet());
        this.x = cosAngle * (this.orbiting.radius + Math.cos(angleFromNormal) * this.height/2) + Math.abs(Math.sin(angleFromNormal) * this.width/2);
        this.y = sinAngle * (this.orbiting.radius + Math.sin(angleFromNormal) * this.height/2) + Math.abs(Math.cos(angleFromNormal) * this.width/2);
        System.out.println("(" + this.x + "," + this.y + ")");
        System.out.println("angleToPlanet:" + this.angleToPlanet() + "sin:" + sinAngle);
        // Match the velocity of the object it's sitting on. Note that this doesn't take surface rotation (rotation of the Object) into account.
        this.velocity_x = this.orbiting.velocity_x;
        this.velocity_y = this.orbiting.velocity_y;

        double angleDistance = this.distanceBetweenAngles(this.angle, this.angleToPlanet());
        double absoluteAngleDistance = Math.abs(angleDistance);
        if (absoluteAngleDistance > this.getMaxBalanceAngle()) {
            // If the Rocket is leaning too far to one side, it should fall over
            int angleSign = ((absoluteAngleDistance < Math.PI/2) || absoluteAngleDistance > 3*Math.PI/2) ? -1 : 1;
            angleSign *= (angleDistance > 0) ? -1 : 1;
            this.setAngle(this.angle + (angleSign * 0.01));
        } else {
            // @TODO Rotate back the other way to settle back to a landed position
        }
    }

    public double getVelocityRelativeToSurface() {
        if (this.orbiting == null) {
            return 0.0;
        }

        return Math.sqrt(Math.pow(this.velocity_x - this.orbiting.velocity_x, 2) + Math.pow(this.velocity_y - this.orbiting.velocity_y, 2));
    }

    public double getMaxBalanceAngle() {
        return Math.atan2(this.width, this.height);
    }

    public Point getCenterBottomCoordinate() {
        double angle = this.angleToPlanet() + Math.PI/2;
        double x = this.x - (this.height/2) * Math.cos(angle);
        double y = this.y - (this.height/2) * Math.sin(angle);
        Point p = new Point(x, y);
        return p;
    }

    public double distanceBetweenAngles(double alpha, double beta) {
        return Math.atan2(Math.sin(alpha-beta), Math.cos(beta-alpha));
//        double phi = Math.abs(beta - alpha) % (2*Math.PI);       // This is either the distance or 360 - distance
//        double distance = phi > Math.PI ? 2*Math.PI - phi : phi;
//        return distance;
    }

    public double getTotalHeight() {
        // @TODO this assumes that the stages are all stacked linearly.
        double height = this.height;
        for (Rocket stage : this.stages) {
            height += stage.getTotalHeight();
        }

        return height;
    }
}
