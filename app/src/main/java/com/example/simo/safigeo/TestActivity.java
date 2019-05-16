package com.example.simo.safigeo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.concurrent.CountDownLatch;

public class TestActivity extends AppCompatActivity {

    private volatile boolean imageDownloaded = false;
    private volatile CountDownLatch latch = new CountDownLatch(1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ImageView img = findViewById(R.id.tstimage);
        String path = "https://firebasestorage.googleapis.com/v0/b/safigeo-6f5be.appspot.com/o/Places%2FMohamed%20Karam-captu-1556389749894.jpg?alt=media&token=84e1f0c3-682f-4e98-aec5-2beb7bafc8e6";

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.d("MyTag", "onBitmapLoaded: ");
                        img.setImageBitmap(bitmap);
                        latch.countDown();
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

                Picasso.with(getApplicationContext())
                        .load(path)
                        .into(target);
                img.setTag(target);


            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Image Downloaded", Toast.LENGTH_SHORT).show();




    }
}
