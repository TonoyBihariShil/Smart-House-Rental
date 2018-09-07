package com.smarthouserental.adepter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarthouserental.R;
import com.smarthouserental.activities.CommentsActivity;
import com.smarthouserental.activities.DirectionActivity;
import com.smarthouserental.landloardview.PostImageDetailsActivity;
import com.smarthouserental.model.RentalPost;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class RentalPostAdepter extends RecyclerView.Adapter<RentalPostAdepter.RentalPostViewHolder>{

    private ArrayList<RentalPost> landLoardPostsList;
    private Activity activity;

    public RentalPostAdepter(ArrayList<RentalPost> landLoardPostsList, Activity activity) {
        this.landLoardPostsList = landLoardPostsList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RentalPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rental_post,parent,false);
        return new RentalPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RentalPostViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final RentalPost rentalPost = landLoardPostsList.get(position);
        holder.nameText.setText(rentalPost.getUserName());
        holder.areaText.setText(rentalPost.getArea());
        holder.typeText.setText(rentalPost.getType());
        holder.detailsText.setText(rentalPost.getDetails());
        Picasso.get().load(rentalPost.getPostImage()).into(holder.rentalPostImage);
        Picasso.get().load(rentalPost.getProfileImage()).into(holder.profileImage);
        holder.address.setText(rentalPost.getAddress());
        holder.price.setText(rentalPost.getPrice());
        holder.distance.setText(rentalPost.getDistance());
        holder.time.setText(rentalPost.getTime());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("landLoardPost").document(rentalPost.getPostId()).collection("Comments").addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0){
                    holder.comments.setText(String.valueOf(queryDocumentSnapshots.size())+" Comments");
                }
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, PostImageDetailsActivity.class).putExtra("postId",rentalPost.getPostId()));
            }
        });

        holder.commentsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, CommentsActivity.class).putExtra("postId",rentalPost.getPostId()));
            }
        });


        holder.directionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, DirectionActivity.class)
                        .putExtra("s_latitide",rentalPost.getS_lattude())
                        .putExtra("s_longitude",rentalPost.getS_longitide())
                        .putExtra("d_latitide",rentalPost.getD_lattude())
                        .putExtra("d_longitude",rentalPost.getD_longitide())

                );
            }
        });


    }

    @Override
    public int getItemCount() {
        return landLoardPostsList.size();
    }

    class RentalPostViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText,areaText,typeText,detailsText,address,price,distance,time,comments;
        private RelativeLayout commentsText;
        private ImageView rentalPostImage;
        private ImageView profileImage,directionImage;
        private View view;

        public RentalPostViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            nameText = itemView.findViewById(R.id.name);
            areaText = itemView.findViewById(R.id.area);
            typeText = itemView.findViewById(R.id.type);
            commentsText = itemView.findViewById(R.id.comments_layout);
            detailsText = itemView.findViewById(R.id.details);
            rentalPostImage = itemView.findViewById(R.id.postImage);
            profileImage = itemView.findViewById(R.id.profileImage);
            address = itemView.findViewById(R.id.address);
            price = itemView.findViewById(R.id.price);
            distance = itemView.findViewById(R.id.distance);
            time = itemView.findViewById(R.id.time);
            comments = itemView.findViewById(R.id.comments);
            directionImage = itemView.findViewById(R.id.directionImage);

        }
    }
}
