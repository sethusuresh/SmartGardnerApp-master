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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sethu.myapplication.DTO.UserDTO;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Enums.Activity;
import Util.Config;
import Util.URLConstants;

public class HomePageActivity extends AppCompatActivity {

    Gson gson = new GsonBuilder().create();
    URLConstants urlConstants = new URLConstants();
    Config config = new Config();
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userRef = rootRef.child(user.getUid());
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        logUserActivity(Activity.LOG_IN.toString());
        ImageView plantWateringGif = findViewById(R.id.PLANT_WATERING_GIF);
        plantWateringGif.setVisibility(View.INVISIBLE);
        mFunctions = FirebaseFunctions.getInstance();
        loadUserProfile();
        getTransactionData();
    }

    private void logUserActivity(String activity) {
        UserDTO userActivity = new UserDTO();
        userActivity.setUserId(user.getUid());
        userActivity.setActivity(activity);
        userActivity.setTargetDevice("None");
        userActivity.setTime(Calendar.getInstance().getTime().toString());
        userActivity.setUserName(user.getEmail());
        String URL = urlConstants.getLogUserActivityURL(config.getHostName());
        try {
            RestInvocation(userActivity, URL, Request.Method.POST);
        } catch (JSONException e) {
            Log.e("logUserActivity", "Error in Request object creation");
            e.printStackTrace();
        }
    }

    private void RestInvocation(UserDTO userActivity, String URL, int requestType) throws JSONException {
        JSONObject requestObject = new JSONObject(gson.toJson(userActivity));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                requestType,
                URL,
                requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("logUserActivity", "Rest call to log user activity completed with status:- " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("logUserActivity", "Rest call to log user activity FAILED with status:- 500" + error.getMessage());
                    }
                }
        );
        requestQueue.add(request);
    }

    private void showSelectedConfig() {
        SharedPreferences sharedPref = getSharedPreferences("SmartGardnerData",Context.MODE_PRIVATE);
        String daysOfWeek = sharedPref.getString(getString(R.string.cust_fet_days_of_week), getString(R.string.cust_fet_days));
        String morningTime = sharedPref.getString(getString(R.string.cust_fet_morning_time), "01:00:00");
        String eveningTime = sharedPref.getString(getString(R.string.cust_fet_evening_time), "01:00:00");
        ImageView plantWateringGif = findViewById(R.id.PLANT_WATERING_GIF);
        plantWateringGif.setVisibility(View.INVISIBLE);
        TextView selectedBasicConfig = findViewById (R.id.selected_config);
        selectedBasicConfig.setVisibility(View.VISIBLE);
        selectedBasicConfig.setText(daysOfWeek+"\nMORNING: "+ morningTime + " am\n" + "EVENING: " + eveningTime +" pm");
    }

    /*private void loadGraph(Map graphDataMap) {
        List<Entry> entries = new ArrayList<Entry>();
        for(Object key : graphDataMap.keySet()){
            entries.add(new Entry(key.to, graphDataMap.get(key)));
        //entries.add(new Entry(graphValueMap., data.getValueY()));
        }
    }*/

    private Task<String> getTransactionData() {
        // Create the arguments to the callable function.
        String text = "WEEK";
        Map<String, Object> data = new HashMap<>();
        data.put("filterBy", text);

        return mFunctions
                .getHttpsCallable("getTransactionData")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = task.getResult().getData().toString();
                        return result;
                    }
                });
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
        showSelectedConfig();
        // Read from the database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                if(value != null && value.get("waterNow") != null && value.get("waterNow").equals("true")){
                   showGif();
                }
                else if(value != null && value.get("weekReport") != null){
                    //write graph creation logic here
                    Map weekReport = (HashMap)value.get("weekReport");
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

        //Handle exceptions from Firebase functions
        getTransactionData()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            // [START_EXCLUDE]
                            Log.w("FIREBASE Functions", "getTransactionData:onFailure", e);
                            return;
                            // [END_EXCLUDE]
                        }

                        // [START_EXCLUDE]
                        String result = task.getResult();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void loadUserProfile() {
        if(user != null){
            if(user.getPhotoUrl() != null)
                Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into((ImageView)findViewById(R.id.PROFILE_IMAGE));
            else
                Glide.with(this).load(R.drawable.default_profile_pic).apply(RequestOptions.circleCropTransform()).into((ImageView)findViewById(R.id.PROFILE_IMAGE));
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

    @Override
    public void onBackPressed() {
        Toast.makeText(HomePageActivity.this, "Logged-Out Successfully", Toast.LENGTH_SHORT).show();
        signOut();
        super.onBackPressed();
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
