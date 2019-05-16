package com.example.simo.safigeo;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Toolbar mToolBar;

    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_main);



        /*mToolBar = (Toolbar)findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(R.string.app_name);
        */

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.main_activity_bottom_nav_view);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new HomeFragment()).commit();
        mBottomNavigationView.setSelectedItemId(R.id.ic_home);

    }

    public void showActionBar()
    {
        if(!getSupportActionBar().equals(mToolBar))
        {
            setSupportActionBar(mToolBar);
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().show();
        }
    }
    public void showSearchBar()
    {
        if(!getSupportActionBar().equals(mToolBar))
        {
            setSupportActionBar(mToolBar);
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().show();
        }
    }

    public void hideActionBar()
    {
        getSupportActionBar().hide();
    }

    public void signOut(View v)
    {
        FirebaseAuth.getInstance().signOut();
        try {
            LoginManager.getInstance().logOut();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }catch (Exception ex)
        {
            Log.d("TAG", "signOut:facebook err ");
        }

        try {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, task -> {
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));

                    });
        }catch (Exception ex)
        {
            Log.d("TAG", "signOut:google err ");
        }
        finish();
    }

    public BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFrag = null;

            switch (item.getItemId())
            {
                case R.id.ic_home:
                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.setContext(MainActivity.this);
                    selectedFrag = homeFragment;
                    //showActionBar();
                    break;
                case R.id.ic_account:
                    selectedFrag = new AccountFragment();
                    //hideActionBar();
                    break;
                case R.id.ic_add:
                    AddFragment addFragment = new AddFragment();
                    addFragment.setContext(MainActivity.this);
                    selectedFrag = addFragment;
                    //showActionBar();
                    break;
                case R.id.ic_places:
                    //showActionBar();
                    MyPlacesFragment myPlacesFragment = new MyPlacesFragment();
                    myPlacesFragment.setContext(MainActivity.this);
                    selectedFrag = myPlacesFragment;
                    break;
                case R.id.ic_search:
                    //showActionBar();
                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setContext(MainActivity.this);
                    selectedFrag = searchFragment;
                    break;
            }

            if(selectedFrag != null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , selectedFrag).commit();
            }
            return true;
        }
    };


}
