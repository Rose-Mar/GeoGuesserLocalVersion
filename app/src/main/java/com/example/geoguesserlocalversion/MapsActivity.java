package com.example.geoguesserlocalversion;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

//import com.example.geoguesserlocalversion.ui.login.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geoguesserlocalversion.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.slider.Slider;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ImageButton locationBtn, loginBtn;
    private Button nextPage, testBtn;
    private Slider slider;
    private float sliderValue;
    private Circle circle;
    private LatLng curLocation;
    private float radius;
    protected double latitude, longitude;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationBtn = findViewById(R.id.location);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(
                        MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MapsActivity.this, " Permission granted", Toast.LENGTH_SHORT).show();


                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {

                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();

                                        curLocation = new LatLng(latitude, longitude);
                                        mMap.addMarker(new MarkerOptions().position(curLocation).title("Marker in curLocation"));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation));

                                        Toast.makeText(MapsActivity.this, "Latitude: " + latitude + " Longitude: " + longitude, Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });


                    slider = findViewById(R.id.slider);

                    circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(latitude, longitude))
                            .radius(1000)
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE));

                    slider.addOnChangeListener(new Slider.OnChangeListener() {
                        @Override
                        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                            sliderValue = value;


                            circle.remove();

                            circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(latitude, longitude))
                                    .radius(1000 * sliderValue)
                                    .strokeColor(Color.RED));
                        }
                    });

                } else {

                    final String[] PERMISSIONS = {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION

                    };

                    requestMultiplePermissionLauncher.launch(PERMISSIONS);
                }

            }
        });


        nextPage = findViewById(R.id.nextPage);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MapsActivity.this, StreetViewActivity.class);
                intent.putExtra("latitude", curLocation.latitude);
                intent.putExtra("longitude", curLocation.longitude);

                radius = (int) (sliderValue * 1000);

                intent.putExtra("radius", (int) radius);
                startActivity(intent);
            }
        });

        testBtn = findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MapsActivity.this, MapsActivity3.class);
                startActivity(intent);
            }
        });


        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//
//                Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
//                startActivity(intent);
            }
        });





    }
//
//    private boolean isGPSEnable(){
//        LocationManager locationManager = null;
//        boolean isEnable = false;
//
//        if(locationManager == null){
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        }
//
//        isEnable = locationManager.isProviderEnabled((LocationManager.GPS_PROVIDER));
//        return isEnable;
//    }


    private ActivityResultLauncher<String[]> requestMultiplePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.containsValue(false)) {

                    Toast.makeText(this, "Permision Granted", Toast.LENGTH_SHORT).show();

                } else {

                }
            });
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng curLocation = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions().position(curLocation).title("Marker in curLocation"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 10));
    }
}