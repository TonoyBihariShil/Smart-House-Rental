package com.smarthouserental.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarthouserental.R;
import com.smarthouserental.landloardview.ProfileUpdateActivity;
import com.smarthouserental.pref.SharedPrefHelper;
import com.smarthouserental.util.Utils;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    //-----------------class instance------------------
    private static final int RC_SIGN_IN = 123;
    private static final int REQUEST_PERMISSION = 12;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestSelfPermission();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        //-----------check user already logged in or not?---------------

        if (Utils.haveNetworkConnection(LoginActivity.this)){
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null){
                checkUserStatus();
            }
        }

        if (!Utils.haveNetworkConnection(LoginActivity.this)){
            Toast.makeText(getApplicationContext(),"Please enable your network connection",Toast.LENGTH_SHORT).show();
        }


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                auth = FirebaseAuth.getInstance();
                if (Utils.haveNetworkConnection(LoginActivity.this)){
                    auth = FirebaseAuth.getInstance();
                    if (auth.getCurrentUser() != null){
                        checkUserStatus();
                    }
                }
                handler.postDelayed(this, 2000);
            }
        }, 2000);



    }

    private void requestSelfPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
    }

    public void continueWithPhoneNumber(View view) {
        handler.removeCallbacks(null);
        if (Utils.haveNetworkConnection(LoginActivity.this)){
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build()))
                    .build(),RC_SIGN_IN
            );
        }else {
            Toast.makeText(getApplicationContext(),"Please enable your network connection",Toast.LENGTH_SHORT).show();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            final IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){

                            if (task.getResult().getString("userName") != null){
                                String userName = task.getResult().getString("userName");
                                String phoneNumber = task.getResult().getString("phoneNumber");
                                String email = task.getResult().getString("email");
                                String profileImage = task.getResult().getString("downloadUrl");
                                String userId = task.getResult().getString("userId");

                                SharedPrefHelper.putKey(LoginActivity.this,"phoneNumber",phoneNumber);
                                SharedPrefHelper.putKey(LoginActivity.this,"name",userName);
                                SharedPrefHelper.putKey(LoginActivity.this,"profileImage",profileImage);
                                SharedPrefHelper.putKey(LoginActivity.this,"email",email);
                                SharedPrefHelper.putKey(LoginActivity.this,"userId",userId);

                                startActivity(new Intent(LoginActivity.this,TypeOfUserActivity.class));
                                finish();

                            }else {
                                SharedPrefHelper.putKey(LoginActivity.this,"phoneNumber",response.getPhoneNumber());
                                SharedPrefHelper.putKey(LoginActivity.this,"userId",auth.getCurrentUser().getUid());
                                startActivity(new Intent(LoginActivity.this,ProfileUpdateActivity.class));
                                finish();
                            }

                        }else {
                            SharedPrefHelper.putKey(LoginActivity.this,"phoneNumber",response.getPhoneNumber());
                            SharedPrefHelper.putKey(LoginActivity.this,"userId",auth.getCurrentUser().getUid());
                            startActivity(new Intent(LoginActivity.this,ProfileUpdateActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {

                    return;
                }

                Log.e("error", "Sign-in error: ", response.getError());
            }
        }
    }

    private void checkUserStatus(){
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    if (task.getResult().getString("userName") != null){
                        String userName = task.getResult().getString("userName");
                        String phoneNumber = task.getResult().getString("phoneNumber");
                        String email = task.getResult().getString("email");
                        String profileImage = task.getResult().getString("downloadUrl");
                        String userId = task.getResult().getString("userId");

                        SharedPrefHelper.putKey(LoginActivity.this,"phoneNumber",phoneNumber);
                        SharedPrefHelper.putKey(LoginActivity.this,"name",userName);
                        SharedPrefHelper.putKey(LoginActivity.this,"profileImage",profileImage);
                        SharedPrefHelper.putKey(LoginActivity.this,"email",email);
                        SharedPrefHelper.putKey(LoginActivity.this,"userId",userId);

                        progressDialog.dismiss();
                        handler.removeCallbacksAndMessages(null);
                        startActivity(new Intent(LoginActivity.this,TypeOfUserActivity.class));
                        finish();

                    }else {
                        progressDialog.dismiss();
                        handler.removeCallbacksAndMessages(null);
                        startActivity(new Intent(LoginActivity.this,ProfileUpdateActivity.class));
                        finish();
                    }
                }else {
                    progressDialog.dismiss();
                    handler.removeCallbacksAndMessages(null);
                    startActivity(new Intent(LoginActivity.this,ProfileUpdateActivity.class));
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                //----------phone state permission result---------------
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

                    Log.v("SUCCESS","SUCCESS");


                } else {
                    requestSelfPermission();
                }

            }
            break;
        }
    }


}
