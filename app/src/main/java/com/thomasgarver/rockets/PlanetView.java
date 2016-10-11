package com.thomasgarver.rockets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class PlanetView extends View {
    Paint paint = new Paint();
    public Planet planet;
    public Rocket activeRocket;
    public ArrayList<Rocket> rockets = new ArrayList<Rocket>();
    private int activeRocketIndex = 0;

    public PlanetView(Context context) {
        super(context);
    }

    public PlanetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlanetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        }
        if (this.activeRocket == null) {
            this.activeRocket = new Rocket("Rocket", 33800, this.planet.x, this.planet.y - this.planet.radius-35, this.planet, this.planet.velocity_x, this.planet.velocity_y);
            this.activeRocket.fuelMass = 400000;
            this.activeRocket.fuelCapacity = 400000;
            this.activeRocket.fuelConsumption = 273 * 9; // This is 9 engines, i.e. Falcon 9
            this.activeRocket.specificImpulse = 280 * 9.81; // Multiply by g to get from mass basis to weight basis @TODO double check if this is the right value to use for g
//            this.activeRocket.angle = 3*Math.PI/4;
            this.activeRocket.angle = Math.PI/2;
//            this.activeRocket.engineThrust = 300;
            this.rockets.add(this.activeRocket);

            Rocket iss = new Rocket("ISS", 100000.0, this.planet.x, this.planet.y - this.planet.radius - 409500.0, this.planet, this.planet.velocity_x + 7670, this.planet.velocity_y);
            this.rockets.add(iss);
        }
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        canvas.scale((float)GlobalConfig.zoom, (float)GlobalConfig.zoom); // @TODO make scale configurable
        canvas.translate(this.getScaleAdjustedCenterX (canvas) - (float)this.activeRocket.x, this.getScaleAdjustedCenterY (canvas) - (float)this.activeRocket.y);

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

}