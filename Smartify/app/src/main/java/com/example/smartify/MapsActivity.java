package com.example.smartify;

import  androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.smartify.ExampleService.dndList;
import static com.example.smartify.ExampleService.latitudeList;
import static com.example.smartify.ExampleService.locationListener;
import static com.example.smartify.ExampleService.locationManager;
import static com.example.smartify.ExampleService.longitudeList;
import static com.example.smartify.ExampleService.radiusList;
import static com.example.smartify.ExampleService.wifiList;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener,GoogleMap.OnMapClickListener {

    public static GoogleMap mMap;
    List<Marker> markers = new ArrayList<Marker>();
    Circle circle;
    SeekBar seekBar;
    public FloatingActionButton fabDelete ;
    public FloatingActionButton fabDnd ;
    public FloatingActionButton fabWifi;
    LinearLayout linearLayout;
    public static int current_Id=-1;
    public List<Circle> circles=new ArrayList<>();
    public ArrayList<Integer> flagArray=new ArrayList<>();
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }



    private void createRedCircle(LatLng latLng, float radius,int id) {
        if (flagArray.get(id)==0){
            Circle circle1 = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .strokeColor(getColor(R.color.red))
                    .strokeWidth(5)
                    .fillColor(getColor(R.color.redTransparent)));
            circles.add(circle1);
            flagArray.set(id,1);
        }
    }
    private boolean isIntersecting(LatLng origin1, LatLng origin2, double distance) {
        Location Lorigin = new Location("");
        Lorigin.setLongitude(origin1.longitude);
        Lorigin.setLatitude(origin1.latitude);
        Location Lpoint = new Location("");
        Lpoint.setLongitude(origin2.longitude);
        Lpoint.setLatitude(origin2.latitude);
        if(Lorigin.distanceTo(Lpoint)<distance)
            return true;
        else
            return false;

    }
    public void createCircle(LatLng latLng,float radius){
        circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(getColor(R.color.colorPrimary))
                .strokeWidth(5)
                .fillColor(getColor(R.color.blueTransparent)));
    }
    public void setOnMapLocation(Location location,String s){
        if(location!=null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            ExampleService.mCurrLocationMarker=mMap.addMarker(new MarkerOptions().position(userLocation).title(s));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));
        }
        }
    private void delete(int id) {
        markers.get(id).remove();
        circle.remove();
        markers.remove(id);
        dndList.remove(id);
        radiusList.remove(id);
        wifiList.remove(id);
        latitudeList.remove(id);
        longitudeList.remove(id);
        linearLayout.animate().alpha(0).setDuration(100);

    }
    private void addMarker(int i) {
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(ExampleService.latitudeList.get(i),ExampleService.longitudeList.get(i)))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))));
    }
    private void updateUI () {
        if (current_Id!=-1){
            if(dndList.get(current_Id)==1) fabDnd.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
            else if(dndList.get(current_Id)==0) fabDnd.setImageResource(R.drawable.ic_do_not_disturb_off_black_24dp);
            if(wifiList.get(current_Id)==1) fabWifi.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
            if(wifiList.get(current_Id)==0) fabWifi.setImageResource(R.drawable.ic_signal_wifi_off_black_24dp);
        }
        if(current_Id!=-1) seekBar.setProgress(radiusList.get(current_Id));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.smartify", Context.MODE_PRIVATE);
        seekBar=(SeekBar) findViewById(R.id.seekBar4);
        linearLayout=(LinearLayout) findViewById(R.id.settings);
        //seekBar.setProgress(30);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (circle != null) circle.setRadius(progress);
                for (int id = 0; id < dndList.size(); id++) {
                    flagArray.add(0);
                    if (isIntersecting(new LatLng(latitudeList.get(current_Id), longitudeList.get(current_Id)), new LatLng(latitudeList.get(id), longitudeList.get(id)), progress + radiusList.get(id)) && id != current_Id) {
                        if (circle != null) circle.remove();
                         //   createRedCircle(new LatLng(latitudeList.get(current_Id),longitudeList.get(current_Id)),progress,current_Id);
                        //createRedCircle(new LatLng(latitudeList.get(id), longitudeList.get(id)), radiusList.get(id),id);
                        Toast.makeText(MapsActivity.this, "Intersected", Toast.LENGTH_SHORT).show();
                    }
                }
                int count=circles.size()-1;
                for (int id = 0; id <dndList.size(); id++)
                {
                    Log.i(Integer.toString(id),Integer.toString(flagArray.get(id)));
                    if (!isIntersecting(new LatLng(latitudeList.get(current_Id), longitudeList.get(current_Id)), new LatLng(latitudeList.get(id), longitudeList.get(id)), progress + radiusList.get(id)) && id != current_Id){
                        if (count>0){
                            circles.get(count).remove();
                            count--;
                            flagArray.set(id,0);
                            Log.i(Integer.toString(id),Integer.toString(flagArray.get(id)));
                            Log.i("circles size",Integer.toString(circles.size()));
                            Log.i("count",Integer.toString(count));

                        }
                    }
                }
                if (circles.size()>0) circles.get(0).setRadius(progress);
               /* for (int id = 0; id < dndList.size(); id++) {
                    if (!isIntersecting(new LatLng(latitudeList.get(current_Id), longitudeList.get(current_Id)), new LatLng(latitudeList.get(id), longitudeList.get(id)), progress + radiusList.get(id)) && id != current_Id)
                       if(redCheck==1){ Toast.makeText(MapsActivity.this, "Nicee", Toast.LENGTH_SHORT).show();
                    circle1.remove();
                    createCircle(new LatLng(latitudeList.get(current_Id), longitudeList.get(current_Id)),progress);
                       redCheck=0;

                       }
                }*/
                if(circle!=null)circle.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                radiusList.set(current_Id,seekBar.getProgress());
                Log.i("current id",Integer.toString(current_Id));
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fabDelete = findViewById(R.id.fabOpen);
        fabDelete.setImageResource(R.drawable.ic_delete_forever_black_24dp);
        fabDelete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorWhite)));
        fabDnd = findViewById(R.id.fabDnd);
        fabDnd.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
        fabDnd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorWhite)));
        fabWifi = findViewById(R.id.fabWifi);
        fabWifi.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
        fabWifi.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorWhite)));
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Delete Location")
                        .setMessage("Are you sure you want to delete this location?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                delete(current_Id);
                                current_Id=-1;
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
        fabWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiList.get(current_Id)==0)
                {   fabWifi.setAlpha(1f);
                    wifiList.set(current_Id,1);
                    fabWifi.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
                    Toast.makeText(MapsActivity.this, "WIFI will be enabled at this Location", Toast.LENGTH_SHORT).show();
                }
                else if(wifiList.get(current_Id)==1){
                    wifiList.set(current_Id,0);
                    fabWifi.setImageResource(R.drawable.ic_signal_wifi_off_black_24dp);
                    fabWifi.setAlpha(0.7f);
                    Toast.makeText(MapsActivity.this, "WIFI will be unaffected for this Location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fabDnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dndList.get(current_Id)==0){
                    fabDnd.setAlpha(1f);
                    dndList.set(current_Id,1);
                    fabDnd.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
                    Toast.makeText(MapsActivity.this, "DND will be enabled at this Location", Toast.LENGTH_SHORT).show();
                }
                else if(dndList.get(current_Id)==1){
                    dndList.set(current_Id,0);
                    fabDnd.setImageResource(R.drawable.ic_do_not_disturb_off_black_24dp);
                    Toast.makeText(MapsActivity.this, "DND will be unaffected for this Location", Toast.LENGTH_SHORT).show();
                    fabDnd.setAlpha(0.7f);
                }
            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        for (int i=0 ; i<ExampleService.latitudeList.size();i++){
            addMarker(i);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,locationListener);
            Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            setOnMapLocation(lastKnownLocation,"Your Location");
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        Toast.makeText(this, "Long press on the Map to add location!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        ExampleService.latitudeList.add(latLng.latitude);
        ExampleService.longitudeList.add(latLng.longitude);
        dndList.add(1);
        ExampleService.wifiList.add(1);
        ExampleService.radiusList.add(30);
        if (circle!=null){circle.remove();}
        int check=0;
        for (int i = 0; i <dndList.size()-1 ; i++) {
            if (isIntersecting(latLng, new LatLng(latitudeList.get(i), longitudeList.get(i)), 60)) {
               // createRedCircle( new LatLng(latitudeList.get(i), longitudeList.get(i)),radiusList.get(i));
                //createRedCircle(new LatLng(latitudeList.get(dndList.size()-1),longitudeList.get(dndList.size()-1)),30);
                Toast.makeText(this, "Intersecting", Toast.LENGTH_SHORT).show();
                check=1;
            }
        }
        if (check==0)  createCircle(latLng,30);
        addMarker(markers.size());
        Log.i("as",Integer.toString(dndList.get(dndList.size()-1)));
        Log.i("asd",Integer.toString(markers.size()));
        Log.i("asdf",Integer.toString(radiusList.size()));

            current_Id = latitudeList.size() - 1;
            updateUI();
            linearLayout.animate().alpha(1).setDuration(100);
            // Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();
        /*try{
            sharedPreferences.edit().putString("dndList",ObjectSerializer.serialize(dndList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(dndList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            sharedPreferences.edit().putString("wifiList",ObjectSerializer.serialize(wifiList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(wifiList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            sharedPreferences.edit().putString("latitudeList",ObjectSerializer.serialize(latitudeList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(dndList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            sharedPreferences.edit().putString("longitudeList",ObjectSerializer.serialize(longitudeList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(dndList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            sharedPreferences.edit().putString("radiusList",ObjectSerializer.serialize(radiusList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(dndList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }*/
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng pos=marker.getPosition();
        current_Id = -1;
        if(circle!=null)
        {circle.remove();}
        linearLayout.animate().alpha(0).setDuration(1);


        for (int i=0;i<markers.size();i++){
            if(latitudeList.get(i)==pos.latitude){
                current_Id=i;
                Log.i("current id", Integer.toString(i));
            }
        }
       /*Log.i("dnd", String.valueOf(dndList.get(current_Id)));
       Log.i("wifi", String.valueOf(wifiList.get(current_Id)));*/


        if(current_Id!=-1) {
            createCircle(marker.getPosition(), radiusList.get(current_Id));
            //Log.i("pos", "String.valueOf(pos.latitude)");
            linearLayout.animate().alpha(1).setDuration(100);
            updateUI();
        }


        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

            Log.i("msg","mapClicked");
            linearLayout.animate().alpha(0).setDuration(100);


    }
}
