package com.example.simo.safigeo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.simo.safigeo.Models.ClusterMarker;
import com.example.simo.safigeo.Models.FireBaseConf;
import com.example.simo.safigeo.Models.Place;
import com.example.simo.safigeo.utils.MyClusterManagerRenderer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Context mContext;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;

    private DatabaseReference mPlacesDatabaseReference;

    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<ClusterMarker>();
    private List<ClusterMarker> myItems = new ArrayList<ClusterMarker>();

    public void setContext(Context con) {
        mContext = con;
    }

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mHomeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        mPlacesDatabaseReference = FirebaseDatabase.getInstance().getReference(FireBaseConf.PlacesRefrence);
        
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return mHomeFragmentView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mClusterManager = new ClusterManager<ClusterMarker>(mContext.getApplicationContext(), mMap);

        mClusterManagerRenderer = new MyClusterManagerRenderer(mContext.getApplicationContext(), mMap, mClusterManager);
        mClusterManager.setRenderer(mClusterManagerRenderer);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>() {
            @Override
            public void onClusterItemInfoWindowClick(ClusterMarker clusterMarker) {
                Intent i = new Intent(mContext , PlaceActivity.class);

                i.putExtra("placeId" , clusterMarker.getPlace().getPlaceId());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

        AddPlacesMarkers();




        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

    }

    private void AddPlacesMarkers() {

        if(mMap != null)
        {
            if(mClusterManager == null)
            {
                mClusterManager = new ClusterManager<ClusterMarker>(mContext.getApplicationContext(),mMap);
            }
            if (mClusterManagerRenderer == null)
            {
                mClusterManagerRenderer = new MyClusterManagerRenderer(mContext.getApplicationContext(),mMap,mClusterManager);
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            //loop

            mPlacesDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        Place place = postSnapshot.getValue(Place.class);
                        //mPlaces.add(place);

                        ClusterMarker newClusterMarker = new ClusterMarker(
                                new LatLng(place.getLatitude(), place.getLongitude()),
                                place.getPlaceName(),
                                place.getPlaceDescription(),
                                place.getPlaceImgUrl(),
                                place
                        );

                        mClusterManager.addItem(newClusterMarker);
                        mClusterMarkers.add(newClusterMarker);
                    }

                    mClusterManager.cluster();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(mContext, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

    protected synchronized void buildGoogleApiClient() {
        Log.d("aaaaaazzzzzazdzaaaa", "buildGoogleApiClient: ");
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }
    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("aaaaaazzzzzazdzaaaa", "onLocationChanged: " + latLng.toString());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your location " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        //mMap.animateCamera(CameraUpdateFactory.zoomBy(17));



        Log.d("aaaaaazzzzzazdzaaaa", "onLocationChanged: move camera");

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}
