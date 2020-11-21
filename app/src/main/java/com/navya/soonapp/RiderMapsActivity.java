package com.navya.soonapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RiderMapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,RoutingListener {
    double locLat = 0;
    double locLong = 0;
    Boolean first=true;
    boolean clicked = true;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LatLng accidentSpot,driverLatLng;
    Timer timer;
    Handler handler;
    Button driverdetails,routedetails;
    int radius=0;
    Boolean driverFound=false;
    String driverFoundID;
    Marker mDriverMarker,mRiderMarker;
    String status;
    Context context;
    ArrayList<String> Drivers;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark,R.color.colorPrimary,R.color.colorAccent,R.color.primary_dark_material_light};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        context=getApplicationContext();
        Drivers=new ArrayList<String>();
        polylines = new ArrayList<>();
        routedetails=(Button)findViewById(R.id.mapbtn);
        driverdetails=(Button)findViewById(R.id.driverbtn);
        driverdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverDetails(driverFoundID);
            }
        });
        routedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showroute(accidentSpot,driverLatLng);
            }
        });
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
    public void onLocationChanged(final Location location) {

        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        onclick();
        if(first==true){
            first=false;
            closestDriver();
           //  sendsmsyourself(location);
        }
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
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    public void onclick() {
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests");
        GeoFire geofire = new GeoFire(ref);
        geofire.setLocation(userid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        accidentSpot = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        if(mRiderMarker!=null)
        {   mRiderMarker.remove();}
        mRiderMarker=mMap.addMarker(new MarkerOptions().position(accidentSpot).title("I am here!!"));


    }
    public void sendsmsyourself(final Location location){
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("Users").child("Riders").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String e1=snapshot.child("EmergencyContact1").getValue().toString();
                String e2=snapshot.child("EmergencyContact2").getValue().toString();
                String lat=String.valueOf(location.getLatitude());
                String lng=String.valueOf(location.getLongitude());
                String msg="Help!! I have met with an accident at"+"\n"+"http://maps.google.com/?q="+lat+","+lng+"\n-via SOON App";
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage(e1,null,msg,null,null);
                smsManager.sendTextMessage(e2,null,msg,null,null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void closestDriver(){
        DatabaseReference driverLocation=FirebaseDatabase.getInstance().getReference().child("DriverAvailable");
        GeoFire geoFire=new GeoFire(driverLocation);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(accidentSpot.latitude,accidentSpot.longitude),radius);
        Toast.makeText(context, "Looking For Drivers Nearby", Toast.LENGTH_SHORT).show();
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

                    Ref.child("CurrentRequest").child(RiderId);
                    Ref.child("CurrentRequest").child(RiderId).child("Status").setValue("Requested");

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

                    /*      */



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
        Intent intent=new Intent(RiderMapsActivity.this,NoDriverFound.class);
        startActivity(intent);
        finish();
        return;
    }
    public void getDriverLocation(){
        driverdetails.setVisibility(View.VISIBLE);
        routedetails.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),"Getting Driver Location",Toast.LENGTH_SHORT).show();
        DatabaseReference driverLocationRef=FirebaseDatabase.getInstance().getReference().child("DriverAvailable").child(driverFoundID).child("l");
        driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Object> map = (List<Object>) snapshot.getValue();

                    if (map.get(0) != null) {
                        locLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locLong = Double.parseDouble(map.get(1).toString());
                    }

                    driverLatLng = new LatLng(locLat, locLong);
                    if(mDriverMarker!=null){
                        mDriverMarker.remove();
                    }
                    mDriverMarker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(driverLatLng).title("Driver Here"));
                    MarkerOptions markerOptions=new MarkerOptions();

                  //  showroute(accidentSpot,driverLatLng);
                   // Toast.makeText(getApplicationContext(), ""+driverLatLng, Toast.LENGTH_LONG).show();
                    //   getRouteToMarker(driverLatLng);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


       //
    }
    private void showroute(LatLng accidentSpot,LatLng driverLatLng){
        Uri uri=Uri.parse("https://www.google.co.in/maps/dir/"+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+"/"+locLat+","+locLong);
        Intent intent =new Intent(Intent.ACTION_VIEW,uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void getRouteToMarker(LatLng driverLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),driverLatLng)
                .key("AIzaSyD_ypfo7ZNQMEhg5OXo9WjTpjuKKRlWYPg")
                .build();
        routing.execute();
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestrouteindex) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }
    private void eraseroute(){
        for(Polyline line:polylines){
            line.remove();
        }
        polylines.clear();
    }
    public void  DriverDetails(final String driverFoundID){

        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Driver Details");
      DatabaseReference dref=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String  name =snapshot.child("Name").getValue().toString();
                    String  addr =snapshot.child("Address").getValue().toString();
                    String  phno =snapshot.child("Phoneno").getValue().toString();
                     String vhno =snapshot.child("VehicleNo").getValue().toString();

                    dialog.setMessage("Name: "+ name +"\nAddress: "+ addr +"\nPhone Number: "+ phno +"\nVehicle Number: "+ vhno);
                    dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog1=dialog.create();
                    dialog1.show();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}