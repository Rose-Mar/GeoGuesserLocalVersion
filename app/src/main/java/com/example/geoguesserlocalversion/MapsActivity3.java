package com.example.geoguesserlocalversion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity3 extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;
    private double desiredLatitude;
    private double desiredLongitude;
    private LatLng curLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(
                MapsActivity3.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity3.this, " Permission granted", Toast.LENGTH_SHORT).show();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity3.this);

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(MapsActivity3.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                desiredLatitude = location.getLatitude();
                                desiredLongitude = location.getLongitude();

                                curLocation = new LatLng(desiredLatitude, desiredLongitude);
                                mGoogleMap.addMarker(new MarkerOptions().position(curLocation).title("Marker in curLocation"));
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation));

                                Toast.makeText(MapsActivity3.this, "Latitude: " + desiredLatitude + " Longitude: " + desiredLongitude, Toast.LENGTH_SHORT).show();


                                PlacesApiRequest placesApiRequest = new PlacesApiRequest(MapsActivity3.this, mGoogleMap, desiredLatitude, desiredLongitude);
                                placesApiRequest.execute();
                            }
                        }
                    });

            LatLng location = new LatLng(desiredLatitude, desiredLongitude);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));



        }
    }
}
