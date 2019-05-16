package com.example.simo.safigeo;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simo.safigeo.Models.Comment;
import com.example.simo.safigeo.Models.FireBaseConf;
import com.example.simo.safigeo.Models.Place;
import com.example.simo.safigeo.Models.Rate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceActivity extends AppCompatActivity {

    FirebaseUser mCurrentUser;
    String CurrentPlaceId;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mCommentsDatabaseReference;
    private DatabaseReference mRatesDatabaseReference;
    private List<Comment> mComments;
    private List<Rate> mRates;
    private CommentMAdapter mAdapter;

    CircleImageView mUserImage;
    TextView mUserDisplayName;
    TextView mPlaceDate;
    TextView mPlaceName;
    TextView mPlaceDescription;
    TextView mPlaceTags;

    ImageView mPlaceImage;

    RecyclerView mCommentRecyclerView;

    EditText mTextComment;
    private Button mButtonSendComment;
    private TextView rateNowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        CurrentPlaceId = getIntent().getStringExtra("placeId");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mComments = new ArrayList<>();
        mRates = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FireBaseConf.PlacesRefrence);
        mCommentsDatabaseReference = FirebaseDatabase.getInstance().getReference(FireBaseConf.CommentsRefrence);
        mRatesDatabaseReference = FirebaseDatabase.getInstance().getReference(FireBaseConf.RatesRefrence);
        mRatesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRates.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Rate rate = postSnapshot.getValue(Rate.class);
                    mRates.add(rate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mUserImage = findViewById(R.id.place_activity_user_profile_img);
        mUserDisplayName = findViewById(R.id.place_activity_user_name);
        mPlaceDate = findViewById(R.id.place_activity_location_date);

        mPlaceName = findViewById(R.id.place_activity_location_name);
        mPlaceDescription = findViewById(R.id.place_activity_location_description);
        mPlaceTags = findViewById(R.id.place_activity_location_tags);

        mPlaceImage = findViewById(R.id.place_activity_location_img);


        rateNowText = ((TextView)findViewById(R.id.RateNowTewt));

        mCommentRecyclerView = findViewById(R.id.place_activity_recycler_view);
        mCommentRecyclerView.setHasFixedSize(true);
        mCommentRecyclerView.setItemViewCacheSize(20);
        mCommentRecyclerView.setDrawingCacheEnabled(true);
        mCommentRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        mButtonSendComment = findViewById(R.id.button_add_comment);

        mTextComment = findViewById(R.id.place_activity_text_comment);
        mTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(mTextComment.getText().toString().equals(""))
                {
                    mButtonSendComment.setEnabled(false);
                }else mButtonSendComment.setEnabled(true);
            }
        });
        displayPlace();
        Rate prevRate = Rated(CurrentPlaceId , mCurrentUser.getUid());
        /*if(prevRate != null)
        {
            rateNowText.setText("Rate Now ("+prevRate.getRate()+"/5)");
        }else   rateNowText.setText("Rate Now (Not set/5)");
        */


    }

    public void finish(View view) {
        finish();
    }

    public void ratePlace(View view) {

        showDialog(this);

    }

    public void addComment(View view)
    {
        if(mTextComment.getText().toString().equals(""))
        {
            return;
        }

        String commentId = mCommentsDatabaseReference.push().getKey();

        Comment comment = new Comment(commentId,
                mCurrentUser.getUid(),
                mCurrentUser.getDisplayName(),
                mCurrentUser.getPhotoUrl().toString(),
                mTextComment.getText().toString(),
                CurrentPlaceId);

        mCommentsDatabaseReference.child(commentId).setValue(comment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(PlaceActivity.this, "comment upload Succsfulyy", Toast.LENGTH_SHORT).show();
                        mTextComment.setText("");
                        updateCommentsRecyclerView();

                    }
        });


    }



    public void displayPlace()
    {
        mDatabaseReference.child(CurrentPlaceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Place place = dataSnapshot.getValue(Place.class);
                Log.d("aaaqqqaaqaqaqaq", "onDataChange: " + place.toString());
                setPlaceInfos(place);

                getComments(place);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setPlaceInfos(Place place)
    {
        Picasso.with(getApplicationContext()).load(place.getUserProfileImgUrl()).fit().centerCrop().into(mUserImage);

        mUserDisplayName.setText(place.getUserName());
        mPlaceDate.setText(place.getPlaceUploadDate());

        mPlaceName.setText(place.getPlaceName());
        mPlaceDescription.setText(place.getPlaceDescription());

        String[] ListTags = place.getPlaceTags().split("--");
        String tags = "";

        for(String tag : ListTags)
        {
            tags += "#"+tag + " ";
        }
        mPlaceTags.setText(tags);

        Picasso.with(getApplicationContext()).load(place.getPlaceImgUrl()).into(mPlaceImage);

    }

    public void getComments(Place place)
    {
        mComments = new ArrayList<>();

        mCommentsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mComments = new ArrayList<>();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {

                    Comment comment = postSnapshot.getValue(Comment.class);
                    if(comment.getImgId().equals(place.getPlaceId()))
                    {
                        mComments.add(comment);

                        Log.d("aaaqqqaaqaqaqaq", "onDataChange: "  + comment);
                    }

                }

                mAdapter = new CommentMAdapter(getApplicationContext(),mComments);
                mCommentRecyclerView.setAdapter(mAdapter);

                /*if(mComments.isEmpty())
                {
                    Toast.makeText(PlaceActivity.this, "np comments", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(PlaceActivity.this, "there is "+mComments.size() + " comments", Toast.LENGTH_SHORT).show();

                */


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void updateCommentsRecyclerView() {
    }

    public void showDialog(AppCompatActivity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.rating_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        Button btnCancel = dialog.findViewById(R.id.btnCancelRating);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmRating);

        Rate prevRate = Rated(CurrentPlaceId , mCurrentUser.getUid());

        Log.d("RateTag", "showDialog: 1");

        if(prevRate != null)
        {
            Log.d("RateTag", "showDialog: rate kayn");
            ratingBar.setRating(prevRate.getRate());
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(prevRate != null)
                {
                    int newRate = (int)ratingBar.getRating();
                    updateRate(prevRate , newRate);
                }else{
                    Log.d("RateTag", "onClick: 1");
                    String uploadId = mRatesDatabaseReference.push().getKey();
                    Log.d("RateTag", "onClick: 2");

                    Rate rate = new Rate();
                    rate.setRate((int)ratingBar.getRating());
                    rate.setImgId(CurrentPlaceId);
                    rate.setUserId(mCurrentUser.getUid());
                    rate.setRateId(uploadId);
                    Log.d("RateTag", "onClick: 3");
                    mRatesDatabaseReference.child(uploadId).setValue(rate);
                }
                rateNowText.setText("Rate Now ("+(int)ratingBar.getRating()+"/5)");
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    private void updateRate(Rate prevRate, int newRate) {
        prevRate.setRate(newRate);
        mRatesDatabaseReference.child(prevRate.getRateId()).setValue(prevRate);
    }

    private Rate Rated(String currentPlaceId, String uid) {

        for(Rate r : mRates)
        {
            if(r.getImgId().equals(currentPlaceId) && r.getUserId().equals(uid))
            {
                return r;
            }
        }

        return null;
    }


}

