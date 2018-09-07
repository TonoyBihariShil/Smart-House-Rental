package com.smarthouserental.rentalview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarthouserental.R;
import com.smarthouserental.adepter.RentalPostAdepter;
import com.smarthouserental.model.GoogleDirection;
import com.smarthouserental.model.LandLoardPost;
import com.smarthouserental.model.RentalPost;
import com.smarthouserental.network.ApiClient;
import com.smarthouserental.network.RouteApi;
import com.smarthouserental.pref.SharedPrefHelper;

import java.util.ArrayList;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentalPostViewActivity extends AppCompatActivity {

    private FirebaseFirestore postDb,userDb;
    private RecyclerView recyclerView;

    private ArrayList<RentalPost> rentalPostList = new ArrayList<>();
    private RentalPostAdepter adepter;

    private String areaSP,typeSP;
    private LatLng sourceLatLng;
    private final int REQUEST_LOCATION =1;
    private TextView noMatchFoundText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_pos_view);

        //-------------------action bar----------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Rental Feed");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getLocation();

        recyclerView = findViewById(R.id.recyclerView);
        noMatchFoundText = findViewById(R.id.no_match_found_text);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adepter = new RentalPostAdepter(rentalPostList,RentalPostViewActivity.this);
        recyclerView.setAdapter(adepter);

        areaSP = SharedPrefHelper.getRentalArea(SharedPrefHelper.RENTAL_AREA,RentalPostViewActivity.this);
        typeSP = SharedPrefHelper.getRentalType(SharedPrefHelper.RENTAL_TYPE,RentalPostViewActivity.this);


        postDb = FirebaseFirestore.getInstance();
        userDb = FirebaseFirestore.getInstance();
        postDb.collection("landLoardPost").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable final FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() >= 1){
                    rentalPostList.clear();
                    if (queryDocumentSnapshots != null){
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            final LandLoardPost post = doc.getDocument().toObject(LandLoardPost.class);
                            String id = post.getUserId();

                            if (id != null && !id.equals("")){
                                noMatchFoundText.setVisibility(View.GONE);

                                userDb.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        final String userName = task.getResult().getString("userName");
                                        final String userImageUrl = task.getResult().getString("downloadUrl");
                                        if (areaSP.trim().equals(post.getArea().trim()) && typeSP.trim().equals(post.getType().trim())){
                                            noMatchFoundText.setVisibility(View.GONE);
                                            String origin = String.valueOf(sourceLatLng.latitude)+","+sourceLatLng.longitude;
                                            String destimation = post.getLatitude()+","+post.getLongitude();

                                            Call<GoogleDirection> call = ApiClient.getClient().create(RouteApi.class).getGoogleDirectionApi(origin,destimation);
                                            call.enqueue(new Callback<GoogleDirection>() {
                                                @Override
                                                public void onResponse(@NonNull Call<GoogleDirection> call, @NonNull Response<GoogleDirection> response) {
                                                    noMatchFoundText.setVisibility(View.GONE);
                                                    if (response.body().getRouteList().size() <= 0 ){
                                                        Toast.makeText(RentalPostViewActivity.this,"Daily limit is over",Toast.LENGTH_LONG).show();
                                                        rentalPostList.add(new RentalPost(userName,userImageUrl,post.getImage(),"2KM","15 mins",post.getArea(),post.getType(),post.getDetails(),post.getPrice(),post.getAddress(),post.getPostId(),sourceLatLng.latitude,sourceLatLng.longitude,Double.parseDouble(post.getLatitude()),Double.parseDouble(post.getLatitude())));
                                                        adepter.notifyDataSetChanged();
                                                    }else {
                                                        rentalPostList.add(new RentalPost(userName,userImageUrl,post.getImage(),response.body().getRouteList().get(0).getLegs().get(0).getDistance().getDistanceText(),response.body().getRouteList().get(0).getLegs().get(0).getDuration().getDurationText(),post.getArea(),post.getType(),post.getDetails(),post.getPrice(),post.getAddress(),post.getPostId(),sourceLatLng.latitude,sourceLatLng.longitude,Double.parseDouble(post.getLatitude()),Double.parseDouble(post.getLongitude())));
                                                        adepter.notifyDataSetChanged();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NonNull Call<GoogleDirection> call, @NonNull Throwable t) {
                                                    Toast.makeText(getApplicationContext(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }else {
                                            noMatchFoundText.setVisibility(View.VISIBLE);
                                        }

                                    }
                                });
                            }else {
                                noMatchFoundText.setVisibility(View.VISIBLE);
                            }
                        }
                    }else {
                        noMatchFoundText.setVisibility(View.VISIBLE);
                    }

                }else {
                    noMatchFoundText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity)getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            sourceLatLng = new LatLng(23.772226, 90.367687);

        } else {
            if (locationManager != null){
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

                if (location != null) {
                    sourceLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                } else  if (location1 != null) {
                    sourceLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                } else  if (location2 != null) {
                    sourceLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                }else {
                    Toast.makeText(getApplicationContext(),"location not found. Please reboot your device",Toast.LENGTH_SHORT).show();
                    sourceLatLng = new LatLng(23.772226, 90.367687);
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rental_post_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            super.onBackPressed();
            finish();
        }else if (item.getItemId() == R.id.action_settings){
            startActivity(new Intent(RentalPostViewActivity.this,SettingsActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
