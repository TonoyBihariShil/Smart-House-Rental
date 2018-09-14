package com.smarthouserental.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.smarthouserental.R;
import com.smarthouserental.landloardview.LandLoardPostAddActivity;
import com.smarthouserental.pref.SharedPrefHelper;
import com.smarthouserental.rentalview.RentalPostViewActivity;
import com.smarthouserental.util.Utils;

public class TypeOfUserActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_of_user);

        firebaseAuth = FirebaseAuth.getInstance();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            super.onBackPressed();
            finish();
        }else if (item.getItemId() == R.id.action_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(TypeOfUserActivity.this)
                    .setMessage("Are you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firebaseAuth.signOut();
                            SharedPreferences pref1 = getSharedPreferences(SharedPrefHelper.RENTAL_AREA,0);
                            SharedPreferences pref2 = getSharedPreferences(SharedPrefHelper.RENTAL_TYPE,0);
                            SharedPreferences pref3 = getSharedPreferences("SmartRentPref",0);
                            pref1.edit().clear().apply();
                            pref2.edit().clear().apply();
                            pref3.edit().clear().apply();
                            startActivity(new Intent(TypeOfUserActivity.this,LoginActivity.class));
                            finish();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
