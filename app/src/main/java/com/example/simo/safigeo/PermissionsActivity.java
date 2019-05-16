package com.example.simo.safigeo;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class PermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);


        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {openMultipleThings();}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {}
        }).check();

    }

    private void openMultipleThings(){
        Toast.makeText(this, "your are logedd in : " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        //startActivity(new Intent(getApplicationContext(),PermissionsActivity.class));
        finish();

    }
}
