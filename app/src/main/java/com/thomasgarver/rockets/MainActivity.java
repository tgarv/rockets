package com.thomasgarver.rockets;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    OrbitView orbitView;
    PlanetView planetView;
    private final int FPS = 40;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orbitView = new OrbitView(this);
        orbitView.setBackgroundColor(Color.WHITE);
        setContentView(orbitView);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        orbitView.invalidate();  // Force the view to re-draw
                    }
                });
            }
        }, 0, 1000 / FPS);

//        planetView = new PlanetView(this);
//        planetView.setBackgroundColor(Color.WHITE);
//        setContentView(planetView);
//
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        planetView.invalidate();  // Force the view to re-draw
//                    }
//                });
//            }
//        }, 0, 1000 / FPS);

    }

}
