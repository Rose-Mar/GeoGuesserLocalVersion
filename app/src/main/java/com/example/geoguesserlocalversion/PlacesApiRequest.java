//package com.example.geoguesserlocalversion;
//
//import static android.provider.Settings.System.getString;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.widget.Toast;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.Executor;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class PlacesApiRequest {
//
//    private static final String TAG = "PlacesApiRequest";
//
//    private double latitude;
//    private double longitude;
//    private GoogleMap googleMap;
//    private Context context;
//
//
//    public PlacesApiRequest(Context context, GoogleMap googleMap, double latitude, double longitude) {
//        this.context = context;
//        this.googleMap = googleMap;
//        this.latitude = latitude;
//        this.longitude = longitude;
//    }
//
//    ExecutorService executor = Executors.newSingleThreadExecutor();
//    Handler handler = new Handler(Looper.getMainLooper());
//
//
//
//
//
//
//
//    protected void sendIDK() {
//
//
//
//
//
//                for (String place : placesList) {
//                    Log.d(TAG, "Miejsce: " + place);
//                }
//
//
//                String placesText = "Miejsca: " + String.join(", ", placesList);
//                Toast.makeText(context, placesText, Toast.LENGTH_LONG).show();
//
//
//        }
//
//
//    }
//}
