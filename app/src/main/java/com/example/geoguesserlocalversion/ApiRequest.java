package com.example.geoguesserlocalversion;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;


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


public class ApiRequest {

    double latitude, longitude;
    float radius;
    String apiKey, type;
    LatLng curLocation;
    private final Handler handler = new Handler(Looper.getMainLooper());
    public LatLng elem;


    public ApiRequest(double latitude, double longitude, float radius, String apiKey) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.apiKey = apiKey;


    }






    public void getRandomLocation(ApiRequestCallback callback) {

        LatLng placesLatLng;

        curLocation = new LatLng(latitude, longitude);

        placesLatLng = getRandomLocation(curLocation, (int) radius);

        apiRequest(placesLatLng, callback);
    }


    protected void apiRequest(LatLng placesLatLng, ApiRequestCallback callback) {

        curLocation = new LatLng(latitude, longitude);


        List<String> attractionList = new ArrayList();
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


        for (int i = 0; i < attractionList.size(); i++) {

            if (type == null) {
                type = attractionList.get(i);
            } else {
                type += "," + attractionList.get(i);
            }
        }


        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                placesLatLng.latitude + "," + placesLatLng.longitude +
                "&rankby=distance&type=" + type + "&key=" + apiKey;
        String test = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=50.01136504029173,21.578240465210776&rankby=distance&type=church&key=AIzaSyDgz7IoHLdTme0Hoh7q284D5UuuLvpk-FQ";

        Log.d("ApiRequest", "Przed uruchomieniem wątku");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("ApiRequest", "Wątek rozpoczął się");
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                handleApiResponse(stringBuilder.toString());
                                callback.onApiRequestComplete();
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
                }

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



