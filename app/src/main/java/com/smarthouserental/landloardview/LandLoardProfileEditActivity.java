package com.smarthouserental.landloardview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import com.smarthouserental.pref.SharedPrefHelper;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandLoardProfileEditActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private EditText nameText,emailText;
    private TextView phoneText,userIdText;
    private CircleImageView profileImage;

    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_loard_profile_edit);

        //-------------action bar------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Profile Update");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(LandLoardProfileEditActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait...");

        nameText = findViewById(R.id.name);
        emailText = findViewById(R.id.email);
        phoneText = findViewById(R.id.phoneNumber);
        userIdText = findViewById(R.id.userId);
        profileImage = findViewById(R.id.profileImage);


        Picasso.get().load(SharedPrefHelper.getKey("profileImage",LandLoardProfileEditActivity.this)).into(profileImage);
        nameText.setText(SharedPrefHelper.getKey("name",LandLoardProfileEditActivity.this));
        emailText.setText(SharedPrefHelper.getKey("email",LandLoardProfileEditActivity.this));
        phoneText.setText(SharedPrefHelper.getKey("phoneNumber",LandLoardProfileEditActivity.this));
        userIdText.setText(SharedPrefHelper.getKey("userId",LandLoardProfileEditActivity.this));
    }

    public void uploadProfileImage(View view) {
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

    public void updateProfile(View view) {

      if (imageUri != null){
          progressDialog.show();
          final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("rentalUserImage/"+userIdText.getText().toString()+".jpg");
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
                  final Uri downloadUri = task.getResult();
                  FirebaseFirestore db = FirebaseFirestore.getInstance();
                  Map<String, Object> user = new HashMap<>();
                  user.put("userName", nameText.getText().toString());
                  user.put("email", emailText.getText().toString());
                  user.put("downloadUrl", downloadUri.toString());
                  user.put("phoneNumber", phoneText.getText().toString());
                  user.put("userId", userIdText.getText().toString());
                  db.collection("users/").document(userIdText.getText().toString()).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          SharedPrefHelper.putKey(LandLoardProfileEditActivity.this,"name",nameText.getText().toString());
                          SharedPrefHelper.putKey(LandLoardProfileEditActivity.this,"email",emailText.getText().toString());
                          SharedPrefHelper.putKey(LandLoardProfileEditActivity.this,"profileImage",downloadUri.toString());
                          progressDialog.dismiss();
                          Toast.makeText(getApplicationContext(),"Profile successfully updated",Toast.LENGTH_SHORT).show();
                          startActivity(new Intent(getApplicationContext(),LandLoardCurrentProfileActivity.class));
                      }
                  }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          progressDialog.dismiss();
                          Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                      }
                  });

              }
          });



      }else {
          progressDialog.show();
          FirebaseFirestore db = FirebaseFirestore.getInstance();
          Map<String, Object> user = new HashMap<>();
          user.put("userName", nameText.getText().toString());
          user.put("email", emailText.getText().toString());
          user.put("phoneNumber", phoneText.getText().toString());
          user.put("userId", userIdText.getText().toString());
          db.collection("users/").document(userIdText.getText().toString()).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  SharedPrefHelper.putKey(LandLoardProfileEditActivity.this,"name",nameText.getText().toString());
                  SharedPrefHelper.putKey(LandLoardProfileEditActivity.this,"email",emailText.getText().toString());
                  progressDialog.dismiss();
                  Toast.makeText(getApplicationContext(),"Profile successfully updated",Toast.LENGTH_SHORT).show();
                  startActivity(new Intent(getApplicationContext(),LandLoardCurrentProfileActivity.class));
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  progressDialog.dismiss();
                  Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
              }
          });

      }
    }
}
