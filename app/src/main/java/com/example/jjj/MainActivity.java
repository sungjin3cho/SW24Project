package com.example.jjj;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.*;
import android.hardware.camera2.params.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity{
	private static final String TAG = MainActivity.class.getSimpleName();
    public List<Recipe> arrayOfRecipe = new ArrayList<>();
    public static RecipeAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSetting();

        mAdapter = new RecipeAdapter(this,arrayOfRecipe);

        ((ListView)findViewById(R.id.lv_main)).setAdapter(mAdapter);

        (findViewById(R.id.start_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                run();
                buttonSetting();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                actionAddRecipe();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionAddRecipe(){

    }

    private void buttonSetting(){
        if(SharedPreference.getInstance(this).getRunningState()){
            ((Button)findViewById(R.id.start_btn)).setText("Stop Service");
        } else {
            ((Button)findViewById(R.id.start_btn)).setText("Start Service");
        }
    }
    public void run()
    {
        SharedPreference pref = SharedPreference.getInstance(this);
        boolean isRun =pref.getRunningState();
        if(isRun) {
            pref.setRunningState(false);
            stopService(new Intent(this, SensorService.class));
        }else {
            pref.setRunningState(true);
            startService(new Intent(this, SensorService.class));
        }
    }
}
