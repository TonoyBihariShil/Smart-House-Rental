package com.smarthouserental.landloardview;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.smarthouserental.R;
import com.smarthouserental.pref.SharedPrefHelper;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandLoardCurrentProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView nameText,emailText,phoneText,userIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_loard_current_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameText = findViewById(R.id.name);
        emailText = findViewById(R.id.email);
        phoneText = findViewById(R.id.phone);
        userIdText = findViewById(R.id.userId);
        profileImage = findViewById(R.id.profileImage);


        Picasso.get().load(SharedPrefHelper.getKey("profileImage",LandLoardCurrentProfileActivity.this)).placeholder(R.drawable.avatar).into(profileImage);
        nameText.setText(SharedPrefHelper.getKey("name",LandLoardCurrentProfileActivity.this));
        emailText.setText(SharedPrefHelper.getKey("email",LandLoardCurrentProfileActivity.this));
        phoneText.setText(SharedPrefHelper.getKey("phoneNumber",LandLoardCurrentProfileActivity.this));
        userIdText.setText(SharedPrefHelper.getKey("userId",LandLoardCurrentProfileActivity.this));
    }

    public void editProfile(View view) {
        startActivity(new Intent(getApplicationContext(),LandLoardProfileEditActivity.class));
    }
}
