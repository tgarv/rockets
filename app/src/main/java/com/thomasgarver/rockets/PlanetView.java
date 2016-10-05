package com.thomasgarver.rockets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class PlanetView extends View {
    Paint paint = new Paint();
    public Planet planet;
    public Rocket rocket;

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
            this.planet = new Planet("Earth", 5.972 * Math.pow(10, 23), 0, 0, null, 0, 0, 6371000);
        }
        if (this.rocket == null) {
            this.rocket = new Rocket("Rocket", 33800, this.planet.x, this.planet.y - this.planet.radius-35, this.planet, this.planet.velocity_x, this.planet.velocity_y);
            this.rocket.fuelMass = 400000;
            this.rocket.fuelCapacity = 400000;
            this.rocket.fuelConsumption = 273 * 9; // This is 9 engines, i.e. Falcon 9
            this.rocket.specificImpulse = 280;
//            this.rocket.angle = 3*Math.PI/4;
            this.rocket.angle = Math.PI/2;
//            this.rocket.engineThrust = 300;
        }
        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);

        canvas.scale((float)GlobalConfig.zoom, (float)GlobalConfig.zoom); // @TODO make scale configurable
        canvas.translate(this.getScaleAdjustedCenterX (canvas) - (float)this.rocket.x, this.getScaleAdjustedCenterY (canvas) - (float)this.rocket.y);

        canvas.drawCircle((float) this.planet.x, (float) this.planet.y, (float) this.planet.radius, paint); // @TODO move the rendering of the Object into the Object class
//        canvas.drawCircle((float) this.rocket.x, (float) this.rocket.y, 100, paint); // @TODO move the rendering of the Object into the Object class
        paint.setStyle(Paint.Style.FILL);
//        Rect rect = new Rect();
//        rect.set();
        this.rocket.draw(canvas, paint);
//        canvas.drawRect((float)(this.rocket.x - 4.5), (float) (this.rocket.y - 50), (float)(this.rocket.x + 4.5), (float) (this.rocket.y + 50), paint);

//        System.out.println("Altitude: " + distance + "m");
        super.draw(canvas);
    }

    public double getRocketAltitude() {
        if (this.rocket != null)
            return Math.sqrt(Math.pow((this.rocket.x - this.planet.x), 2) + Math.pow((this.rocket.y - this.planet.y), 2)) - this.planet.radius;
        return 0.0;
    }

    public double getRocketVelocity() {
        if (this.rocket != null)
            return Math.sqrt(Math.pow(this.rocket.velocity_x, 2) + Math.pow(this.rocket.velocity_y, 2));
        return 0.0;
    }

}