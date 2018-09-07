package com.smarthouserental.pref;

import android.app.Activity;
import android.content.SharedPreferences;

public class SharedPrefHelper {


    public static final String RENTAL_TYPE ="RENTAL_TYPE";
    public static final String RENTAL_AREA ="RENTAL_AREA";

    private static SharedPreferences sharedPreferences;
    public static void putKey(Activity activity,String key,String value){
        sharedPreferences = activity.getSharedPreferences("SmartRentPref",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String getKey(String key,Activity activity){
        sharedPreferences = activity.getSharedPreferences("SmartRentPref",0);
        return sharedPreferences.getString(key,"nope");
    }

    public static void  putRentalType(Activity activity,String value){
        sharedPreferences = activity.getSharedPreferences("RentalTypePref",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RENTAL_TYPE,value);
        editor.apply();
    }

    public static void  putRentalArea(Activity activity,String value){
        sharedPreferences = activity.getSharedPreferences("RentalAreaPref",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RENTAL_AREA,value);
        editor.apply();
    }

    public static String getRentalArea(String key,Activity activity){
        sharedPreferences = activity.getSharedPreferences("RentalAreaPref",0);
        return sharedPreferences.getString(key,"Shukrabad");
    }

    public static String getRentalType(String key,Activity activity){
        sharedPreferences = activity.getSharedPreferences("RentalTypePref",0);
        return sharedPreferences.getString(key,"Family");
    }




    //userName downloadUrl email phoneNumber userId
}
