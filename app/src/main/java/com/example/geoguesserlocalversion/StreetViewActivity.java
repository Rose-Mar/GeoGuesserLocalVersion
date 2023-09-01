package com.example.geoguesserlocalversion;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;

import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
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
    int i = 0;
    private Button giveUpBtn;
    private Button answer;

    private static final int STREETVIEW_MAX_DISTANCE = 100;
    private LatLng lng;
    private boolean isWorking;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        Intent intent = this.getIntent();

        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        radius = intent.getIntExtra("radius", 0);

        lng = new LatLng(latitude, longitude);


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
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

        LatLng randomLocation = getRandomLocation(lng, radius);
        streetViewPanorama.setPosition(randomLocation);

        streetViewPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
                if (location != null && location.links != null && location.links.length > 0) {
                    isWorking = true;
                    Toast.makeText(StreetViewActivity.this, "Działa", Toast.LENGTH_SHORT).show();

                } else {
                    isWorking = false;
                    Toast.makeText(StreetViewActivity.this, "nie działa", Toast.LENGTH_SHORT).show();

                }
            }
        });
        i++;
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
        LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);

        return randomLatLng;


    }


}
