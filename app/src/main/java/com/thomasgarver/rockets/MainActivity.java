package com.thomasgarver.rockets;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        final TextView downrangeDistanceView = (TextView) findViewById(R.id.downrange_distance);
        final TextView fuelPercentageView = (TextView) findViewById(R.id.fuel_percentage);
        final CircularSeekBar seekbar = (CircularSeekBar) findViewById(R.id.angle_bar);
        final SeekBar throttleBar = (SeekBar) findViewById(R.id.throttle_bar);
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
                        if (planetView.rocket != null) {
                            downrangeDistanceView.setText(formatter.format(planetView.rocket.getDownrangeDistance()) + "m");
                            fuelPercentageView.setText("Fuel:" + formatter.format(planetView.rocket.getPercentageFuelRemaining()*100) + "%");
                        }
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

        button = (Button) findViewById(R.id.toggle_angle);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (seekbar.getVisibility() == View.INVISIBLE)
                    seekbar.setVisibility(View.VISIBLE);
                else if (seekbar.getVisibility() == View.VISIBLE)
                    seekbar.setVisibility(View.INVISIBLE); // @TODO set the center of the angle seekbar to the center of the rocket
            }
        });

        // Code for handling the circular seek bar that controls the angle
        class CircleSeekBarListener implements CircularSeekBar.OnCircularSeekBarChangeListener {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                double progressInRadians = (double)(progress) * 3.6 * Math.PI/180.0 + Math.PI/2.0;
                planetView.rocket.angle = progressInRadians;
            }
            @Override
            public void onStartTrackingTouch(CircularSeekBar circularSeekBar) {

            }
            @Override
            public void onStopTrackingTouch(CircularSeekBar circularSeekBar) {

            }
        }

        seekbar.setOnSeekBarChangeListener(new CircleSeekBarListener());

        button = (Button) findViewById(R.id.toggle_throttle);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (throttleBar.getVisibility() == View.INVISIBLE)
                    throttleBar.setVisibility(View.VISIBLE);
                else if (throttleBar.getVisibility() == View.VISIBLE)
                    throttleBar.setVisibility(View.INVISIBLE); // @TODO set the center of the angle seekbar to the center of the rocket
            }
        });

        throttleBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println(progress/100.0);
                planetView.rocket.setThrottle(progress/100.0);
            }
        });

    }

}
