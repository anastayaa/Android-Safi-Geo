package com.example.simo.safigeo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mabbas007.tagsedittext.TagsEditText;

public class SearchFragment extends Fragment {
    Context mContext;

    private DatabaseReference mPlacesDatabaseReference;
    List<String> searchTagList;
    List<String> allTagList;
    boolean allTagListFilled = false;

    List<Place> mPlaces;

    RecyclerView mRecyclerView;
    private LocationMAdapter mAdapter;

    TagsEditText inputTags;

    public SearchFragment(){}

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search , container,false);
        searchTagList = new ArrayList<String>();
        allTagList = new ArrayList<String>();
        mPlaces = new ArrayList<Place>();

        mRecyclerView = view.findViewById(R.id.search_recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));


        inputTags = view.findViewById(R.id.searchTagsEditText);

        inputTags.setTagsListener(new TagsEditText.TagsEditListener() {
            @Override
            public void onTagsChanged(Collection<String> collection) {
                searchTagList.clear();
                for(String tag : collection)
                {
                    if(searchTagList.contains(tag))
                    {
                        continue;
                    }else{
                        searchTagList.add(tag);
                    }
                }
                Log.d("TAGListner", "onEditingFinished: " + searchTagList.toString());

                filterPlaces(searchTagList);

            }
            @Override
            public void onEditingFinished() {
                Log.d("TAGListner", "onEditingFinished: ");
            }
        });

        mPlacesDatabaseReference = FirebaseDatabase.getInstance().getReference(FireBaseConf.PlacesRefrence);
        filterPlaces(searchTagList);

        return view;
    }

    private void filterPlaces(List<String> searchTagList) {
        mPlaces.clear();
        mPlacesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Place place = postSnapshot.getValue(Place.class);
                    String tags = place.getPlaceTags();

                    if(!allTagListFilled)
                    {
                        String[] ListTags = tags.split("--");
                        for(String tag : ListTags)
                        {
                            allTagList.add(tag);
                        }
                    }

                    if(searchTagList.isEmpty())
                    {
                        mPlaces.add(place);
                    }else{
                        for (String tag : searchTagList)
                        {
                            if(tags.contains(tag))
                            {
                                mPlaces.add(place);
                                break;
                            }
                        }
                    }



                }

                allTagListFilled=true;

                mAdapter = new LocationMAdapter(mContext,mPlaces);
                mRecyclerView.setAdapter(mAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
