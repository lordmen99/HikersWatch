package com.mikebarone.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation;
    Geocoder geocoder;
    List<Address> listAddresses;
    String GPSInfo = "";
    TextView gpsData;

    public void updateGPSInfo(Location location){
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            if(location != null ){
                listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                if(listAddresses != null && listAddresses.size() > 0) {
                    GPSInfo = "Latitude: " + location.getLatitude() + "\n"
                            + "Longitude: " + location.getLongitude() + "\n"
                            + "Altitude: " + location.getAltitude() + "\n"
                            + "Accuracy: " + location.getAccuracy();
                    if (listAddresses.get(0).getAddressLine(0) != null)
                        GPSInfo += "\n\nAddress:\n" + listAddresses.get(0).getAddressLine(0);
                    if (listAddresses.get(0).getAddressLine(1) != null)
                        GPSInfo += "\n" + listAddresses.get(0).getAddressLine(1);
                    if (listAddresses.get(0).getAddressLine(2) != null)
                        GPSInfo += "\n" + listAddresses.get(0).getAddressLine(2);
                    gpsData.setText(GPSInfo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateGPSInfo(currentLocation);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsData = (TextView)findViewById(R.id.gpsData);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateGPSInfo(location);
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

        if(Build.VERSION.SDK_INT <23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        } else {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateGPSInfo(currentLocation);
            }
        }
    }
}
