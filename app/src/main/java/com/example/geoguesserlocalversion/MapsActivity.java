package com.example.geoguesserlocalversion;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ApiRequest apiRequest;
    private ActivityMapsBinding binding;
    private ImageButton loginBtn;
    private Button randomLocationBtn, attractionBtn;
    private Slider slider;
    private float sliderValue;
    private Circle circle;
    private LatLng curLocation;
    private int radius;
    protected double latitude, longitude;
    private String alertText, alertTitle;
    private LatLng attractionLocation;

    LatLng elem;

    String type;
    StringBuilder stringBuilder = new StringBuilder();
    String stringBuilderr;

    private FusedLocationProviderClient fusedLocationProviderClient;
    Handler handler = new Handler(Looper.getMainLooper());






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationFocus();


        slider = findViewById(R.id.slider);
        slider.addOnChangeListener(new Slider.OnChangeListener() {


            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {


                sliderValue = value;
                if (circle!= null){
                circle.remove();}

                circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(latitude, longitude))
                        .radius(1000 * sliderValue)
                        .strokeColor(Color.RED));

                radius = (int) (sliderValue * 1000);

            }
        });



        randomLocationBtn = findViewById(R.id.randomLocationBtn);
        randomLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MapsActivity.this, StreetViewActivity.class);
                intent.putExtra("latitude", curLocation.latitude);
                intent.putExtra("longitude", curLocation.longitude);




                if (radius == 0.0) {

                    alertTitle = "Distance";
                    alertText = "Choose a distance";


                    alertDialog(alertText, alertTitle);

                } else {


                    intent.putExtra("radius", radius);
                    startActivity(intent);
                }
            }
        });

        attractionBtn = findViewById(R.id.attractionBtn);
        attractionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String apiKey = getResources().getString(R.string.google_api_key);

                if (radius == 0.0) {

                    alertTitle = "Distance";
                    alertText = "Choose a distance";

                    alertDialog(alertText, alertTitle);

                } else {


                    ApiRequest apiRequest = new ApiRequest(curLocation.latitude, curLocation.longitude,radius,apiKey);
                    apiRequest.getRandomLocation(new ApiRequestCallback() {
                        @Override
                        public void onApiRequestComplete() {
                            elem= apiRequest.elem;
                            Intent intent = new Intent(MapsActivity.this, StreetViewActivity.class);


                            intent.putExtra("latitude", elem.latitude);
                            intent.putExtra("longitude", elem.longitude);

                            startActivity(intent);
                        }
                    });
                }
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 10));
    }

    public void locationFocus() {

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
//                                mMap.addMarker(new MarkerOptions().position(curLocation).title("Marker in curLocation"));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10.0f));
                                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mMap.setMyLocationEnabled(true);

                                Toast.makeText(MapsActivity.this, "Latitude: " + latitude + " Longitude: " + longitude, Toast.LENGTH_SHORT).show();


                            }
                        }
                    });
        }else {

            final String[] PERMISSIONS = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION

            };

            requestMultiplePermissionLauncher.launch(PERMISSIONS);
        }


    }


    public void alertDialog(String alertText, String alertTitle){

        new AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(alertText)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}