package com.smarthouserental.landloardview;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarthouserental.R;
import com.smarthouserental.adepter.PostImageAdepter;
import com.smarthouserental.pref.SharedPrefHelper;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class LandLoardPostAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //-------------xml instance----------------------
    private RecyclerView recyclerView;
    private TextView locationAddresss, userName;
    private CircleImageView profileImage;
    private Spinner spinner, typeSppiner;
    private EditText detailsText, price, address;

    //-----------class instance--------------
    private static final int REQUEST_LOCATION = 1;
    private static final int CUSTOM_REQUEST_CODE = 2;
    private ArrayList<String> filePathList = new ArrayList<>();
    private PostImageAdepter adepter;
    private String[] areaName = {"Shukrabad", "Firmgate", "Shamoli", "Mohammadpur", "Dhanmondi32"};
    private String[] type = {"Family", "Bachelor"};
    private String latitude = "", longitude = "";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;


    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_loard_post_add);
        progressDialog = new ProgressDialog(LandLoardPostAddActivity.this);
        progressDialog.setMessage("posting...");
        progressDialog.setCancelable(false);

        //-------------xml instance----------------------
        recyclerView = findViewById(R.id.recyclerView);
        locationAddresss = findViewById(R.id.locationAddress);
        profileImage = findViewById(R.id.profileImage);
        spinner = findViewById(R.id.spinner);
        typeSppiner = findViewById(R.id.spinner2);
        userName = findViewById(R.id.userName);
        detailsText = findViewById(R.id.details);
        price = findViewById(R.id.price);
        address = findViewById(R.id.homeNo);


        userName.setText(SharedPrefHelper.getKey("name", LandLoardPostAddActivity.this));
        auth = FirebaseAuth.getInstance();
        //--------------action bar------------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //---------------recycler view-------------------------
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        buildGoogleApiClient();
        Picasso.get().load(SharedPrefHelper.getKey("profileImage", LandLoardPostAddActivity.this)).placeholder(R.drawable.avatar).into(profileImage);


        //-----------set spinner-----------------
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areaName);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        typeSppiner.setOnItemSelectedListener(this);
        ArrayAdapter<String> aaa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, type);
        aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSppiner.setAdapter(aaa);

    }


    public void addImage(View view) {

        FilePickerBuilder.getInstance().setMaxCount(5)
                .setSelectedFiles(filePathList)
                .setActivityTheme(R.style.LibAppTheme)
                .pickPhoto(this, CUSTOM_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CUSTOM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                filePathList.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                adepter = new PostImageAdepter(filePathList);
                recyclerView.setAdapter(adepter);
                adepter.notifyDataSetChanged();
            }
        }

    }


   /* private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (location != null) {
                    locationAddresss.setText(String.valueOf(location.getLatitude() + "  " + location.getLongitude()));
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                } else if (location1 != null) {
                    locationAddresss.setText(String.valueOf(location.getLatitude() + "  " + location.getLongitude()));
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                } else if (location2 != null) {
                    locationAddresss.setText(String.valueOf(location.getLatitude() + "  " + location.getLongitude()));
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                } else {
                    Toast.makeText(getApplicationContext(), "location not found. Please reboot your device", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }*/

    public void gotoProfileImagePage(View view) {
        startActivity(new Intent(LandLoardPostAddActivity.this, LandLoardCurrentProfileActivity.class));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean isFlag = true;

    public void publishPost(View view) {
        //-------------check some field-----------------
        if (filePathList.size() <= 0) {
            Toast.makeText(getApplicationContext(), "please upload image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (detailsText.getText().toString().isEmpty()) {
            detailsText.setError("Please add details");
            return;
        }

        if (address.getText().toString().isEmpty()) {
            address.setError("home address required");
            return;
        }


        if (price.getText().toString().isEmpty()) {
            price.setError("price required");
            return;
        }

        isFlag = true;
        progressDialog.show();
        db = FirebaseFirestore.getInstance();
        final String postId = FirebaseDatabase.getInstance().getReference().push().getKey();
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("userId", auth.getCurrentUser().getUid());
        postMap.put("userName", userName.getText().toString());
        postMap.put("latitude", latitude);
        postMap.put("longitude", longitude);
        postMap.put("price", price.getText().toString());
        postMap.put("address", address.getText().toString());
        postMap.put("profileImageUrl", SharedPrefHelper.getKey("profileImage", LandLoardPostAddActivity.this));
        postMap.put("area", spinner.getSelectedItem().toString());
        postMap.put("type", typeSppiner.getSelectedItem().toString());
        postMap.put("postId", postId);
        postMap.put("time", FieldValue.serverTimestamp());
        postMap.put("details", detailsText.getText().toString());
        db.collection("landLoardPost").document(postId).set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                for (int i = 0; i < filePathList.size(); i++) {
                    Bitmap bitmap = BitmapFactory.decodeFile(filePathList.get(i));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    byte[] data = baos.toByteArray();

                    String randomId = UUID.randomUUID().toString();
                    final StorageReference reference = FirebaseStorage.getInstance().getReference().child("loardRentImage").child(randomId + ".jpg");
                    UploadTask uploadTask = reference.putBytes(data);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Something went wrong. please try again", Toast.LENGTH_SHORT).show();
                                throw task.getException();
                            }


                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                HashMap<String, Object> imageMap = new HashMap<>();
                                imageMap.put("image", downloadUri.toString());
                                if (isFlag) {
                                    db.collection("landLoardPost").document(postId).update(imageMap);
                                    isFlag = false;
                                }
                                FirebaseFirestore imagedb = FirebaseFirestore.getInstance();
                                imagedb.collection("landLoardPost").document(postId).collection("postiamges").add(imageMap);


                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Something went wrong. please try again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
                progressDialog.dismiss();
                detailsText.setText("");
                price.setText("");
                address.setText("");
                filePathList.clear();
                adepter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Post has been published", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong. please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void landLoardPost(View view) {
        startActivity(new Intent(LandLoardPostAddActivity.this, LandLoardPostActivity.class));
    }


    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        googleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
       if (location != null){
           locationAddresss.setText(String.valueOf(location.getLatitude() + "  " + location.getLongitude()));
           latitude = String.valueOf(location.getLatitude());
           longitude = String.valueOf(location.getLongitude());
           if (googleApiClient != null){
               googleApiClient.disconnect();
           }
       }else {
           Toast.makeText(getApplicationContext(),"Device location not found",Toast.LENGTH_SHORT).show();
           finish();
       }
    }
}
