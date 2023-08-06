package com.example.geoguesserlocalversion;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;

import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;


import android.os.Bundle;




public class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback
{


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
        LatLng sanFrancisco = new LatLng(37.754130, -122.447129);
        streetViewPanorama.setPosition(sanFrancisco);
    }





}
