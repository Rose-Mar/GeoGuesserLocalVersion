package com.example.geoguesserlocalversion;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;

import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {


    double latitude;
    double longitude;







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);




        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.street_view_panorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);





    }



    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

        Intent intent = this.getIntent();

        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);




        LatLng sanFrancisco = new LatLng(latitude, longitude);
        streetViewPanorama.setPosition(sanFrancisco);
    }





}
