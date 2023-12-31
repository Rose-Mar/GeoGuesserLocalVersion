package com.example.geoguesserlocalversion;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MapsActivity3 extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;
    private double desiredLatitude;
    private double desiredLongitude;
    private LatLng curLocation;
    private double placesLat, placesLng;
    private LatLng placesLatLng;
    private int radius;
    LatLng randElem;
    LatLng elem;

    String type;

    private Button nextPage;

    StringBuilder stringBuilder = new StringBuilder();
    String stringBuilderr;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


        Intent intent = this.getIntent();
        desiredLatitude = intent.getDoubleExtra("latitude", 0);
        desiredLongitude = intent.getDoubleExtra("longitude", 0);
        curLocation = new LatLng(desiredLatitude,desiredLongitude);
        radius = intent.getIntExtra("radius",0);

        placesLatLng = getRandomLocation(curLocation,radius);




    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(
                MapsActivity3.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity3.this, "Permission granted", Toast.LENGTH_SHORT).show();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity3.this);

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(MapsActivity3.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
//                                desiredLatitude = location.getLatitude();
//                                desiredLongitude = location.getLongitude();
//
//                                curLocation = new LatLng(desiredLatitude, desiredLongitude);
                                mGoogleMap.addMarker(new MarkerOptions().position(curLocation).title("Marker in curLocation"));
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation));

                                Toast.makeText(MapsActivity3.this, "Latitude: " + desiredLatitude + " Longitude: " + desiredLongitude, Toast.LENGTH_SHORT).show();



                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        apiRequest();
                                    }
                                });
                            }
                        }
                    });

//            LatLng location = new LatLng(desiredLatitude, desiredLongitude);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 15));



        }


        nextPage = findViewById(R.id.nextPage);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MapsActivity3.this, StreetViewActivity.class);
                intent.putExtra("latitude", elem.latitude);
                intent.putExtra("longitude", elem.longitude);
                startActivity(intent);
            }
        });
    }

    protected void apiRequest() {


        List <String> attractionList  = new ArrayList();
//        attractionList.add("amusement_park");
//        attractionList.add("hindu_temple");
//        attractionList.add("mosque");
//        attractionList.add("park");
        attractionList.add("church");
//        attractionList.add("museum");

//        attractionList.add("rv_park");
//        attractionList.add("synagogue");
//        attractionList.add("tourist_attraction");
//        attractionList.add("zoo");



        for (int i = 0; i<attractionList.size(); i++){

            if (type == null){
                type = attractionList.get(i);
            }else {
                type += "," + attractionList.get(i);
            }
        }

        String apiKey = getResources().getString(R.string.google_api_key);

//        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + desiredLatitude + "," + desiredLongitude +
//                "&radius=5000&type=bar|restaurant&key=" + apiKey;
// najbliższe lokalizacji zrobic jutro
        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + placesLatLng.latitude + "," + placesLatLng.longitude +
                "&rankby=distance&type="+ type +"&key=" + apiKey;


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        stringBuilderr = stringBuilder.toString();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                handleApiResponse(stringBuilderr);
                            }
                        });
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error", e);
                }
            }
        }).start();
    }

    private void handleApiResponse(String response) {
        if (response != null && !response.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");

                List<String> placesList = new ArrayList<>();
                List<LatLng> placesLatLngList = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject place = results.getJSONObject(i);
                    String placeName = place.getString("name");
                    double placeLat = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double placeLng = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    LatLng placeLocation = new LatLng(placeLat, placeLng);
                    placesList.add(placeName);
                    placesLatLngList.add(placeLocation);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(placeLat, placeLng))
                            .title(placeName);
                    mGoogleMap.addMarker(markerOptions);
                }

//                Random rand = new Random();
//
//                randElem = placesLatLngList.get(rand.nextInt(placesLatLngList.size()));

                elem = placesLatLngList.get(0);

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON", e);
            }
        }
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
