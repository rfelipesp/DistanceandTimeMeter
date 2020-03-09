package br.com.rfelipe.distanceandtimemeter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.SystemClock;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_GPS = 1001;
    private Button allowgps;
    private Button activategps;
    private Button disablegps;
    private Button startroute;
    private Button finishroute;
    private TextView travelledDistanceTextView;
    private Chronometer timeSpentChronometer;
    private TextView searchTextView;

    private double latitudeAtual;
    private double longitudeAtual;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView locationTextView;

    ControlTrack controlTrack = new ControlTrack();

    private void ControlButtons(boolean allow, boolean activate, boolean disable, boolean start, boolean finish, boolean search) {

        allowgps.setEnabled(allow);
        activategps.setEnabled(activate);
        disablegps.setEnabled(disable);
        startroute.setEnabled(start);
        finishroute.setEnabled(finish);
        searchTextView.setEnabled(search);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        allowgps = findViewById(R.id.allowgps);
        activategps = findViewById(R.id.activategps);
        disablegps = findViewById(R.id.disablegps);
        startroute = findViewById(R.id.startroute);
        finishroute = findViewById(R.id.finishroute);
        travelledDistanceTextView = findViewById(R.id.travelledDistanceTextView);
        timeSpentChronometer = findViewById(R.id.timeSpentChronometer);
        searchTextView = findViewById(R.id.searchTextView);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!controlTrack.getAtivo())
                    return;

                float distance = controlTrack.UpdatePercurso(location).getDistancia();

                float meters = distance % 1000;
                float kilometers = distance - meters;

                travelledDistanceTextView.setText(Math.round(kilometers) + "km " + Math.round(meters) + "m");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri gmmIntentUri =  Uri.parse(String.format("geo:%f,%f?q=" + searchTextView.getText().toString(), latitudeAtual, longitudeAtual));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }catch (Exception e){

                }
            }
        });

        allowgps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        REQUEST_PERMISSION_GPS
                );

            }
        });

        activategps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission
                        (MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 500,0, locationListener );
                }
                ControlButtons(false, false, true, true, false, true);
            }
        });

        disablegps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager.removeUpdates(locationListener);
                ControlButtons(false, true, false, false, false, false);
            }
        });

        startroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                controlTrack.setAtivo(true);
                timeSpentChronometer.setBase(SystemClock.elapsedRealtime());
                timeSpentChronometer.start();
                ControlButtons(false, false, false, false, true, true);
            }
        });

        finishroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timeSpentChronometer.stop();
                ControlButtons(false, false, true, true, false, true);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_GRANTED){
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            ControlButtons(false, true, false, false, false, false);

        }
        else{
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            ControlButtons(true, false, false, false, false, false);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001){
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                //permissão concedida, ativamos o GPS
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){
                    ControlButtons(false, true, false, false, false,false);
                }
            }
            else{
                Toast.makeText(this, getString(R.string.no_gps_no_app), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

}
