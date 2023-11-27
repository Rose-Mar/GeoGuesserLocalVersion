package com.example.geoguesserlocalversion;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class PlacesApiRequest extends AsyncTask<Void, Void, String> {

    private static final String TAG = "PlacesApiRequest";

    private double latitude;
    private double longitude;
    private GoogleMap googleMap;
    private Context context;

    public PlacesApiRequest(Context context, GoogleMap googleMap, double latitude, double longitude) {
        this.context = context;
        this.googleMap = googleMap;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected String doInBackground(Void... params) {

        String apiKey = context.getResources().getString(R.string.google_api_key);




        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +latitude+ "," +longitude +
                "&radius=5000&type=restaurant&key=" + apiKey;

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
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
            return null;
        }
    }
    @Override
    protected void onPostExecute(String result) {

                Toast.makeText(context, longitude + "  " + longitude, Toast.LENGTH_LONG).show();
        if (result != null) {

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("results");

                List<String> placesList = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject place = results.getJSONObject(i);
                    String placeName = place.getString("name");
                    double placeLat = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double placeLng = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                    placesList.add(placeName);


                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(placeLat, placeLng))
                            .title(placeName);
                    googleMap.addMarker(markerOptions);

                }


                for (String place : placesList) {
                    Log.d(TAG, "Miejsce: " + place);
                }


                String placesText = "Miejsca: " + String.join(", ", placesList);
                Toast.makeText(context, placesText, Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON", e);
            }
        }
    }
}
