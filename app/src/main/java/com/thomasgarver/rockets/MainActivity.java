package com.thomasgarver.rockets;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    OrbitView orbitView;
    PlanetView planetView;
    private final int FPS = 40;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        orbitView = new OrbitView(this);
//        orbitView.setBackgroundColor(Color.WHITE);
//        setContentView(orbitView);
//
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        orbitView.invalidate();  // Force the view to re-draw
//                    }
//                });
//            }
//        }, 0, 1000 / FPS);

//        planetView = new PlanetView(this);
//        planetView.setBackgroundColor(Color.WHITE);
        setContentView(R.layout.activity_main   );
        final long startTime = System.currentTimeMillis();
        final TextView timerView = (TextView) findViewById(R.id.timer);
        final TextView altitudeView = (TextView) findViewById(R.id.altitude);
        final TextView velocityView = (TextView) findViewById(R.id.velocity);
        final DecimalFormat formatter = new DecimalFormat();
        formatter.setRoundingMode(RoundingMode.DOWN);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        planetView.invalidate();  // Force the view to re-draw
                        long timeDiff = (System.currentTimeMillis() - startTime);
                        long timeDiffSeconds = timeDiff / 1000;
                        // @TODO timer needs to take warp factor into account
                        timerView.setText(String.format("%02d:%02d:%02d", (timeDiffSeconds) / 60, timeDiffSeconds % 60, timeDiff%1000));
                        altitudeView.setText(formatter.format(planetView.getRocketAltitude()) + "m");
                        velocityView.setText(formatter.format(planetView.getRocketVelocity()) + "m/s");
                    }
                });
            }
        }, 0, 1000 / FPS);

        planetView = (PlanetView) findViewById(R.id.planet_view);

        Button button = (Button) findViewById(R.id.zoom_in);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GlobalConfig.zoom *= 1.5;
            }
        });

        button = (Button) findViewById(R.id.zoom_out);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GlobalConfig.zoom /= 1.5;
            }
        });

        button = (Button) findViewById(R.id.increase_warp);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GlobalConfig.warpFactor *= 2;
            }
        });

        button = (Button) findViewById(R.id.decrease_warp);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GlobalConfig.warpFactor = Math.max(1, GlobalConfig.warpFactor/2);
            }
        });

        button = (Button) findViewById(R.id.increase_angle);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                planetView.rocket.angle += Math.PI/90;
            }
        });

        button = (Button) findViewById(R.id.decrease_angle);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                planetView.rocket.angle -= Math.PI/90;
            }
        });

    }

}
