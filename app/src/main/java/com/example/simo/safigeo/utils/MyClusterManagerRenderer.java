package com.example.simo.safigeo.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.simo.safigeo.MapsActivity;
import com.example.simo.safigeo.Models.ClusterMarker;
import com.example.simo.safigeo.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;
    private Context mContext;
    ClusterManager<ClusterMarker> mClusterManager;

    List<Target> mTargetList = new ArrayList<Target>();
    private List<Marker> markerHeper = new ArrayList<>();

    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        this.mClusterManager = clusterManager;
        iconGenerator = new IconGenerator(context.getApplicationContext() );
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) 140;
        markerHeight = 90;
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth,markerHeight));
        int padding  = 5;
        imageView.setPadding(padding,padding,padding,padding);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        Log.d("MyTag", "onBeforeClusterItemRendered: " + item.getPlace().getPlaceName());

        if(item.getIconPicture() == "")
        {
            Bitmap icon = iconGenerator.makeIcon();

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
            return;
        }

        Target target = new Target( ) {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("MyTag", "onBitmapLoaded: need update : "+ item.getPlace().getPlaceName());

                Marker markerToChange = null;
                for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
                    markerHeper.add(marker);

                    if (marker.getPosition().equals(item.getPosition())) {
                        markerToChange = marker;
                        break;
                    }
                }
                /*
                for (Marker marker : markerHeper) {
                    Log.d("MyTag", " getClusterMarkerCollection item: " + marker.getTitle());

                    if (marker.getPosition().equals(item.getPosition())) {
                        markerToChange = marker;
                        break;
                    }
                }*/
                // if found - change icon
                if (markerToChange != null) {
                    Log.d("MyTag", "onBitmapLoaded: marker found :" + item.getPlace().getPlaceName());
                    imageView.setImageBitmap(bitmap);
                    Bitmap icon = iconGenerator.makeIcon();
                    markerToChange.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());

                }else{
                    Log.d("MyTag", "onBitmapLoaded: marker not found :"+ item.getPlace().getPlaceName());
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("MyTag", "onBitmapFailed: ");

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d("MyTag", "onPrepareLoad: ");
            }
        };

        mTargetList.add(target);

        Picasso.with(mContext)
                .load(item.getIconPicture())
                .into(mTargetList.get(mTargetList.size()-1));
        imageView.setTag(mTargetList.get(mTargetList.size()-1));

        //Bitmap icon = iconGenerator.makeIcon();

       // markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());

        Log.d("MyTag", "onBeforeClusterItemRendered: done ;" + item.getPlace().getPlaceName());

    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterMarker> cluster, MarkerOptions markerOptions) {
        Log.d("MyTag", "onBeforeClusterRendered:  cluster ---------" + cluster.toString());



        Collection<ClusterMarker> items = cluster.getItems();
        List<ClusterMarker> clusterItems = (List<ClusterMarker>) cluster.getItems();
        for (ClusterMarker item : clusterItems)
        {
            Log.d("MyTag", "item " + item.toString());
        }

        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return cluster.getSize() > 1;
    }
}
