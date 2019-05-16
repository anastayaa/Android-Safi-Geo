package com.example.simo.safigeo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.example.simo.safigeo.Models.ClusterMarker;
import com.example.simo.safigeo.Models.Place;
import com.example.simo.safigeo.utils.MyClusterManagerRenderer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<ClusterMarker>();
    private List<ClusterMarker> myItems = new ArrayList<ClusterMarker>();

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mClusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), mMap);

        mClusterManagerRenderer = new MyClusterManagerRenderer(getApplicationContext(), mMap, mClusterManager);
        mClusterManager.setRenderer(mClusterManagerRenderer);


        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                ClusterMarker newClusterMarker = new ClusterMarker(
                        latLng,
                        "test",
                        "trsre",
                        "",
                        new Place()
                );

                myItems.add(newClusterMarker);

                mClusterManager.addItems(myItems);
                mClusterManager.cluster();

            }
        });




    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
