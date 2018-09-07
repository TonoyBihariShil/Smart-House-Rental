package com.smarthouserental.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.smarthouserental.R;
import com.smarthouserental.landloardview.LandLoardPostAddActivity;
import com.smarthouserental.rentalview.RentalPostViewActivity;
import com.smarthouserental.util.Utils;

public class TypeOfUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_of_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Select Service");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }


    public void landOfLoard(View view) {
        if (Utils.isGPSEnabled(TypeOfUserActivity.this)){
            startActivity(new Intent(TypeOfUserActivity.this, LandLoardPostAddActivity.class));
        }else {
            Toast.makeText(getApplicationContext(),"Please Enable Your Gps",Toast.LENGTH_SHORT).show();
        }
    }

    public void rental(View view) {
        if (Utils.isGPSEnabled(TypeOfUserActivity.this)){
            startActivity(new Intent(TypeOfUserActivity.this, RentalPostViewActivity.class));
        }else {
            Toast.makeText(getApplicationContext(),"Please Enable Your Gps",Toast.LENGTH_SHORT).show();
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
}
