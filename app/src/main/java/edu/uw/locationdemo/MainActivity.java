package edu.uw.locationdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LOCATION";

    private GoogleApiClient gac; // Responsible for communicating with google api service

    private TextView textLat;
    private TextView textLng;
    private TextView textAlt;
    private TextView textOth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(gac == null) {
            gac = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)     // Which thing is listener for callbacks
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        textLat = (TextView)findViewById(R.id.txt_lat);
        textLng = (TextView)findViewById(R.id.txt_lng);
        textAlt = (TextView)findViewById(R.id.txt_alt);
        textOth = (TextView)findViewById(R.id.txt_oth);



    }

    @Override
    protected void onStart() {
        gac.connect();

        super.onStart();
    }

    private static final int LOC_REQUEST_CODE = 1;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(new Long(10000)); // Every ten seconds
        request.setFastestInterval(5000); // The fastest we are willing to get new location data.

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //okay send request

            LocationServices.FusedLocationApi.requestLocationUpdates(gac, request, this);

        } else {
            // u fail
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOC_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Gave us permission, send request
                this.onConnected(null);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Called when location changed
        if(location != null) {
            textLat.setText("" + location.getLatitude());
            textLng.setText("" + location.getLongitude());
            textAlt.setText("" + location.getAltitude());
            textOth.setText("" + location.getBearing());
        }
    }
}
