package com.example.simo.safigeo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.login.LoginManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                //startActivity(new Intent(getApplicationContext(), TestActivity.class));
                finish();

            }
        }, 3000);   //3 seconds
    }
}
