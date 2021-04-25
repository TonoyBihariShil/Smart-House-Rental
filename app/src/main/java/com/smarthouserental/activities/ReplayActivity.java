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
import com.smarthouserental.adepter.ReplayAdepter;
import com.smarthouserental.model.Replay;
import com.smarthouserental.pref.SharedPrefHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ReplayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputReplay;

    private ReplayAdepter adepter;
    private Bundle extras;
    private String commentsId,postId;
    private ArrayList<Replay> replayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);

        extras = getIntent().getExtras();
        if (extras != null){
            commentsId = extras.getString("commentId");
            postId = extras.getString("postId");
        }

        recyclerView = findViewById(R.id.recyclerView);
        inputReplay = findViewById(R.id.input_replay);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adepter = new ReplayAdepter(replayList,ReplayActivity.this);
        recyclerView.setAdapter(adepter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("landLoardPost").document(postId).collection("Comments").document(commentsId).collection("Replays").addSnapshotListener(ReplayActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){
                            Replay replay = doc.getDocument().toObject(Replay.class);
                            replayList.add(replay);
                            adepter.notifyDataSetChanged();
                        }

                    }
                }
            }
        });
    }

    public void replayPost(View view) {
        if (inputReplay.getText().toString().isEmpty()){
            inputReplay.setError("Write a replay");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> replay = new HashMap<>();
        replay.put("replay",inputReplay.getText().toString());
        replay.put("name", SharedPrefHelper.getKey("name",ReplayActivity.this));
        db.collection("landLoardPost").document(postId).collection("Comments").document(commentsId).collection("Replays").add(replay);
        inputReplay.setText("");

    }
}
