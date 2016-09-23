package com.thomasgarver.rockets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import static java.lang.Thread.sleep;

public class OrbitView extends View {
    Paint paint = new Paint();
    public Planet planet;
    public Planet planet2;
    public Object object1;
    public Object object2;
    public float scaleFactor = 0.000015f;

    public OrbitView(Context context) {
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
        if (this.planet == null) {
            this.planet = new Planet("Earth", 5.972 * Math.pow(10, 12), 0, 0, null, 0, 0, 6371000);
        }
        if (this.planet2 == null) {
            this.planet2 = new Planet("Moon", 1000000, this.planet.x, this.planet.y + 20000000, this.planet, 0.0050, 0.0, 1000000);
        }
//        if (this.object1 == null) {
//            this.object1 = new Object("O1", 1, this.planet2.x, this.planet2.y + 5000000, this.planet2, 38.0, 0.0);
//        }
//        if (this.object2 == null) {
//            this.object2 = new Object("O2", 1, this.planet.x + 1000000, this.planet.y, planet, 0.0, -0.30);
//        }
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);

        canvas.scale(this.scaleFactor, this.scaleFactor); // @TODO make scale configurable
        canvas.translate(this.getScaleAdjustedCenterX (canvas) - (float)this.planet.x, this.getScaleAdjustedCenterX (canvas) - (float)this.planet.y);

        canvas.drawCircle((float) this.planet.x, (float) this.planet.y, (float) this.planet.radius, paint); // @TODO move the rendering of the Object into the Object class
        canvas.drawCircle((float) this.planet2.x, (float) this.planet2.y, (float) this.planet2.radius, paint); // @TODO move the rendering of the Object into the Object class
//        canvas.drawCircle((float) this.object1.x, (float) this.object1.y, 400, paint);
//        canvas.drawCircle((float) this.object2.x, (float) this.object2.y, 300, paint);

        super.draw(canvas);
    }

}