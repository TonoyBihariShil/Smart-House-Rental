package com.smarthouserental.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarthouserental.R;
import com.smarthouserental.adepter.CommentAdepter;
import com.smarthouserental.model.Comments;
import com.smarthouserental.pref.SharedPrefHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class CommentsActivity extends AppCompatActivity {

    //---------xml instance---------------
    private RecyclerView recyclerView;
    private EditText inputComments;

    private CommentAdepter adepter;
    private Bundle extras;
    private String postId;
    private ArrayList<Comments> commentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        recyclerView = findViewById(R.id.recyclerView);
        inputComments = findViewById(R.id.input_comments);

        extras = getIntent().getExtras();
        if (extras != null){
            postId = extras.getString("postId");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adepter = new CommentAdepter(commentsList,CommentsActivity.this,postId);
        recyclerView.setAdapter(adepter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("landLoardPost").document(postId).collection("Comments").addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0){
                   for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                       if (doc.getType() == DocumentChange.Type.ADDED){
                           Comments comments = doc.getDocument().toObject(Comments.class);
                           comments.setCommentId(doc.getDocument().getId());
                           commentsList.add(comments);
                           adepter.notifyDataSetChanged();
                       }

                   }
                }
            }
        });

    }

    public void commentPost(View view) {
        if (inputComments.getText().toString().isEmpty()){
            inputComments.setError("Write a comment");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> comment = new HashMap<>();
        comment.put("comment",inputComments.getText().toString());
        comment.put("name", SharedPrefHelper.getKey("name",CommentsActivity.this));
        db.collection("landLoardPost").document(postId).collection("Comments").add(comment);
        inputComments.setText("");

    }
}
