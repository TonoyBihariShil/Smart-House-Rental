package com.smarthouserental.landloardview;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarthouserental.R;

import java.util.HashMap;

public class LandLordPostUpdate extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    private String[] type = {"Family","Bachelor"};
    private EditText addressText,detailsText,askingRateText;
    private Spinner spinner;

    private String postId;
    private Bundle extras;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_lord_post_update);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Update");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        extras = getIntent().getExtras();
        if (extras != null){
            postId = extras.getString("postId");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("landLoardPost").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                addressText.setText(task.getResult().getString("address"));
                detailsText.setText(task.getResult().getString("details"));
                askingRateText.setText(task.getResult().getString("price"));
            }
        });

        addressText = findViewById(R.id.address);
        askingRateText = findViewById(R.id.askingRate);
        detailsText = findViewById(R.id.details);
        spinner = findViewById(R.id.spinner2);

        //-----------set spinner-----------------
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, type);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void updatePost(View view) {
        FirebaseFirestore updateDb = FirebaseFirestore.getInstance();

        HashMap<String,Object>updateData = new HashMap<>();
        if (addressText.getText().toString().equals("") || askingRateText.getText().toString().equals("") || detailsText.getText().toString().equals("")){
            Toast.makeText(LandLordPostUpdate.this,"Filed can not be empty",Toast.LENGTH_SHORT).show();
            return;
        }

        updateData.put("address",addressText.getText().toString());
        updateData.put("details",detailsText.getText().toString());
        updateData.put("price",askingRateText.getText().toString());
        updateData.put("type",spinner.getSelectedItem().toString());
        updateDb.collection("landLoardPost").document(postId).update(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(LandLordPostUpdate.this,"Successfully updated",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LandLordPostUpdate.this,LandLoardPostActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LandLordPostUpdate.this,"Please try again",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
