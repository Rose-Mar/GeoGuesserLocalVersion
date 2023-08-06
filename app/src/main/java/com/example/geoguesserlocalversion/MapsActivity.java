package com.example.geoguesserlocalversion;

//import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.Table.map;

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
import android.icu.text.NumberFormat;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Button locationBtn;
    private Button nextPage;
    private Slider slider;
    private float sliderValue;
    private  Circle circle;
    private LatLng curLocation;
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



                if(ContextCompat.checkSelfPermission(
                        MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)==
                        PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MapsActivity.this, " Permission granted", Toast.LENGTH_SHORT).show();


                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location != null){

                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();

                                        curLocation = new LatLng(latitude, longitude);
                                        mMap.addMarker(new MarkerOptions().position(curLocation).title("Marker in curLocation"));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation));

                                        Toast.makeText(MapsActivity.this, "Latitude: "+latitude+" Longitude: "+longitude, Toast.LENGTH_SHORT).show();




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
                                    .radius(1000*sliderValue)
                                    .strokeColor(Color.RED));
                        }
                    });


                    //TODO
                    // check do location is put on
                }
                else {

                    final String[] PERMISSIONS = {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION

                    };

                    requestMultiplePermissionLauncher.launch(PERMISSIONS);
                }

            }
        });



        nextPage= findViewById(R.id.nextPage);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LatLng randomLocation = getRandomLocation(curLocation,(int)(1000*sliderValue));
                Toast.makeText(MapsActivity.this, "Random Location: " + randomLocation, Toast.LENGTH_SHORT).show();



                Intent intent = new Intent(MapsActivity.this, StreetViewActivity.class);
                intent.putExtra("latitude",randomLocation.latitude );
                intent.putExtra("longitude",randomLocation.longitude );
                startActivity(intent);
            }
        });
    }

    private LatLng getRandomLocation(LatLng point, int radius){
        List<LatLng> randomPoints = new ArrayList<>();
        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate 10 random points
        for(int i = 0; i<10; i++) {
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
            randomPoints.add(randomLatLng);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre

        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
        return randomPoints.get(indexOfNearestPointToCentre);

    }

    private boolean isGPSEnable(){
        LocationManager locationManager = null;
        boolean isEnable = false;

        if(locationManager == null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        }

        isEnable = locationManager.isProviderEnabled((LocationManager.GPS_PROVIDER));
        return isEnable;
    }


    private ActivityResultLauncher<String[]> requestMultiplePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted ->{
                if(isGranted.containsValue(false)){

                    //TODO
                    Toast.makeText(this, "Permision Granted", Toast.LENGTH_SHORT).show();

                }else{

                }
            });

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng curLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(curLocation).title("Marker in curLocation"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation));
    }
}