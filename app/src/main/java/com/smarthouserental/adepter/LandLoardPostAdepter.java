package com.smarthouserental.adepter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarthouserental.R;
import com.smarthouserental.activities.CommentsActivity;
import com.smarthouserental.landloardview.LandLoardPostActivity;
import com.smarthouserental.landloardview.LandLordPostUpdate;
import com.smarthouserental.landloardview.PostImageDetailsActivity;
import com.smarthouserental.model.LandLoardPost;
import com.smarthouserental.pref.SharedPrefHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nullable;


public class LandLoardPostAdepter extends RecyclerView.Adapter<LandLoardPostAdepter.LandLoardPostViewHolder>{

    private List<LandLoardPost> landLoardPostList;
    private Activity activity;

    public LandLoardPostAdepter(List<LandLoardPost> landLoardPostList, Activity activity) {
        this.landLoardPostList = landLoardPostList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public LandLoardPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_land_loard_post,parent,false);
        return new LandLoardPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LandLoardPostViewHolder holder, int position) {
        final LandLoardPost post = landLoardPostList.get(position);
        holder.nameText.setText(SharedPrefHelper.getKey("name",activity));
        holder.areaText.setText("Area: "+post.getArea());
        holder.typeText.setText("Type: "+post.getType());
        holder.details.setText(post.getDetails());
        holder.address.setText(post.getAddress());
        holder.price.setText(post.getPrice());
        Picasso.get().load(post.getImage()).into(holder.profileImage);


        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, PostImageDetailsActivity.class)
                        .putExtra("postId",post.getPostId())
                );
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("landLoardPost").document(post.getPostId()).collection("Comments").addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0){
                    holder.comments.setText(String.valueOf(queryDocumentSnapshots.size())+" Comments");
                }
            }
        });

        holder.commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, CommentsActivity.class).putExtra("postId",post.getPostId()));
            }
        });

        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, LandLordPostUpdate.class)
                        .putExtra("postId",post.getPostId())
                );
                activity.finish();
            }
        });

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                        .setMessage("Are you want to delete post?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("landLoardPost").document(post.getPostId()).delete();
                                Toast.makeText(activity,"successfully deleted",Toast.LENGTH_LONG).show();
                                activity.finish();
                                activity.startActivity(new Intent(activity, LandLoardPostActivity.class));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });



    }

    @Override
    public int getItemCount() {
        return landLoardPostList.size();
    }

    class LandLoardPostViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText,areaText,typeText,details,address,price,comments;
        private LinearLayout commentView;
        private ImageView profileImage,deleteIcon,editIcon;
        LandLoardPostViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.name);
            areaText = itemView.findViewById(R.id.area);
            typeText = itemView.findViewById(R.id.type);
            details = itemView.findViewById(R.id.details);
            profileImage = itemView.findViewById(R.id.post_image);
            address = itemView.findViewById(R.id.address);
            price = itemView.findViewById(R.id.price);
            comments = itemView.findViewById(R.id.comments);
            commentView = itemView.findViewById(R.id.comments_layout);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
            editIcon = itemView.findViewById(R.id.editIcon);
        }
    }
}
