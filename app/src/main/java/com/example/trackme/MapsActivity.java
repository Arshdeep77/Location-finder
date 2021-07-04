package com.example.trackme;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.trackme.Fragments.Contacts;
import com.example.trackme.model.LiveLocation;
import com.example.trackme.model.Select;
import com.example.trackme.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trackme.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOACATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUEST_CODE = 123;
    private static final String CONTACTS = "contacts";
    private GoogleMap mMap;
    private Button exitBut;
    private ActivityMapsBinding binding;
    private LocationCallback locationCallback;
    private FirebaseAuth AuthObj;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean mLocationPermesion = false;
    private FloatingActionButton fab;
    private int TransCount = 0;
    Polyline polyline;
    LocationManager locationManager;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder builder;
    private LocationSettingsRequest.Builder build;
    private SettingsClient client;
    private Task<LocationSettingsResponse> task;
    private MarkerOptions marker1;
    private FragmentManager fragManager;
    private Contacts con;
    private FrameLayout frame;
    private DatabaseReference ref;
    private LatLng myLatLng=null;
    private LatLng friendLatLng=null;


    //some hardware commands control programaticaly

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    @Override
    protected void onResume() {
        super.onResume();


        LoginCheck();

    }

    @Override
    public void onBackPressed() {


        if (fragManager != null && TransCount > 0) {
            FragmentTransaction back = fragManager.beginTransaction();


            back.remove(con);
            back.commit();

            TransCount = 0;
        } else {
            super.onBackPressed();
        }
    }

    private void gpsCheck() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AuthObj = FirebaseAuth.getInstance();
        fab = findViewById(R.id.fab);
        exitBut=findViewById(R.id.exitBut);
        //check login

        //fab button
        setClickOnFab();
        setExit();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location loco = locationResult.getLastLocation();
                    myLatLng=new LatLng(loco.getLatitude(), loco.getLongitude());
                    setMarkers();



                    ref = FirebaseDatabase.getInstance().getReference("/SendList/" + FirebaseAuth.getInstance().getUid());

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            GenericTypeIndicator<String> indicator = new GenericTypeIndicator<String>() {

                            };

                            Iterable<DataSnapshot> it = snapshot.getChildren();

                            for (DataSnapshot i : it) {
                                String value = i.getValue(indicator);
                                DatabaseReference ref2 = FirebaseDatabase.getInstance()
                                        .getReference("/reciever_of/" + value);
                                LiveLocation myLoc = new LiveLocation(loco.getLatitude(),loco.getLongitude());
                                ref2.setValue(myLoc);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });

                }
                return;
            }
        };




    }

    private void setExit() {

        friendLatLng=null;
       setMarkers();
        exitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeReciever();
            }
        });
    }

    private void fetchAnotherUserLoc() {
        String number=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("/reciever_of/"+number);





       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot snapshot) {



              if(snapshot!=null){
                  GenericTypeIndicator<LiveLocation> ind=new GenericTypeIndicator<LiveLocation>() {};
                  LiveLocation value=snapshot.getValue(ind);
                   if(value!=null)
                  friendLatLng=new LatLng(value.getLat(),value.getLon());

              }

setMarkers();
           }

           @Override
           public void onCancelled( DatabaseError error) {

           }
       });


    }

    private void setMarkers() {
if(mMap!=null){
    mMap.clear();
}
        if(friendLatLng!=null){

            mark(friendLatLng,10,BitmapDescriptorFactory.HUE_GREEN);
        }
        if(myLatLng!=null){
            mark(myLatLng,10);
        }

    }

    private void setClickOnFab() {


        fragManager = getSupportFragmentManager();
        frame = (FrameLayout) findViewById(R.id.frame);


        fab.setOnClickListener(view -> {
            if (TransCount == 0) {

                FragmentTransaction tr1 = fragManager.beginTransaction();
                con = new Contacts();
                tr1.add(R.id.frame, con);
                tr1.commit();

                TransCount = 1;
            }


        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

     fetchAnotherUserLoc();


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }

    private void LoginCheck() {


        if (AuthObj.getCurrentUser() == null) {
            Intent exit = new Intent(this, PhoneNumber.class);


            finish();
            startActivity(exit);


        } else {
            Intent get = getIntent();
            String sig = get.getStringExtra(Otp_verification.SIGNAL);
            if (sig != null) {

            }


            gpsCheck();
            getPermission();
            createLocationRequest();
            startLocationUpdates();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermesion = false;
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "nooo", Toast.LENGTH_LONG).show();
                    getPermission();
                    return;
                }
            }
            mLocationPermesion = true;
        }
        Toast.makeText(getApplicationContext(), mLocationPermesion + "", Toast.LENGTH_LONG).show();
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(7000);
        locationRequest.setFastestInterval(5000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        build = new LocationSettingsRequest.Builder();

// ...

        client = LocationServices.getSettingsClient(this);
        task = client.checkLocationSettings(build.build());

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            getPermission();
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }


    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

void clerMark(){
        if(mMap!=null)
        mMap.clear();
}
    void mark(LatLng latLng, int i,float col) {


        marker1 = new MarkerOptions().position(latLng).title(i + "city")
       .icon(BitmapDescriptorFactory.defaultMarker(col));
        mMap.addMarker(marker1);



    }

    void mark(LatLng latLng, int i) {


        marker1 = new MarkerOptions().position(latLng).title(i + "city");
        mMap.addMarker(marker1);



    }

    void move(LatLng latLng, float zoom) {

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        if (mMap != null) {

            mMap.animateCamera(cameraUpdate, 5000, null);
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    getPermission();

                }
                mMap.setMyLocationEnabled(true);

                mMap.addCircle(new CircleOptions().radius(26).fillColor(8));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

public static void removeReciever(){


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("/ricever_of/"+FirebaseAuth.getInstance()
        .getCurrentUser().getPhoneNumber());

        DatabaseReference refList=FirebaseDatabase.getInstance().getReference("/SendList/"+FirebaseAuth.getInstance().getUid());

        refList.removeValue();

        ref.removeValue();

}



    private void getPermission() {
        if ((ContextCompat.checkSelfPermission(this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, COARSE_LOACATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{FINE_LOCATION, COARSE_LOACATION, COARSE_LOACATION, Manifest.permission.ACCESS_WIFI_STATE},
                    LOCATION_REQUEST_CODE);

            startLocationUpdates();

        } else {
            mLocationPermesion = true;

            startLocationUpdates();
        }

    }

}

