package com.example.simo.safigeo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simo.safigeo.Models.Place;
import com.facebook.drawee.view.SimpleDraweeView;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LocationMAdapter extends RecyclerView.Adapter<LocationMAdapter.LocationViewHolder> {

    private Context mContext;
    private List<Place> mPlaces;

    public LocationMAdapter(Context con , List<Place> list)
    {
        mContext = con;
        mPlaces = list;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.location_item_layout, parent,false);
        return new LocationViewHolder(v);
    }

    @Override
        public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {

        Place placeCurrent = mPlaces.get(position);

        Picasso.with(mContext).load(placeCurrent.getUserProfileImgUrl()).fit().into(holder.item_location_user_image);
        holder.item_location_user_name.setText(placeCurrent.getUserName());
        holder.item_location_date.setText(placeCurrent.getPlaceUploadDate());
        holder.item_location_name.setText(placeCurrent.getPlaceName());
        Log.d("TAG", "onBindViewHolder: img uri ; "+placeCurrent.getPlaceImgUrl());

        //Picasso.with(mContext).load(placeCurrent.getPlaceImgUrl()).into(holder.item_location_img);

        holder.item_location_img.setImageURI(placeCurrent.getPlaceImgUrl());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext , PlaceActivity.class);
                i.putExtra("placeId" , placeCurrent.getPlaceId());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });






    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView item_location_user_image;
        TextView item_location_user_name;
        TextView item_location_date;

        TextView item_location_name;

        SimpleDraweeView item_location_img;

        public LocationViewHolder(View itemView) {
            super(itemView);

            item_location_img = itemView.findViewById(R.id.item_location_img);


            item_location_user_image = itemView.findViewById(R.id.item_location_user_image);
            item_location_user_name = itemView.findViewById(R.id.item_location_user_name);
            item_location_date = itemView.findViewById(R.id.item_location_date);

            item_location_name = itemView.findViewById(R.id.item_location_name);


        }




    }
}
