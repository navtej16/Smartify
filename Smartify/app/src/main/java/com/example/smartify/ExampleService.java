package com.example.smartify;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.LongDef;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import static com.example.smartify.MainActivity.accelerometerSensor;
import static com.example.smartify.MainActivity.mNotificationManager;
import static com.example.smartify.MainActivity.proximitySensor;
import static com.example.smartify.MainActivity.sensorManager;
import static com.example.smartify.MapsActivity.current_Id;
import static com.example.smartify.MapsActivity.mMap;


public class ExampleService extends Service {
    public static ArrayList<Integer> dndList = new ArrayList<Integer>();
    public static ArrayList<Double> latitudeList = new ArrayList<Double>();
    public static ArrayList<Double> longitudeList = new ArrayList<Double>();
    public static ArrayList<Integer> wifiList = new ArrayList<Integer>();
    public static ArrayList<Integer> radiusList = new ArrayList<Integer>();
    public static String lastAppString = null;
    WindowManager wm;
    LinearLayout ll;
    private View overlay;

    float z = -20;
    float pValue;
    boolean proximity = true;
    boolean accelerometer = false;
    private Looper serviceLooper;
    static public ServiceHandler serviceHandler;
    static boolean flip;
    private static Timer timer = new Timer();
    int fFlag = 0;
    static int flipSettings = 2;
    int innerflag = 0;
    WifiManager wifiManager;
    static SensorEventListener accelerometerListener;
    public static LocationManager locationManager;
    public static LocationListener locationListener;
    int dndFlag = 0;
    int wifiFlag = 0;
    public static Location mLastLocation;
    public static Marker mCurrLocationMarker;
    WindowManager.LayoutParams orientationLayout;
    int rotateFlag = 0;
    String TAG = "debugging";
    String currentApp = "";
    int count = 0;

    class HeadsetIntentReceiver extends BroadcastReceiver {
        private String TAG = "HeadSet";

        public HeadsetIntentReceiver() {
            Log.d(TAG, "Created");
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case (0):
                        Log.d(TAG, "Headset unplugged");
                        break;
                    case (1):
                        Log.d(TAG, "Headset plugged");
                        //  Intent intent1 = new Intent(context,MainActivity2.class);
                        if (lastAppString != null) {
                            Intent intent1 = getPackageManager().getLaunchIntentForPackage(lastAppString);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (intent1 != null) {
                                ExampleService.this.startActivity(intent1);//null pointer check in case package name was not found
                            }
                        }

                        // context.startActivity(intent1);
                        break;
                    default:
                        Log.d(TAG, "Error");
                }
            }
        }


    }

    public static boolean isInside(LatLng origin, LatLng point, double radius) {
        Location Lorigin = new Location("");
        Lorigin.setLongitude(origin.longitude);
        Lorigin.setLatitude(origin.latitude);
        Location Lpoint = new Location("");
        Lpoint.setLongitude(point.longitude);
        Lpoint.setLatitude(point.latitude);
        if (Lorigin.distanceTo(Lpoint) < radius)

            return true;
        else
            return false;

    }


    // Use like this:


    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            Log.i("flipStatus", String.valueOf(flip));

            accelerometerListener = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent event) {
                    //   Log.i("info","sensorchanged");
                    if (flip) {
                        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                            if (event.values[2] <= -9.6) {
                                accelerometer = true;
                            } else {
                                accelerometer = false;
                            }
                            //   Log.v("z value", String.valueOf(event.values[2]));

                        }
                        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                            if (event.values[0] == 0.0) {
                                proximity = true;
                            } else {
                                proximity = false;
                            }

                            //  Log.i("pValue", String.valueOf(event.values[0]));
                        }


                        if (!accelerometer && !proximity) {
                            if (fFlag == 1) {
                                fFlag = 0;
                                Log.i(TAG, "onSensorChanged:face up");
                                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                            }

                        } else if (accelerometer && proximity) {
                            if (fFlag == 0) {
                                fFlag = 1;
                                Log.i(TAG, "onSensorChanged:face down");

                                new CountDownTimer(2000, 100) {

                                    public void onTick(long millisUntilFinished) {

                                        innerflag = 0;
                                        Log.d("time", String.valueOf(millisUntilFinished));
                                        if (!accelerometer && !proximity) {
                                            innerflag = 1;
                                            fFlag = 0;

                                        }

                                        //here you can have your logic to set text to edittext
                                    }

                                    @SuppressLint("WrongConstant")
                                    public void onFinish() {
                                        //   Log.i("finish","true");
                                        if (innerflag == 0) {
                                            mNotificationManager.setInterruptionFilter(flipSettings);
                                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            // Vibrate for 500 milliseconds
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                v.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
                                            } else {
                                                //deprecated in API 26
                                                v.vibrate(400);
                                            }
                                            fFlag = 1;
                                        }
                                    }

                                }.start();

                            }
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job

        }
    }


    @Override
    public void onCreate() {
/*
        ImageView openapp = new ImageView(this);
        openapp.setImageResource(R.mipmap.ic_launcher_round);
        ViewGroup.LayoutParams butnparams = new ViewGroup.LayoutParams(
                50,50);
        openapp.setLayoutParams(butnparams);

        wm = (WindowManager) getSystemService(Service.WINDOW_SERVICE);

        LinearLayout orientationChanger = new LinearLayout(getApplicationContext());
        orientationChanger.setClickable(false);
        orientationChanger.setFocusable(false);
        orientationChanger.setFocusableInTouchMode(false);
        orientationChanger.setLongClickable(false);

        orientationLayout = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888);

        wm.addView(orientationChanger, orientationLayout);
        orientationChanger.setVisibility(View.GONE);

        orientationLayout.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
        wm.updateViewLayout(orientationChanger, orientationLayout);
        orientationChanger.setVisibility(View.VISIBLE);
*/



        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        HeadsetIntentReceiver receiver = new HeadsetIntentReceiver();
        registerReceiver(receiver, receiverFilter);
        Log.i("info", "Service Started");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Your Location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mCurrLocationMarker = mMap.addMarker(markerOptions);
                for (int i = 0; i < dndList.size(); i++) {
                    if (ExampleService.isInside(new LatLng(latitudeList.get(i), longitudeList.get(i)),
                            new LatLng(location.getLatitude(), location.getLongitude()), radiusList.get(i))) {
                        if (dndFlag == 0 && dndList.get(i) == 1) {
                            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                            dndFlag = 1;
                        }
                        if (wifiFlag == 0 && wifiList.get(i) == 1) {
                            wifiManager.setWifiEnabled(true);
                            wifiFlag = 1;
                        }

                    } else {
                        if (dndFlag == 1) {
                            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                            dndFlag = 0;
                        }
                        if (wifiFlag == 1 && wifiList.get(i) == 1) {
                            wifiManager.setWifiEnabled(false);
                            wifiFlag = 0;
                        }
                    }
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        HandlerThread thread = new HandlerThread("ServiceStartArguments", 16);
        Log.i("info", "registered");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();

        else
            startForeground(1, new Notification());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        super.onCreate();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_brightness_3_black)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        if (latitudeList.size() > 0)
            Log.i("abcd", String.valueOf(latitudeList.get(latitudeList.size() - 1)));

        serviceHandler.sendMessage(msg);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(accelerometerListener);
        super.onDestroy();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

