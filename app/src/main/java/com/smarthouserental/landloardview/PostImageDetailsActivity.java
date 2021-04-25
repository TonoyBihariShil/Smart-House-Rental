package com.smarthouserental.landloardview;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarthouserental.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostImageDetailsActivity extends AppCompatActivity {

    private Bundle extras;
    private PostImageDetailsAdepter adepter;
    private List<String> imageList = new ArrayList<>();

    //----------------xml instance-----------------
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image_details);

        //-------------action bar------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Post all image");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adepter = new PostImageDetailsAdepter(imageList,PostImageDetailsActivity.this);
        recyclerView.setAdapter(adepter);


        extras = getIntent().getExtras();
        if (extras != null){

            String postId = extras.getString("postId");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("landLoardPost").document(postId).collection("postiamges").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult() != null){
                       for (int i = 0 ; i<task.getResult().size() ; i++){
                           imageList.add(task.getResult().getDocumentChanges().get(i).getDocument().getString("image"));
                           adepter.notifyDataSetChanged();
                       }
                    }
                }
            });

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            super.onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class PostImageDetailsAdepter extends RecyclerView.Adapter<PostImageDetailsAdepter.PostImageDetailsViewHolder>{

        private List<String> postImageLinkUrlList;
        private Activity activity;

        public PostImageDetailsAdepter(List<String> postImageLinkUrlList, Activity activity) {
            this.postImageLinkUrlList = postImageLinkUrlList;
            this.activity = activity;
        }

        @NonNull
        @Override
        public PostImageDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_post_image_details,parent,false);
            return new PostImageDetailsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostImageDetailsViewHolder holder, int position) {
            Picasso.get().load(postImageLinkUrlList.get(position)).into(holder.postImages);
        }

        @Override
        public int getItemCount() {
            return postImageLinkUrlList.size();
        }

        class PostImageDetailsViewHolder extends RecyclerView.ViewHolder {

            private ImageView postImages;
            PostImageDetailsViewHolder(View itemView) {
                super(itemView);
                postImages = itemView.findViewById(R.id.list_post_image_details_image);
            }
        }
    }
}
