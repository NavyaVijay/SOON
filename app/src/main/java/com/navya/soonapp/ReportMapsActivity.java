package com.navya.soonapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ReportMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    Boolean first=true;
    boolean clicked = true;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private LatLng accidentSpot,driverLatLng;
    Timer timer;
    Handler handler;
    private int radius=0;
    private Boolean driverFound=false;
    private String driverFoundID;
    Marker mDriverMarker,mRiderMarker;
    String status,key,es1,es2,vehNo;
    Context context;
    ArrayList<String> Drivers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_maps);
        Intent intent1=getIntent();
        vehNo=intent1.getStringExtra(ReportFragmentActivity.VEHICLE_NO);

        context=getApplicationContext();
        Drivers=new ArrayList<String>();
        Toast.makeText(ReportMapsActivity.this, "Vehicle No:"+vehNo, Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(final Location location) {
       mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

       if(first==true){
            first=false;
             onclick();
             closestDriver();
             if(vehNo!=null){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query query = rootRef.child("Users").child("Riders").orderByChild("VehicleNo").equalTo(vehNo);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        key = ds.getKey();
                        if (key != null) {
                            DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                            dr.child("Users").child("Riders").child(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        es1 = snapshot.child("EmergencyContact1").getValue().toString();
                                        es2 = snapshot.child("EmergencyContact2").getValue().toString();
                                        String lat=String.valueOf(location.getLatitude());
                                        String lng=String.valueOf(location.getLongitude());
                                        String msg="Help!! "+vehNo+" have met with an accident at"+"\n"+"http://maps.google.com/?q="+lat+","+lng+"\n-via SOON App";
                                        SmsManager smsManager=SmsManager.getDefault();
                                       smsManager.sendTextMessage(es1,null,msg,null,null);
                                        smsManager.sendTextMessage(es2,null,msg,null,null);
                                        Toast.makeText(ReportMapsActivity.this, "User Found, Emergency Contacts informed", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
                } else {
                    Toast.makeText(ReportMapsActivity.this, "No User Found, Cannot inform Emergency Contacts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        };
        query.addListenerForSingleValueEvent(valueEventListener);
    }

       }}
    public void onclick() {
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests");
        GeoFire geofire = new GeoFire(ref);
        geofire.setLocation(userid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        accidentSpot = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mRiderMarker=mMap.addMarker(new MarkerOptions().position(accidentSpot).title("Accident here!!"));
    }

    public void closestDriver(){
        DatabaseReference driverLocation=FirebaseDatabase.getInstance().getReference().child("DriverAvailable");
        GeoFire geoFire=new GeoFire(driverLocation);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(accidentSpot.latitude,accidentSpot.longitude),radius);
        Toast.makeText(getApplicationContext(), "Looking For Drivers within "+radius+ " km", Toast.LENGTH_SHORT).show();
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound&&!Drivers.contains(key)) {
                    driverFound = true;
                    driverFoundID = key;
                    Drivers.add(driverFoundID);
                    Toast.makeText(context, "Driver Found", Toast.LENGTH_SHORT).show();
                    final DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
                    final String RiderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                   /* HashMap map=new HashMap();
                    map.put("RequestId",RiderId);
                    map.put("Status","Requested");*/

                    Ref.child("CurrentRequest").child(RiderId);
                    Ref.child("CurrentRequest").child(RiderId).child("Status").setValue("Requested");
                    /* Ref.updateChildren(map);*/
                    handler=new Handler();
                 Toast.makeText(context, "Waiting To Respond", Toast.LENGTH_SHORT).show();
                   handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Checking Response", Toast.LENGTH_SHORT).show();
                            final DatabaseReference statRef = FirebaseDatabase.getInstance().getReference();
                            statRef.child("Users").child("Drivers").child(driverFoundID).child("CurrentRequest").child(RiderId).child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    status = snapshot.getValue().toString();
                                    Toast.makeText(context, "Checking response", Toast.LENGTH_SHORT).show();
                                    if (status.equals("Accepted")) {
                                        Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
                                        getDriverLocation();
                                    }
                                    if (status.equals("Declined")) {
                                        driverFound = false;
                                        Toast.makeText(context, "Declined", Toast.LENGTH_SHORT).show();
                                        statRef.child("Users").child("Drivers").child(driverFoundID).child("CurrentRequest").child(RiderId).child("Status").removeValue();
                                        onGeoQueryReady();
                                    }
                                    if (status.equals("Requested")) {
                                        driverFound = false;
                                        Toast.makeText(context, "No response", Toast.LENGTH_SHORT).show();
                                        statRef.child("Users").child("Drivers").child(driverFoundID).child("CurrentRequest").child(RiderId).child("Status").removeValue();
                                        onGeoQueryReady();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    },15000);





                }

            }


            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!driverFound){
                    if(radius<10){
                        radius++;
                        closestDriver();}
                    else{
                        noDriverFound();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }
    public void noDriverFound(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Requests").child(userid);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests");
                    ref.child(userid).removeValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Intent intent=new Intent(this,NoDriverFound.class);
        startActivity(intent);
        finish();
        return;
    }
    public void getDriverLocation(){
        DatabaseReference driverLocationRef=FirebaseDatabase.getInstance().getReference().child("DriverAvailable").child(driverFoundID).child("l");
        driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double locLat = 0;
                    double locLong = 0;
                    if (map.get(0) != null) {
                        locLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locLong = Double.parseDouble(map.get(1).toString());
                    }
                    Toast.makeText(getApplicationContext(),"Getting Driver Location",Toast.LENGTH_SHORT).show();

                    driverLatLng = new LatLng(locLat, locLong);
                    if(mDriverMarker!=null){
                        mDriverMarker.remove();
                    }
                    mDriverMarker=mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Driver Here"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}