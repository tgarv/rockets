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
    public Planet planet;
    public Object object1;
    public Object object2;
    public float scaleFactor = 0.025f;

    public DrawView(Context context) {
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
        canvas.scale(this.scaleFactor, this.scaleFactor); // @TODO make scale configurable
        // @TODO objects should be placed on an absolute coordinate plane, and the scale factor should just determine the pixels-to-coordinates conversion
        if (this.planet == null)
            this.planet = new Planet(this.getScaleAdjustedCenterX(canvas), this.getScaleAdjustedCenterY(canvas), 6371, 5.972 * (10^24));
        if (this.object1 == null)
            this.object1 = new Object("O1", 1, this.getScaleAdjustedCenterX(canvas), this.getScaleAdjustedCenterY(canvas) + 10000, planet, 0.28, 0.0);
        if (this.object2 == null)
            this.object2 = new Object("O2", 1, this.getScaleAdjustedCenterX(canvas) + 10000, this.getScaleAdjustedCenterY(canvas), planet, 0.0, -0.5);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);

        canvas.drawCircle((float) this.planet.x, (float) this.planet.y, (float) this.planet.radius, paint); // @TODO move the rendering of the Object into the Object class
        canvas.drawCircle((float) this.object1.x, (float) this.object1.y, 400, paint);
        canvas.drawCircle((float) this.object2.x, (float) this.object2.y, 300, paint);

        super.draw(canvas);
    }

}