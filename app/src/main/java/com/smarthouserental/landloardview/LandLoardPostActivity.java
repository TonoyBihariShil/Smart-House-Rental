package com.smarthouserental.landloardview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarthouserental.R;
import com.smarthouserental.adepter.LandLoardPostAdepter;
import com.smarthouserental.model.LandLoardPost;
import com.smarthouserental.pref.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class LandLoardPostActivity extends AppCompatActivity {

    private List<LandLoardPost> landLoardPostList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noPostPublishText;

    private LandLoardPostAdepter adepter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_loard_post);
        noPostPublishText = findViewById(R.id.no_post_found_text);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("My Post");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        adepter = new LandLoardPostAdepter(landLoardPostList,LandLoardPostActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adepter);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("landLoardPost").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                noPostPublishText.setVisibility(View.VISIBLE);
                if (queryDocumentSnapshots != null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){
                            LandLoardPost post = doc.getDocument().toObject(LandLoardPost.class);
                            if (post.getUserId().equals(SharedPrefHelper.getKey("userId",LandLoardPostActivity.this))){
                                landLoardPostList.add(post);
                                adepter.notifyDataSetChanged();
                                noPostPublishText.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        });

    }
}
