package com.example.loginpus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class gpsdemo extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int PERMISSION_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Button btnshowmap, btnsetbase;

    Location currentLocation;

    //Switch sw_locationupdates, sw_gps;

    LocationRequest locationRequest;

    FusedLocationProviderClient fusedLocationProviderClient;

    GeoFire geoFire;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsdemo);

        // give UI label a value
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
       // tv_sensor = findViewById(R.id.tv_sensor);
       // tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        btnshowmap = findViewById(R.id.btnshowmap);
        btnsetbase = findViewById(R.id.btnsetbase);
      //  sw_locationupdates = findViewById(R.id.sw_locationsupdates);
       // sw_gps = findViewById(R.id.sw_gps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(gpsdemo.this);


        //set properties of location

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //sw_gps.setOnClickListener(new View.OnClickListener() {
           // @Override
          //  public void onClick(View v) {
           //     if (sw_gps.isChecked()) {
                    //most accurate
            //        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
           //         tv_sensor.setText("Using GPS sensors");
           //     } else {
          //          locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            //        tv_sensor.setText("Using Towers + WiFi");


          //      }
          //  }
      //  });
        DatabaseReference currentReference = FirebaseDatabase.getInstance().getReference("Current Location");
        GeoFire geoFire = new GeoFire(currentReference);
        updateGPS();

        btnshowmap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent Toupdate = new Intent(gpsdemo.this, MapsActivity.class);
                startActivity(Toupdate);

            }
    });
        btnsetbase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get locatio + add to list
                LocationHelper helper = new LocationHelper(
                        currentLocation.getLongitude(),
                        currentLocation.getLatitude()
                );
                FirebaseDatabase.getInstance().getReference("Base Location")
                        .setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(gpsdemo.this, " Base Location Saved", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(gpsdemo.this, "Base Location Not Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }//end onCreate

    //tell program trigger method after permission have been granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS() {
        //get permission from user to use GPS
        //get current location from fused client
        //update text view

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permission given by user
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //got permission
                    updateUIValues(location);
                    currentLocation = location;

                    LocationHelper helper = new LocationHelper(
                            location.getLongitude(),
                            location.getLatitude()
                    );


                    FirebaseDatabase.getInstance().getReference("Current Location")
                            .setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(gpsdemo.this, "Location Saved", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(gpsdemo.this, "Location Not Saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //geoFire.setLocation("current-location", new GeoLocation(location.getLatitude(), location.getLongitude()));

                }
            });
        } else {
            //permission denied

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }


    }

    private void updateUIValues(Location location) {

        //update text view
        if (location != null){
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("Altitude Not Available");
        }

        if (location.hasAltitude()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tv_speed.setText("Speed Not Available");
        }

        Geocoder geocoder = new Geocoder(gpsdemo.this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e) {
            tv_address.setText("Unable to get street address");
        }

        }}
}