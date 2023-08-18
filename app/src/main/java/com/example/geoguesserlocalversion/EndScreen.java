package com.example.geoguesserlocalversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class EndScreen extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;

    protected double latitude, longitude;
    private double longitudePoint, latitudePoint;

    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);


        Intent intent = this.getIntent();
        latitudePoint = intent.getDoubleExtra("latitude", 0);
        longitudePoint = intent.getDoubleExtra("longitude", 0);


        if (ContextCompat.checkSelfPermission(
                EndScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(EndScreen.this, " Permission granted", Toast.LENGTH_SHORT).show();


            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(EndScreen.this);

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(EndScreen.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                Toast.makeText(EndScreen.this, "Latitude: " + latitude + " Longitude: " + longitude, Toast.LENGTH_SHORT).show();


                                textView = findViewById(R.id.textView);
                                if (latitude == latitudePoint && longitude == longitudePoint) {


                                    textView.setText("Congratulation!");


                                } else {
                                    textView.setText("Try again!");
                                }


                            }
                        }
                    });

        }
    }
}