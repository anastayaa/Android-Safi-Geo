package com.example.simo.safigeo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simo.safigeo.Models.FireBaseConf;
import com.example.simo.safigeo.Models.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPlacesFragment extends Fragment {
    Context mContext;

    private RecyclerView mRecyclerView;
    private LocationMAdapter mAdapter;

    private DatabaseReference mdDatabaseReference;
    private List<Place> mPlaces;

    ProgressBar mProgressBar;
    TextView mTextInfo;

    public MyPlacesFragment()
    {

    }


    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_places , container,false);
        mRecyclerView = view.findViewById(R.id.my_places_recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mProgressBar = view.findViewById(R.id.fragment_my_places_progres_bar);
        mTextInfo = view.findViewById(R.id.fragment_my_places_info_text);

        mPlaces = new ArrayList<>();

        mdDatabaseReference = FirebaseDatabase.getInstance().getReference(FireBaseConf.PlacesRefrence);

        mdDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Place place = postSnapshot.getValue(Place.class);
                    if(place.getUserId().matches(FirebaseAuth.getInstance().getUid()))
                    {
                        mPlaces.add(place);
                    }
                }

                mAdapter = new LocationMAdapter(mContext,mPlaces);
                mRecyclerView.setAdapter(mAdapter);

                if(mPlaces.isEmpty())
                {
                    mProgressBar.setVisibility(View.GONE);
                    mTextInfo.setText("You did not add any insterting place");
                }else{
                    mProgressBar.setVisibility(View.GONE);
                    mTextInfo.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
