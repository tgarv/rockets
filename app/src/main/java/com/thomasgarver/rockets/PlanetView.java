package com.thomasgarver.rockets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

public class PlanetView extends View {
    Paint paint = new Paint();
    public Planet planet;
    public Rocket activeRocket;
    public ArrayList<Rocket> rockets = new ArrayList<Rocket>();
    private int activeRocketIndex = 0;
    private ScaleGestureDetector mScaleDetector;

    public PlanetView(Context context) {
        super(context);
        this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public PlanetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public PlanetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        return true;
    }

    private float getScaleAdjustedCenterX(Canvas canvas) {
        return (float)(canvas.getWidth()/GlobalConfig.zoom)/2;
    }

    private float getScaleAdjustedCenterY(Canvas canvas) {
        return (float)(canvas.getHeight()/GlobalConfig.zoom)/2;
    }

    @Override
    public void draw(Canvas canvas) {
        if (this.planet == null) {
            this.planet = new Planet("Earth", 5.972 * Math.pow(10, 24), 0, 0, null, 0, 0, 6371000);
            this.planet.start();
        }
        if (this.activeRocket == null) {
            // start at (1, 0) on the unit circle
            this.activeRocket = new Rocket("Rocket", 33800, this.planet.x + this.planet.radius + 35, this.planet.y, this.planet, this.planet.velocity_x, this.planet.velocity_y);
            this.activeRocket.angle = 2 * Math.PI - this.activeRocket.angleToPlanet();
            this.activeRocket.initialAngle = this.activeRocket.angle;

            this.activeRocket.fuelMass = 395700;
            this.activeRocket.fuelCapacity = 395700;
            this.activeRocket.fuelConsumption = 273 * 9; // This is 9 engines, i.e. Falcon 9
            this.activeRocket.specificImpulse = 281.8 * 9.81; // Multiply by g to get from mass basis to weight basis @TODO double check if this is the right value to use for g
            this.activeRocket.start();
            Rocket secondStage = new Rocket("Second Stage", 3900, this.activeRocket.x, this.activeRocket.y, this.planet, this.activeRocket.velocity_x, this.activeRocket.velocity_y);
            secondStage.height = 13.8;
            secondStage.fuelMass = 92670;
            secondStage.fuelCapacity = 108185;
            secondStage.fuelConsumption = 273;
            secondStage.specificImpulse = 348 * 9.81;
            this.activeRocket.addStage(secondStage);
            this.rockets.add(this.activeRocket);

            Rocket iss = new Rocket("ISS", 100000.0, this.planet.x, this.planet.y - this.planet.radius - 409500.0, this.planet, this.planet.velocity_x + 7670, this.planet.velocity_y);
            iss.start();
            this.rockets.add(iss);
        }
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        canvas.scale((float)GlobalConfig.zoom, (float)GlobalConfig.zoom);
        canvas.translate(this.getScaleAdjustedCenterX (canvas) - (float)this.activeRocket.x, this.getScaleAdjustedCenterY (canvas) - (float)this.activeRocket.y);
        float rotateAngle = (float) Math.toDegrees(this.activeRocket.angleToPlanet() - Math.PI/2); // Subtract PI/2 because we want it going "up" -- because coordinate plane starts with angle 0 to the "right"
        canvas.rotate(rotateAngle, (float) this.activeRocket.x, (float) this.activeRocket.y);

        canvas.drawCircle((float) this.planet.x, (float) this.planet.y, (float) this.planet.radius, paint); // @TODO move the rendering of the Object into the Object class
        paint.setStyle(Paint.Style.FILL);

        for (Rocket rocket : this.rockets) {
            rocket.draw(canvas, paint);
            canvas.drawText(rocket.name, (float)rocket.x, (float)rocket.y, paint);
        }
        super.draw(canvas);
    }

    public double getRocketAltitude() {
        if (this.activeRocket != null)
            return this.activeRocket.getAltitude();
        return 0.0;
    }

    public double getRocketVelocity() {
        if (this.activeRocket != null)
            return this.activeRocket.getVelocity();
        return 0.0;
    }

    public void toggleActiveRocket() {
        if (this.rockets.size() > 0) {
            this.activeRocketIndex += 1;
            this.activeRocketIndex %= this.rockets.size();
            this.activeRocket = this.rockets.get(this.activeRocketIndex);
        }
    }

    /**
     * Listener for pinch/zoom events, used to zoom in and out
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Square the scaleFactor to make zooming in and out easier
            GlobalConfig.zoom *= (detector.getScaleFactor() * detector.getScaleFactor());

            return true;
        }
    }

    public void stage() {
        Rocket newRocket = this.activeRocket.stage();
        if (newRocket != null) {
            this.rockets.add(newRocket);
            this.activeRocketIndex = this.rockets.size() - 1;
            this.activeRocket = newRocket;
        }
    }

}