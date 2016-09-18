package com.thomasgarver.rockets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import static java.lang.Thread.sleep;

public class DrawView extends View {
    Paint paint = new Paint();
    public Planet planet = new Planet(15000, 15000, 6371, 5.972 * (10^24));
    public Object object1 = new Object("O1", 1, 15000, 25000, planet, 0.28, 0.0);
    public float scaleFactor = 0.05f;

    public DrawView(Context context) {
        super(context);
    }

    @Override
    public void draw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        canvas.scale(this.scaleFactor, this.scaleFactor); // @TODO make scale configurable

        // @TODO move Object update into its own thread within the Object
        this.object1.doTimeStep(1000); //@TODO make time step configurable (warp)
        canvas.drawCircle((float) this.planet.x, (float) this.planet.y, (float) this.planet.radius, paint); // @TODO move the rendering of the Object into the Object class
        canvas.drawCircle((float) this.object1.x, (float) this.object1.y, 400, paint);

        super.draw(canvas);
    }

}