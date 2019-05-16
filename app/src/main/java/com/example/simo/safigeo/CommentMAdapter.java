package com.example.simo.safigeo;

import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simo.safigeo.Models.Comment;
import com.example.simo.safigeo.Models.Place;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentMAdapter extends RecyclerView.Adapter<CommentMAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    public CommentMAdapter(Context con , List<Comment> list)
    {
        mContext = con;
        mComments = list;

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.comment_item_layout, parent,false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment commentCurrent = mComments.get(position);
        Picasso.with(mContext).load(commentCurrent.getUserProfileImgUrl()).fit().into(holder.comment_item_user_img);
        holder.comment_item_user_name.setText(commentCurrent.getUserName());
        holder.comment_item_text_comment_body.setText(commentCurrent.getCommentString());
        String time = commentCurrent.getCommentDate().substring(commentCurrent.getCommentDate().length()-5);
        holder.comment_item_text_message_time.setText(time);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.comment_item_text_message_time.setText(commentCurrent.getCommentDate());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        holder.comment_item_text_message_time.setText(time);
                    }
                }, 2000);   //3 seconds

            }
        });

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView comment_item_user_img;
        TextView comment_item_user_name;
        TextView comment_item_text_comment_body;

        TextView comment_item_text_message_time;

        public CommentViewHolder(View itemView) {
            super(itemView);



            comment_item_user_img = itemView.findViewById(R.id.comment_item_user_img);
            comment_item_user_name = itemView.findViewById(R.id.comment_item_user_name);
            comment_item_text_comment_body = itemView.findViewById(R.id.comment_item_text_comment_body);

            comment_item_text_message_time = itemView.findViewById(R.id.comment_item_text_message_time);


        }




    }

}

