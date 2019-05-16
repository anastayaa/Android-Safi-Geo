package com.example.simo.safigeo;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class AccountFragment extends Fragment {

    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    ImageView mProfileImg;
    TextView mProfileName;
    TextView mProfileMail;


    public AccountFragment()
    {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View myFragmentView = inflater.inflate(R.layout.fragment_account , container,false);

        mProfileImg =(ImageView) myFragmentView.findViewById(R.id.account_profile_pic);
        mProfileName =(TextView) myFragmentView.findViewById(R.id.account_profile_name);
        mProfileMail =(TextView) myFragmentView.findViewById(R.id.account_profile_email);

        Toolbar toolbar = myFragmentView.findViewById(R.id.account_tool_bar);
        toolbar.setTitle(mUser.getDisplayName());

        mProfileName.setText(mUser.getDisplayName());
        mProfileMail.setText(mUser.getEmail());


        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        String imageUri = mUser.getPhotoUrl().toString()+"?height=500";

        imageUri = imageUri.replace("s96-c", "s500-c");

        Log.d("TAG", "AccountFragment: image url to string : " + imageUri + " image get path : " + mUser.getPhotoUrl().getPath() + " provider " + mUser.getProviderId());

        //ivBasicImage.setImageDrawable();
        if(mProfileImg == null)
        {
            Log.d("TAG", "image null");
        }


        try {
            Picasso.with(getContext()).load(imageUri).into(mProfileImg);

        }catch (Exception ex)
        {
            Log.d("TAG", "ex " + ex.getMessage());

        }

        Log.d("TAG", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");


        return myFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();




    }
}
