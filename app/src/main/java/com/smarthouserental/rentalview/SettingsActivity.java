package com.smarthouserental.rentalview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.smarthouserental.R;
import com.smarthouserental.pref.SharedPrefHelper;

public class SettingsActivity extends AppCompatActivity {


    private RadioGroup typeRadioGroup,areaRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //----------------tool bar--------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Rental Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        typeRadioGroup = findViewById(R.id.rentalTypeRadioGroup);
        areaRadioGroup = findViewById(R.id.rentalSearchRadioGroup);

        //-------------type radio group-------------
        String type = SharedPrefHelper.getRentalType(SharedPrefHelper.RENTAL_TYPE,SettingsActivity.this);
        if (type.equals("Family")){
            typeRadioGroup.check(typeRadioGroup.getChildAt(0).getId());
        }else {
            typeRadioGroup.check(typeRadioGroup.getChildAt(1).getId());
        }

        typeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int typeSelectedId = typeRadioGroup.getCheckedRadioButtonId();
                RadioButton typeRadioButton = findViewById(typeSelectedId);
                SharedPrefHelper.putRentalType(SettingsActivity.this,typeRadioButton.getText().toString());
            }
        });


        //-------------area radio group-------------
        String area = SharedPrefHelper.getRentalArea(SharedPrefHelper.RENTAL_AREA,SettingsActivity.this);
        if (area.equals("Shukrabad")){
            areaRadioGroup.check(areaRadioGroup.getChildAt(0).getId());
        }else if (area.equals("Shamoli")){
            areaRadioGroup.check(areaRadioGroup.getChildAt(1).getId());
        }else if (area.equals("Firmgate")){
            areaRadioGroup.check(areaRadioGroup.getChildAt(2).getId());
        }else if (area.equals("Mohammadpur")){
            areaRadioGroup.check(areaRadioGroup.getChildAt(3).getId());
        }else {
            areaRadioGroup.check(areaRadioGroup.getChildAt(4).getId());
        }

        areaRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int typeSelectedId = areaRadioGroup.getCheckedRadioButtonId();
                RadioButton areaRadioButton = findViewById(typeSelectedId);
                SharedPrefHelper.putRentalArea(SettingsActivity.this,areaRadioButton.getText().toString());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            gotoRentalPostViewActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        gotoRentalPostViewActivity();
    }

    private void gotoRentalPostViewActivity(){
        startActivity(new Intent(SettingsActivity.this,RentalPostViewActivity.class));
        finish();
    }
}
