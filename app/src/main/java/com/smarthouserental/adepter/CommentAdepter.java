package com.smarthouserental.adepter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarthouserental.R;
import com.smarthouserental.activities.ReplayActivity;
import com.smarthouserental.model.Comments;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class CommentAdepter extends RecyclerView.Adapter<CommentAdepter.CommentViewHolder>{

    private ArrayList<Comments> commentList;
    private Activity activity;
    private String postId;

    public CommentAdepter(ArrayList<Comments> commentList, Activity activity,String postId) {
        this.commentList = commentList;
        this.activity = activity;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comments_item,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {
        final Comments comments = commentList.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("landLoardPost").document(postId).collection("Comments").document(comments.getCommentId()).collection("Replays").addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0){
                    holder.replayText.setText(String.valueOf(queryDocumentSnapshots.size())+" Replay");
                }
            }
        });


        holder.commentText.setText(comments.getName()+" : "+comments.getComment());
        holder.replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ReplayActivity.class)
                        .putExtra("commentId",comments.getCommentId())
                        .putExtra("postId",postId));
            }
        });


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView commentText,replayText;
        private LinearLayout replayButton;
        public CommentViewHolder(View itemView) {
            super(itemView);

            commentText = itemView.findViewById(R.id.comments_text);
            replayButton = itemView.findViewById(R.id.replay_Button);
            replayText = itemView.findViewById(R.id.replayText);
        }
    }
}
