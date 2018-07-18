package com.example.a16022635.p09gettingmylocations;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {

    boolean started;
    FusedLocationProviderClient client;
    LocationCallback lc;
    String folderLoc;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        client = LocationServices.getFusedLocationProviderClient(this);
        Log.d("Service", "Service created");

        lc = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();

                }
            }
        };

//        Toast.makeText(getApplicationContext(), "Service created", Toast.LENGTH_LONG).show();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(started==false ) {
            started = true;
            LocationRequest lr = LocationRequest.create();
            lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            lr.setInterval(10000);
            lr.setFastestInterval(5000);
            lr.setSmallestDisplacement(100);

            lc = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if(locationResult != null) {
                        Location data = locationResult.getLastLocation();
                        double lat = data.getLatitude();
                        double lng = data.getLongitude();
                        Log.d("Location", "Lat: " + lat + ", Lng: " + lng);

                        try {
                            folderLoc = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09GettingMyLocations";
                            File targetFile = new File(folderLoc, "location.txt");
                            FileWriter writer = new FileWriter(targetFile, true);
                            writer.write(lat + ", " + lng);
                            writer.flush();
                            writer.close();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to write", Toast.LENGTH_LONG);
                            e.printStackTrace();
                        }
                    }
                };
            };
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }

            client.requestLocationUpdates(lr, lc, null);

            Log.d("Service", "Service started");
            Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_LONG);
        } else {
            Log.d("Service", "Service is still running");
            Toast.makeText(getApplicationContext(), "Service is still running", Toast.LENGTH_LONG).show();

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "Service exited");
        Toast.makeText(getApplicationContext(), "Service exited", Toast.LENGTH_LONG).show();
        client.removeLocationUpdates(lc);
        super.onDestroy();
    }
}
