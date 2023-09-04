package com.example.geoguesserlocalversion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

public class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    double latitude;
    double longitude;
    int radius;
    private Button giveUpBtn;
    private Button answer;

    private StreetViewPanorama streetViewPanorama;
    private boolean isStreetViewLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        Intent intent = this.getIntent();

        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        radius = intent.getIntExtra("radius", 0);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.street_view_panorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        giveUpBtn = findViewById(R.id.giveUpBtn);
        giveUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StreetViewActivity.this, MapsActivity2.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });

        answer = findViewById(R.id.answer);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StreetViewActivity.this, EndScreen.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {
        streetViewPanorama = panorama;
        setupStreetViewListener();
        loadRandomStreetViewLocation();
    }

    private void setupStreetViewListener() {
        if (streetViewPanorama != null) {
            streetViewPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
                @Override
                public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
                    if (location != null && location.links != null && location.links.length > 0) {
                        isStreetViewLoaded = true;
                        onStreetViewLoaded();
                    } else {
                        isStreetViewLoaded = false;
                        onStreetViewNotLoaded();
                    }
                }
            });
        }
    }

    private void loadRandomStreetViewLocation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStreetViewLoaded) {
                    final LatLng randomLocation = getRandomLocation(new LatLng(latitude, longitude), radius);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            streetViewPanorama.setPosition(randomLocation);
                        }
                    });
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void onStreetViewLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(StreetViewActivity.this, "Działa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onStreetViewNotLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(StreetViewActivity.this, "Loading...", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private LatLng getRandomLocation(LatLng point, int radius) {
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        double x0 = point.latitude;
        double y0 = point.longitude;
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLatitude = new_x + x0;
        double foundLongitude = y + y0;
        return new LatLng(foundLatitude, foundLongitude);
    }
}
