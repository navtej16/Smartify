package com.example.smartify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static SensorManager sensorManager;
    static Sensor accelerometerSensor;
    static Sensor proximitySensor;
    static boolean accelerometerPresent;
    public static NotificationManager mNotificationManager;



    public void Flip(View view){
        Intent flipIntent=new Intent(MainActivity.this,Flip.class);
        startActivity(flipIntent);
    }
    public void location(View view){
        Intent locationIntent=new Intent(MainActivity.this,MapsActivity.class);
        startActivity(locationIntent);
    }
    public void Earphone(View view){
        Intent earphoneIntent= new Intent(MainActivity.this,earphone.class);
        startActivity(earphoneIntent);
    }
    public void Rotate(View view) {
        Intent autoIntent = new Intent(MainActivity.this, autoRotate.class);
        startActivity(autoIntent);
    }
    private int i=0;
    private ImageView imageView;
    private ImageView imageDesmartify;
    Toast toastObject;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==100){
                i=0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView= findViewById(R.id.imageView);
        imageDesmartify= findViewById(R.id.imageViewDesmartify);
        imageDesmartify.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                imageDesmartify.animate().alpha(0).setDuration(500);
                imageView.animate().alpha(1).setDuration(500);
                Toast.makeText(MainActivity.this, "Smartifyed :)", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(MainActivity.this,ExampleService.class);
                startService(serviceIntent);
                return true;
            }
        });
        imageDesmartify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i==0){
                    ++i;
                    handler.sendEmptyMessageDelayed(100,4000); // 3000 equal 3sec , you can set your own limit of secs
                }else if(i==6){
                    toastObject.cancel();
                    toastObject =Toast.makeText(MainActivity.this, "Desmartifyed!" , Toast.LENGTH_SHORT);
                    toastObject.show();
                    i=0;
                    handler.removeMessages(100);
                    imageView.animate().rotationYBy(360).alpha(0).setDuration(500);
                    imageDesmartify.animate().rotationYBy(360).alpha(1).setDuration(500);
                    Intent serviceIntent = new Intent(MainActivity.this,ExampleService.class);
                    stopService(serviceIntent);
                }else if (i==3) {
                    ++i;
                    toastObject =Toast.makeText(MainActivity.this, "three steps away from desmartifying" , Toast.LENGTH_SHORT);
                    toastObject.show();
                }else if (i==4) {
                    ++i;
                    toastObject.cancel();
                    toastObject =Toast.makeText(MainActivity.this, "two steps away from desmartifying" , Toast.LENGTH_SHORT);
                    toastObject.show();
                }else if (i==5) {
                    ++i;
                    toastObject.cancel();
                    toastObject =Toast.makeText(MainActivity.this, "one steps away from desmartifying" , Toast.LENGTH_SHORT);
                    toastObject.show();
                }
                else{
                    i++;
                }
            }
        });
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (isFirstRun) {
            //show start activity
            startActivity(new Intent(MainActivity.this, OnBoarding.class));
            Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
                    .show();
        }


        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensorList.size() > 0){
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
        }
        else{
            accelerometerPresent = false;
            Toast.makeText(this, "Oops! No accelerometer present", Toast.LENGTH_SHORT).show();
        }
        Intent serviceIntent = new Intent(this , ExampleService.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        startForegroundService(serviceIntent);
        else
        {
            startService(serviceIntent);
        }
    }

    public void startService(View view){


    }
    public  void stopService(View view){
        Intent serviceIntent = new Intent(this , ExampleService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onStart() {
        /*sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accelerometerListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);*/
        Log.i("info","started");
        super.onStart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.i("info","resume");
        super.onResume();
        if(accelerometerPresent){
            sensorManager.registerListener(ExampleService.accelerometerListener, accelerometerSensor,2000,ExampleService.serviceHandler);
            sensorManager.registerListener(ExampleService.accelerometerListener, proximitySensor, 2000,ExampleService.serviceHandler);
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.i("info","stop");
        if(accelerometerPresent){
           // sensorManager.unregisterListener(accelerometerListener);


        }
    }

    @Override
    protected void onDestroy() {
        Log.i("info","destroy");
        super.onDestroy();
    }
}

