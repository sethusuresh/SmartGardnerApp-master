package com.example.sethu.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sethu.myapplication.DTO.BasicConfigDTO;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Util.TimeUtil;

public class BasicFetActivity extends Activity{


    BasicConfigDTO basicConfigDTO = new BasicConfigDTO();

    TimeUtil timeUtil = new TimeUtil();
    boolean waterMorning = true;
    boolean waterEvening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_fet);
        List<String> hourList = timeUtil.getHourList();
        List<String> minuteList = timeUtil.getMinuteList();
        //get existing config
        Map<String,Integer> userSelectedTime = getUserSelectedTime();

        //setting morning hr drop-down
        Spinner morningHourSpinner = findViewById(R.id.basic_morning_hour_spinner);
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hourList);
        morningHourSpinner.setAdapter(hourAdapter);
        morningHourSpinner.setSelection(userSelectedTime.get(String.valueOf(R.string.basic_fet_mor_hr)));

        //setting morning min drop-down
        Spinner morningMinuteSpinner = findViewById(R.id.basic_morning_minute_spinner);
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, minuteList);
        morningMinuteSpinner.setAdapter(minuteAdapter);
        morningMinuteSpinner.setSelection(userSelectedTime.get(String.valueOf(R.string.basic_fet_mor_min)));

        //setting evening hr drop-down
        Spinner eveningHourSpinner = findViewById(R.id.basic_evening_hour_spinner);
        eveningHourSpinner.setAdapter(hourAdapter);
        eveningHourSpinner.setSelection(userSelectedTime.get(String.valueOf(R.string.basic_fet_eve_hr)));

        //setting evening min drop-down
        Spinner eveningMinuteSpinner = findViewById(R.id.basic_evening_minute_spinner);
        eveningMinuteSpinner.setAdapter(minuteAdapter);
        eveningMinuteSpinner.setSelection(userSelectedTime.get(String.valueOf(R.string.basic_fet_eve_min)));

        //setting the check box selection
        CheckBox morningCheckbox = findViewById(R.id.dailyMorCheckbox);
        morningCheckbox.setChecked(true);
        waterMorning = true;
        if(userSelectedTime.get(String.valueOf(R.string.basic_fet_mor_hr)) == -1){
            morningCheckbox.setChecked(false);
            waterMorning = false;
        }
        CheckBox eveningCheckbox = findViewById(R.id.dailyEveCheckbox);
        eveningCheckbox.setChecked(true);
        waterEvening = true;
        if(userSelectedTime.get(String.valueOf(R.string.basic_fet_eve_hr)) == -1){
            eveningCheckbox.setChecked(false);
            waterEvening = false;
        }
    }

    private Map<String,Integer> getUserSelectedTime() {
        //fetching existing config
        SharedPreferences sharedPref = getSharedPreferences("SmartGardnerData",Context.MODE_PRIVATE);
        String morningTime = sharedPref.getString(getString(R.string.basic_fet_morning_time), "01:00:00");
        String eveningTime = sharedPref.getString(getString(R.string.basic_fet_evening_time), "01:00:00");
        int morningHrPos = timeUtil.getHourList().indexOf(morningTime.split(":")[0].replaceFirst("^0+(?!$)", ""));
        int morningMinPos = timeUtil.getMinuteList().indexOf(morningTime.split(":")[1].replaceFirst("^0+(?!$)", ""));
        int eveningHrPos = timeUtil.getHourList().indexOf(eveningTime.split(":")[0].replaceFirst("^0+(?!$)", ""));
        int eveningMinPos = timeUtil.getMinuteList().indexOf(eveningTime.split(":")[1].replaceFirst("^0+(?!$)", ""));
        Map<String,Integer> timePosMap= new HashMap<>();
        timePosMap.put(String.valueOf(R.string.basic_fet_mor_hr), morningHrPos);
        timePosMap.put(String.valueOf(R.string.basic_fet_mor_min), morningMinPos);
        timePosMap.put(String.valueOf(R.string.basic_fet_eve_hr), eveningHrPos);
        timePosMap.put(String.valueOf(R.string.basic_fet_eve_min), eveningMinPos);

        //show user selection
        TextView selectedBasicConfig = findViewById (R.id.selected_basic_config);
        String userSelection = "";
        if(morningHrPos != -1){
            userSelection = userSelection + "MORNING: "+ morningTime + " am\n" ;
        }
        if(eveningHrPos != -1){
            userSelection = userSelection + "EVENING: " + eveningTime +" pm";
        }
        selectedBasicConfig.setText(userSelection);
        return timePosMap;
    }

    public void saveBasicConfig(View view) throws ParseException {
        String userConfig = "";
        Spinner morningHourSpinner = findViewById(R.id.basic_morning_hour_spinner);
        Spinner morningMinuteSpinner = findViewById(R.id.basic_morning_minute_spinner);
        Spinner eveningHourSpinner = findViewById(R.id.basic_evening_hour_spinner);
        Spinner eveningMinuteSpinner = findViewById(R.id.basic_evening_minute_spinner);
        basicConfigDTO.setMorning_hour(morningHourSpinner.getSelectedItem().toString());
        basicConfigDTO.setMorning_minute(morningMinuteSpinner.getSelectedItem().toString());
        basicConfigDTO.setEvening_hour(eveningHourSpinner.getSelectedItem().toString());
        basicConfigDTO.setEvening_minute(eveningMinuteSpinner.getSelectedItem().toString());
        TextView selectedBasicConfig = findViewById (R.id.selected_basic_config);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm a");
        java.sql.Time morningTime = new java.sql.Time(formatter.parse(basicConfigDTO.getMorning_hour()+":"+basicConfigDTO.getMorning_minute()+" am").getTime());
        if(waterMorning){
            basicConfigDTO.setMorningTime(morningTime);
            userConfig = userConfig + "MORNING: "+ basicConfigDTO.getMorningTime() + " am\n";
        }
        else{
            basicConfigDTO.setMorningTime(new Time(0,0,0));
        }
        java.sql.Time eveningTime = new java.sql.Time(formatter.parse(basicConfigDTO.getEvening_hour()+":"+basicConfigDTO.getEvening_minute()+" pm").getTime());
        if(waterEvening){
            basicConfigDTO.setEveningTime(eveningTime);
            userConfig = userConfig + "EVENING: " + basicConfigDTO.getEveningTime() +" pm";
        }
        else{
            basicConfigDTO.setEveningTime(new Time(0,0,0));
        }
        selectedBasicConfig.setText(userConfig);
        //save to local storage
        SharedPreferences sharedPref = getSharedPreferences("SmartGardnerData",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.basic_fet_morning_time), basicConfigDTO.getMorningTime().toString());
        editor.putString(getString(R.string.basic_fet_evening_time), basicConfigDTO.getEveningTime().toString());
        editor.apply();
        Toast.makeText(this, "new timings saved successfully!!!", Toast.LENGTH_SHORT).show();
    }

    public void basicFetCheckbox(View view){
        CheckBox morningCheckbox = findViewById(R.id.dailyMorCheckbox);
        CheckBox eveningCheckbox = findViewById(R.id.dailyEveCheckbox);
        if(!morningCheckbox.isChecked()){
            waterMorning = false;
        }
        else{
            waterMorning = true;
        }
        if(!eveningCheckbox.isChecked()){
            waterEvening = false;
        }
        else{
            waterEvening = true;
        }
    }
}
