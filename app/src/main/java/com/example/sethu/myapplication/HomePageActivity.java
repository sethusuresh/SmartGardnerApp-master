package com.example.sethu.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class HomePageActivity extends AppCompatActivity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userRef = rootRef.child(user.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton dailyRadioButton = (RadioButton) radioGroup.getChildAt(0);
        ImageView plantWateringGif = findViewById(R.id.PLANT_WATERING_GIF);
        plantWateringGif.setVisibility(View.INVISIBLE);
        dailyRadioButton.setChecked(true);
        loadUserProfile();
    }

    private void showGif() {
        TextView selectedBasicConfig = findViewById (R.id.selected_config);
        selectedBasicConfig.setVisibility(View.INVISIBLE);
        ImageView plantWateringGif = findViewById(R.id.PLANT_WATERING_GIF);
        plantWateringGif.setVisibility(View.VISIBLE);
        Glide.with(this).asGif().load(R.drawable.plant_watering).into(plantWateringGif);
    }

    @Override
    public void onStart () {
        super.onStart();
        // Read from the database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                if(value.get("waterNow").equals("true")){
                   showGif();
                }
                else{
                    ImageView plantWateringGif = findViewById(R.id.PLANT_WATERING_GIF);
                    plantWateringGif.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("MainActivity", "Failed to read value.", error.toException());
            }
        });
    }

    private void loadUserProfile() {
        if(user != null){
            if(user.getPhotoUrl() != null)
                Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into((ImageView)findViewById(R.id.PROFILE_IMAGE));
        }
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    public void onProfilePicClick (View view){
        signOut();
    }

    private void saveConfigInDB (String morningTime, String eveningTime, String daysOfWeek) {
        userRef.child("morningTime").setValue(morningTime);
        userRef.child("eveningTime").setValue(eveningTime);
        userRef.child("daysOfWeek").setValue(daysOfWeek);
    }

    public void saveConfig(View view) {
        String morningTime = null;
        String eveningTime = null;
        String daysOfWeek = null;
        boolean canProceed;
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int selectedRadioId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedRadioId);
        int position = radioGroup.indexOfChild(radioButton);
        if(position == 0){
            //Daily
            SharedPreferences sharedPref = getSharedPreferences("SmartGardnerData", Context.MODE_PRIVATE);
            morningTime = sharedPref.getString(getString(R.string.basic_fet_morning_time), "01:00:00");
            eveningTime = sharedPref.getString(getString(R.string.basic_fet_evening_time), "01:00:00");
            daysOfWeek = getString(R.string.cust_fet_days);
            canProceed = true;
        }
        else if(position == 1){
            //Cust
            SharedPreferences sharedPref = getSharedPreferences("SmartGardnerData",Context.MODE_PRIVATE);
            daysOfWeek = sharedPref.getString(getString(R.string.cust_fet_days_of_week), getString(R.string.cust_fet_days));
            morningTime = sharedPref.getString(getString(R.string.cust_fet_morning_time), "01:00:00");
            eveningTime = sharedPref.getString(getString(R.string.cust_fet_evening_time), "01:00:00");
            canProceed = true;
        }
        else if(position == 2){
            //Adv
            TextView selectedBasicConfig = findViewById (R.id.selected_config);
            selectedBasicConfig.setText("");
            Toast.makeText(this, "Coming soon!!!", Toast.LENGTH_SHORT).show();
            canProceed = false;
        }
        else{
            canProceed = false;
            Toast.makeText(this, "Please select an option!!!", Toast.LENGTH_SHORT).show();
        }

        if(canProceed){
            //show user selection
            ImageView plantWateringGif = findViewById(R.id.PLANT_WATERING_GIF);
            plantWateringGif.setVisibility(View.INVISIBLE);
            TextView selectedBasicConfig = findViewById (R.id.selected_config);
            selectedBasicConfig.setVisibility(View.VISIBLE);
            selectedBasicConfig.setText(daysOfWeek+"\nMORNING: "+ morningTime + " am\n" + "EVENING: " + eveningTime +" pm");
            //saving config to firebase database
            saveConfigInDB(morningTime, eveningTime, daysOfWeek);
        }
    }

    public void dailyConfig(View view){
        Intent intent = new Intent(HomePageActivity.this, BasicFetActivity.class);
        startActivity(intent);
    }

    public void advancedConfig(View view){
        Toast.makeText(this, "Coming soon!!!", Toast.LENGTH_SHORT).show();
    }

    public void customConfig(View view){
        Intent intent = new Intent(HomePageActivity.this, CustomFetActivity.class);
        startActivity(intent);
    }

    public void instWaterOption(View view) {
        //saving config to firebase database
        userRef.child("waterNow").setValue("true");
    }
}
