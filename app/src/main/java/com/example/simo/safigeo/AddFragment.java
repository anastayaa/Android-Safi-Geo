package com.example.simo.safigeo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.simo.safigeo.Models.FireBaseConf;
import com.example.simo.safigeo.Models.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mabbas007.tagsedittext.TagsEditText;

public class AddFragment extends Fragment {


    Uri mImgUri;
    ImageView mLocationIamge;
    BottomNavigationView mbottomNavigationView;

    LinearLayout mlinearLayout;

    EditText mTxtLocationName;
    EditText mTxtLocationDescription;
    TagsEditText mTxtLocationTags;

    ProgressBar mUploadingProgressBar;
    ProgressBar RoundedPB;

    Context mContext;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    List<String> mToAddTagList;

    public AddFragment() {

        mStorageReference = FirebaseStorage.getInstance().getReference(FireBaseConf.PlacesRefrence);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FireBaseConf.PlacesRefrence);


    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add , container,false);

        mToAddTagList = new ArrayList<String >();
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setAspectRatio(2,1)
                .start(getContext(), this);

        mLocationIamge = (ImageView) view.findViewById(R.id.imageToAdd);
        mUploadingProgressBar = view.findViewById(R.id.uploadingProgressBar);
        RoundedPB =  view.findViewById(R.id.add_fragment_progres_bar);


        mlinearLayout = view.findViewById(R.id.layoutForAddingImage);
        mlinearLayout.setVisibility(View.INVISIBLE);

        mbottomNavigationView = ((AppCompatActivity)getActivity()).findViewById(R.id.main_activity_bottom_nav_view);

        mTxtLocationName = view.findViewById(R.id.imgName);
        mTxtLocationDescription = view.findViewById(R.id.imgDescription);
        mTxtLocationTags = view.findViewById(R.id.tagsEditText);
        mTxtLocationTags.setTagsListener(new TagsEditText.TagsEditListener() {
            @Override
            public void onTagsChanged(Collection<String> collection) {
                mToAddTagList.clear();
                for(String tag : collection)
                {
                    if(mToAddTagList.contains(tag))
                    {
                        continue;
                    }else{
                        mToAddTagList.add(tag);
                    }
                }

            }
            @Override
            public void onEditingFinished() {
                Log.d("TAGListner", "onEditingFinished: ");
            }
        });
        ((Button)(view.findViewById(R.id.btnAdd))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavePlace();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == MainActivity.RESULT_OK) {
                Uri resultUri = result.getUri();

                Log.d("MyTAG", "onActivityResult: path  "+result.getUri().getPath());
                mLocationIamge.setImageDrawable(null);
                mImgUri = resultUri;
                mLocationIamge.setImageURI(mImgUri);
                mlinearLayout.setVisibility(View.VISIBLE);
                RoundedPB.setVisibility(View.INVISIBLE);


            } else{
                 mbottomNavigationView.setSelectedItemId(R.id.ic_home);
            }

        }else {
            mbottomNavigationView.setSelectedItemId(R.id.ic_home);
        }


    }




    public void SavePlace()
    {
        mlinearLayout.setVisibility(View.INVISIBLE);
        RoundedPB.setVisibility(View.VISIBLE);

        String imgName = mTxtLocationName.getText().toString();
        String imgDescription = mTxtLocationDescription.getText().toString();
        //List<String> tagList = mTxtLocationTags.getTags();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String tags = TextUtils.join("--", mToAddTagList);

        Log.d("AQWAAQQQWQA", "Plaace tags: " + tags);


        if(imgName.equals("") ||  imgDescription.equals("") || mToAddTagList.size() <= 0)
        {
            Log.d("AQWAAQQQWQA", "Please fill all the infos");
            Toast.makeText(mContext, "Please fill all the infos", Toast.LENGTH_LONG).show();
            mlinearLayout.setVisibility(View.VISIBLE);
            RoundedPB.setVisibility(View.INVISIBLE);
            return;
        }


        Place place = new Place();


        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Please activate the GPS and try again", Toast.LENGTH_LONG).show();

            Log.d("AndroidClarified","not ranted");
            mlinearLayout.setVisibility(View.VISIBLE);
            RoundedPB.setVisibility(View.INVISIBLE);
            return ;
        }

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        Task task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("AQWAAQQQWQA","getted " + location.getLatitude()+" "+location.getLongitude());
                place.setLatitude(location.getLatitude());
                place.setLongitude(location.getLongitude());
                Log.d("AQWAAQQQWQA","setted " + place.getLatitude()+" "+place.getLongitude());
                place.setUser(FirebaseAuth.getInstance().getCurrentUser());
                place.setPlaceName(imgName);
                place.setPlaceDescription(imgDescription);
                place.setPlaceTags(tags);

                try {
                    Log.d("AQWAAQQQWQA", "place ogject to send in function uploadPlace : " + place);

                    uploadPlace(place);

                }catch (Exception ex)
                {
                    Log.d("AQWAAQQQWQA", "exception: set1" + ex.getMessage());

                }

            }
        });


    }

    private String getFileExt(Uri uri)
    {

        ContentResolver cR = ((AppCompatActivity)getActivity()).getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ex = mime.getExtensionFromMimeType(cR.getType(uri));

        String fileExt = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        Log.d("AQWAAQQQWQA", "SavePlace: file ex" + fileExt);
        return fileExt;
    }

    public void uploadPlace(Place place)
    {
        Log.d("AQWAAQQQWQA", "SavePlace: start upload methode");

        mUploadingProgressBar.setVisibility(View.VISIBLE);
        Log.d("AQWAAQQQWQA", "SavePlace: visible");

        if(mImgUri != null)
        {
            String imageNameInFireBase = FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+"-"+mTxtLocationName.getText().toString()+
                    "-"+System.currentTimeMillis() + "." + getFileExt(mImgUri);
            StorageReference fileReference = mStorageReference.child(imageNameInFireBase);

            UploadTask uploadTask = fileReference.putFile(mImgUri);
            Log.d("AQWAAQQQWQA", "SavePlace: put file");

            Task<Uri> urlTask =
                    uploadTask
                            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        Log.d("AQWAAQQQWQA", "SavePlace: put file err");
                                        throw task.getException();

                                    }

                                    // Continue with the task to get the download URL
                                    return fileReference.getDownloadUrl();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {

                                        Log.d("AQWAAQQQWQA", "SavePlace: put file complete");

                                        Uri downloadUri = task.getResult();

                                        //Toast.makeText(mContext, "ImageUploadSuccesful name : "+imageNameInFireBase, Toast.LENGTH_SHORT).show();
                                        String uploadId = mDatabaseReference.push().getKey();
                                        place.setPlaceId(uploadId);
                                        place.setPlaceImgUrl(downloadUri.toString());
                                        //place.setPlaceImgUrl(taskSnapshot.getTask().);

                                        Log.d("AQWAAQQQWQA", "SavePlace: seetting place in daba");

                                        Log.d("AQWAAQQQWQA", "place object after uploading image : " + place);

                                        try {
                                            mDatabaseReference.child(uploadId).setValue(place);

                                        }catch (Exception ex)
                                        {
                                            Log.d("AQWAAQQQWQA", "err adding to database : " + ex.getMessage());

                                        }
                                        Log.d("AQWAAQQQWQA", "SavePlace: seetting place and uploading");
                                        RoundedPB.setVisibility(View.INVISIBLE);
                                        Toast.makeText(mContext, "Place added succesfoly", Toast.LENGTH_LONG).show();
                                        mbottomNavigationView.setSelectedItemId(R.id.ic_places);





                                    } else {
                                        Toast.makeText(mContext, "File Not uploaded", Toast.LENGTH_SHORT).show();
                                        RoundedPB.setVisibility(View.INVISIBLE);
                                        mbottomNavigationView.setSelectedItemId(R.id.ic_places);

                                        // Handle failures
                                        // ...
                                    }
                                }
            });
        }else{
            Toast.makeText(mContext, "File Not uploaded", Toast.LENGTH_SHORT).show();
        }
    }
}
