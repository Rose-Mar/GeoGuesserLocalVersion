package com.example.geoguesserlocalversion;

//import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.Table.map;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geoguesserlocalversion.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.tasks.Task;

import java.util.Collection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView findPoint;
    private Button findPointBtn;
    private Button locationBtn;
    private Button nextPage;
    protected double latitude, longitude;
    private LocationRequest locationRequest;
    final float NEARBY_CONTACTS_RADIUS  = 1.5f;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION

        };



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

                                        LatLng sydney = new LatLng(latitude, longitude);
                                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                                        Toast.makeText(MapsActivity.this, "Latitude: "+latitude+" Longitude: "+longitude, Toast.LENGTH_SHORT).show();


                                        CircleOptions co = new CircleOptions();

                                        co.center(new LatLng(latitude, longitude));
                                        co.radius(NEARBY_CONTACTS_RADIUS * 1000);
//                                        co.strokeColor(mActivity.getResources().getColor(R.color.map_blue));
                                        co.fillColor(Color.TRANSPARENT);
                                        mMap.addCircle(co);


                                    }
                                }
                            });


                    //TODO
                    // check do location is put on
                }
                else {
                    requestMultiplePermissionLauncher.launch(PERMISSIONS);
                }

            }
        });



        nextPage= findViewById(R.id.nextPage);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, StreetViewActivity.class);
                intent.putExtra("latitude",latitude );
                intent.putExtra("longitude",longitude );
                startActivity(intent);
            }
        });



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
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}