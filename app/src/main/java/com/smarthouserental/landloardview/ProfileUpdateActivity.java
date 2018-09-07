package com.smarthouserental.landloardview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarthouserental.R;
import com.smarthouserental.activities.TypeOfUserActivity;
import com.smarthouserental.pref.SharedPrefHelper;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUpdateActivity extends AppCompatActivity {

    //-----------xml instance---------------
    private EditText nameEt,emailEt;
    private CircleImageView profileImage;

    //---------------class instance----------------
    private Uri imageUri;
    private int GALLERY_REQUEST_CODE = 1;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        progressDialog = new ProgressDialog(ProfileUpdateActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);

        nameEt = findViewById(R.id.profileName);
        emailEt = findViewById(R.id.email);
        profileImage = findViewById(R.id.profileImage);



        //---------action bar----------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile Update");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addProfileImage(View view) {
        //----------open the gallery----------------
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"select an image"),GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                imageUri = data.getData();
                profileImage.setImageURI(imageUri);
            }
        }
    }

    public void saveProfile(View view) {
        final String name = nameEt.getText().toString();
        final String email = emailEt.getText().toString();

        if (name.isEmpty()){
            nameEt.setError("name required");
            return;
        }

        if (email.isEmpty()){
            emailEt.setError("email required");
            return;
        }

        if (imageUri == null){
            Toast.makeText(getApplicationContext(),"upload profile iamge",Toast.LENGTH_SHORT).show();
        }

        progressDialog.show();
        String userId = SharedPrefHelper.getKey("userId",ProfileUpdateActivity.this);
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("rentalUserImage/"+userId+".jpg");
        UploadTask uploadTask  = storageRef.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                String userId = SharedPrefHelper.getKey("userId",ProfileUpdateActivity.this);
                if (task.isSuccessful()) {
                    final Uri downloadUri = task.getResult();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();
                    user.put("userName", name);
                    user.put("downloadUrl", downloadUri.toString());
                    user.put("email", email);
                    user.put("phoneNumber", SharedPrefHelper.getKey("phoneNumber",ProfileUpdateActivity.this));
                    user.put("userId", SharedPrefHelper.getKey("userId",ProfileUpdateActivity.this));
                    db.collection("users/").document(userId).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            SharedPrefHelper.putKey(ProfileUpdateActivity.this,"name",name);
                            SharedPrefHelper.putKey(ProfileUpdateActivity.this,"profileImage",downloadUri.toString());
                            SharedPrefHelper.putKey(ProfileUpdateActivity.this,"email",email);
                            startActivity(new Intent(ProfileUpdateActivity.this,TypeOfUserActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"something went worng. please try again",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });


                } else {
                   Toast.makeText(getApplicationContext(),"something went worng. please try again",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });



    }
}
