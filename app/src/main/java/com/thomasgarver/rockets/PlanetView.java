package com.thomasgarver.rockets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PlanetView extends View {
    Paint paint = new Paint();
    public Planet planet;
    public Rocket rocket;
    public float scaleFactor = 0.25f;

    public PlanetView(Context context) {
        super(context);
    }

    private float getScaleAdjustedCenterX(Canvas canvas) {
        return (canvas.getWidth()/this.scaleFactor)/2;
    }

    private float getScaleAdjustedCenterY(Canvas canvas) {
        return (canvas.getHeight()/this.scaleFactor)/2;
    }

    @Override
    public void draw(Canvas canvas) {
        // @TODO move this to an example activity somewhere
        if (this.planet == null) {
            this.planet = new Planet("Earth", 5.972 * Math.pow(10, 24), 0, 0, null, 0.0, 0.0, 6371000);
        }
        if (this.rocket == null) {
            this.rocket = new Rocket("Rocket", 100, this.planet.x, this.planet.y - this.planet.radius - 100, this.planet, this.planet.velocity_x, this.planet.velocity_y);
            this.rocket.fuelMass = 5;
            this.rocket.fuelConsumption = 1;
            this.rocket.specificImpulse = 0;
//            this.rocket.angle = 7*Math.PI/8;
            this.rocket.angle = Math.PI/2;
//            this.rocket.engineThrust = 300;
        }
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);

        canvas.scale(this.scaleFactor, this.scaleFactor); // @TODO make scale configurable
        canvas.translate(this.getScaleAdjustedCenterX (canvas) - (float)this.rocket.x, this.getScaleAdjustedCenterX (canvas) - (float)this.rocket.y);

        canvas.drawCircle((float) this.planet.x, (float) this.planet.y, (float) this.planet.radius, paint); // @TODO move the rendering of the Object into the Object class
        canvas.drawCircle((float) this.rocket.x, (float) this.rocket.y, 100, paint); // @TODO move the rendering of the Object into the Object class

        double distance = Math.sqrt(Math.pow((this.rocket.x - this.planet.x + this.planet.radius), 2) + Math.pow((this.rocket.y - this.planet.y + this.planet.radius), 2));
//        System.out.println("Altitude: " + distance + "m");
        super.draw(canvas);
    }

}