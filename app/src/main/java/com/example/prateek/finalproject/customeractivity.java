package com.example.prateek.finalproject;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class customeractivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    FirebaseFirestore db;
    LocationRequest mLocationRequest;
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters
    private final static int GEOFENCE_RADIUS_IN_METERS = 300;
    private final static long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE;
    SharedPreferences sharedPreferences;
    private GoogleMap mMap;
    Button mLogout, mRequest, mSettings;
    private LatLng pickuplocation;
    private Marker pickupmarker;
    private boolean requestBol = false;
    private LinearLayout mDriverInfo;
    private TextView t1, t2;
    private LatLng driverLatLng, destinationLatLng;
    ArrayList<LatLng> MarkerPoints;
    Polyline currentPolyline;
    private GoogleApiClient googleApiClient;
    double currentLatitude, currentLongitude;
    TextView vehicleno;


    // Play services location

    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300193;

    private LocationRequest mLocationRequest1;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    private PendingIntent mGeofencePendingIntent;
    private List<Geofence> mGeofenceList;

    FirebaseDatabase ref;
    GeoFire geoFire;

    Marker mCurrent;
    Button calld;
    String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customeractivity);

        mGeofenceList = new ArrayList<Geofence>();

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resp == ConnectionResult.SUCCESS) {


            createLocationRequest();

            createGeofences(12.932409, 77.600737);




        }

        else
        {
            Toast.makeText(getApplicationContext(),"Not supported",Toast.LENGTH_SHORT).show();
        }







        






    /*
        Geofence geofence = new Geofence.Builder()
                .setRequestId("Google HQ")
                .setCircularRegion(12.932872, 77.907334, GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(1)
                .build();
        GeofencingClient mGeofencingClient = new GeofencingClient(getApplicationContext());*/





        mRequest = (Button) findViewById(R.id.logout1);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        user = sharedPreferences.getString("username","");
        mDriverInfo=(LinearLayout)findViewById(R.id.driverInfo); // info about the customer
        t1=(TextView)findViewById(R.id.dname);
        db = FirebaseFirestore.getInstance();
        ref = FirebaseDatabase.getInstance();
        vehicleno = (TextView) findViewById(R.id.carno);
        destinationLatLng = new LatLng(0.0,0.0);
        MarkerPoints = new ArrayList<LatLng>();
        calld = (Button) findViewById(R.id.calldphone);

        calld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + driverFoundID));//change the number.
                startActivity(callIntent);
            }
        });







        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setUpLocation();
        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol)
                {
                    requestBol=false;

                    geoQuery.removeAllListeners();
                    driverLocationRef.removeEventListener(driverLocationRefListener);

                    if (driverFoundID!=null){
                        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRideId");
                        driverRef.removeValue();
                        driverFoundID=null;
                    }
                    driverFound=false;
                    radius=1;
                    String mobile = sharedPreferences.getString("username", "");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(reference);
                    geoFire.removeLocation(mobile);

                    if (pickupmarker!=null){
                        pickupmarker.remove();
                        mDriverMarker.remove();
                        driverLocationRef.removeEventListener(driverLocationRefListener);
                    }
                    mRequest.setText("Request for Driver");
                    mDriverInfo.setVisibility(View.GONE);
                    t1.setText("");
                    t2.setText("");


                }else {
                    requestBol=true;

                    String mobile = sharedPreferences.getString("username", "");

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(reference);
                    geoFire.setLocation(mobile, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    pickuplocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                   pickupmarker= mMap.addMarker(new MarkerOptions().position(pickuplocation).title("Help here"));

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                    mRequest.setText("FINDING YOUR DRIVER...");


                    getClosestDriver();
                }
            }
        });


    }



    private void callgoogleapi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(connectionCallbacks)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private int radius = 1;
    private boolean driverFound = false;
    String driverFoundID;
    GeoQuery geoQuery;

    private   void  getClosestDriver()
    {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("DriverAvailable");

        GeoFire geoFire = new GeoFire(ref);

        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickuplocation.latitude,pickuplocation.longitude),radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol) {
                    driverFound = true;
                    driverFoundID = key;
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                     DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String customerId = sharedPreferences.getString("username","");
                    HashMap hashMap = new HashMap();
                    hashMap.put("customerRideId",customerId);
                    driverRef.updateChildren(hashMap);
                    DatabaseReference driverRef1 = FirebaseDatabase.getInstance().getReference().child("Time").child(customerId);
                    HashMap hashMap1 = new HashMap();
                    hashMap1.put("dateandtime",date);
                    driverRef1.updateChildren(hashMap1);




                    getDriverLocation();
                    getDriverInfo(driverFoundID);
//                    mRequest.setText("Looking for driver location...");

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
                if (!driverFound){
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }


    private void getDriverInfo(String cid){
        mDriverInfo.setVisibility(View.VISIBLE);
        final DocumentReference contact = db.collection("drivers").document(cid); // change to driver
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                        String strDate = "Current Time : " + mdformat.format(calendar.getTime());
                        String name = document.getString("Name");
                        String vehicle = document.getString("VehicleNo");
                        t1.setText(name);
                        vehicleno.setText(vehicle);

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }














    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
        private void getDriverLocation(){
             driverLocationRef = FirebaseDatabase.getInstance().getReference().child("DriverWorking").child(driverFoundID).child("l");
            driverLocationRefListener=driverLocationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()&& requestBol)
                    {
                        List<Object> map = (List<Object>) dataSnapshot.getValue();
                        double locationLat = 0;
                        double locationLng = 0;
                        if (map.get(0)!=null)
                        {
                            locationLat = Double.parseDouble(map.get(0).toString());

                        }
                        if (map.get(1)!=null)
                        {
                            locationLng = Double.parseDouble(map.get(1).toString());

                        }
                        driverLatLng = new LatLng(locationLat,locationLng);
                        if (mDriverMarker!=null)
                        {
                            mDriverMarker.remove();
                        }

                        Location loc1 = new Location("");
                        loc1.setLatitude(pickuplocation.latitude);
                        loc1.setLongitude(pickuplocation.longitude);

                        Location loc2 = new Location("");
                        loc2.setLatitude(driverLatLng.latitude);
                        loc2.setLongitude(driverLatLng.longitude);

                        final float distance = loc1.distanceTo(loc2);

                        if(distance<100)
                        {
                            mRequest.setText("Driver is here");


                            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O)
                            {
                                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                NotificationManager mNotificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationChannel channel = new NotificationChannel("default",
                                        "YOUR_CHANNEL_NAME",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
                                mNotificationManager.createNotificationChannel(channel);
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                                        .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                                        .setContentTitle("Alert") // title for notification
                                        .setContentText("Driver is here!")// message for notification
                                        .setSound(soundUri) // set alarm sound for notification
                                        .setAutoCancel(true); // clear notification after click
                                Intent intent = new Intent(getApplicationContext(), customeractivity.class);
                                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                mBuilder.setContentIntent(pi);
                                mNotificationManager.notify(0, mBuilder.build());
                            }
                            else
                            {
                                Intent intent = new Intent();
                                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
                                Notification notification = new Notification.Builder(getApplicationContext())
                                        .setContentTitle("Alert")
                                        .setContentText("Driver is here!")
                                        .setSmallIcon(R.drawable.ic_account_box_black_24dp).getNotification();

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0,notification);
                            }


                            final Thread thread = new Thread(){
                                @Override
                                public void run() {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("customerRequest").child(user);
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists())
                                            {

                                            }
                                            else
                                            {
                                                t1.setText("");
                                                vehicleno.setText("");
                                                mDriverInfo.setVisibility(View.GONE);
                                                mDriverMarker.remove();
                                                pickupmarker.remove();
                                                mRequest.setText("REQUEST FOR DRIVER");

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            };
                            thread.run();





                        }else {
                            mRequest.setText("Driver Found!");
                            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O)
                            {
                                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                NotificationManager mNotificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationChannel channel = new NotificationChannel("default",
                                        "YOUR_CHANNEL_NAME",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
                                mNotificationManager.createNotificationChannel(channel);
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                                        .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                                        .setContentTitle("Alert") // title for notification
                                        .setContentText("Driver has been found!")// message for notification
                                        .setSound(soundUri) // set alarm sound for notification
                                        .setAutoCancel(true); // clear notification after click
                                Intent intent = new Intent(getApplicationContext(), customeractivity.class);
                                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                mBuilder.setContentIntent(pi);
                                mNotificationManager.notify(0, mBuilder.build());

                                final Thread thread = new Thread()
                                {
                                    @Override
                                    public void run() {
                                        try
                                        {
                                           sleep(60000);
                                           if (!(distance<distance-100))
                                           {
                                               requestBol=false;

                                               geoQuery.removeAllListeners();
                                               driverLocationRef.removeEventListener(driverLocationRefListener);

                                               if (driverFoundID!=null){
                                                   DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRideId");
                                                   driverRef.removeValue();
                                                   driverFoundID=null;
                                               }
                                               driverFound=false;
                                               radius=1;
                                               String mobile = sharedPreferences.getString("username", "");
                                               DatabaseReference reference = FirebaseDatabase.getInstance().getReference("customerRequest");
                                               GeoFire geoFire = new GeoFire(reference);
                                               geoFire.removeLocation(mobile);

                                               if (pickupmarker!=null){
                                                   pickupmarker.remove();
                                                   mDriverMarker.remove();
                                                   driverLocationRef.removeEventListener(driverLocationRefListener);
                                               }
                                               mRequest.setText("Request for Driver");
                                               mDriverInfo.setVisibility(View.GONE);
                                               t1.setText("");
                                               t2.setText("");
                                           }
                                           else
                                           {

                                           }
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                            }
                            else
                            {
                                Intent intent = new Intent();
                                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
                                Notification notification = new Notification.Builder(getApplicationContext())
                                        .setContentTitle("Alert")
                                        .setContentText("Driver has been found")
                                        .setSmallIcon(R.drawable.ic_account_box_black_24dp).getNotification();

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0,notification);
                            }
                        }


                        mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("youdriver"));


                        MarkerPoints.add(pickuplocation);
                        MarkerPoints.add(driverLatLng);

                        LatLng origin = pickuplocation;
                        LatLng dest = driverLatLng;

                        String url = getUrl(origin, dest);
                        Log.d("onMapClick", url.toString());
                        FetchUrl FetchUrl = new FetchUrl();

                        // Start downloading json data from Google Directions API
                        FetchUrl.execute(url);





                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();

                    }
                }
                break;

        }
    }

    private void setUpLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION

            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();

            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
           /* sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);*/
           /* String mobile = sharedPreferences.getString("username","");
            geoFire.setLocation(mobile, new GeoLocation(latitude, longitude),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (mCurrent != null)
                                mCurrent.remove();
*/
//                             mCurrent = mMap.addMarker(new MarkerOptions()
//                                            .position(new LatLng(latitude,longitude))
//                                            .title("You"));

                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                            mMap.setMyLocationEnabled(true);






                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),12.0f));

                        }
               /*     });


            Log.d("EDMTDEV",String.format("Your location was changed : %f / %f",latitude,longitude));

        }*/
        else
        {
           // Log.d("EDMTDEV","Cannot get your location");
            Toast.makeText(getApplicationContext(),"Cannot get your location",Toast.LENGTH_SHORT).show();

        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else
            {
                Toast.makeText(this,"This device is not supported",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng dangerous_area = new LatLng(12.932409,77.600737);
        mMap.addCircle(new CircleOptions()
                .center(dangerous_area)
                .radius(800)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f)
        );

/*
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(dangerous_area.latitude,dangerous_area.longitude),0.5f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendNotification("EDMTDEV",String.format("%s entered the dangerous area",key));
            }

            @Override
            public void onKeyExited(String key) {
                sendNotification("EDMTDEV",String.format("%s is no longer in the dangerous area",key));

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });*/
    }

   /* private void sendNotification(String title, String content) {

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content);


        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this,MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_xSOUND;

        manager.notify(new Random().nextInt(),notification);
    }*/

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        displayLocation();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        displayLocation();
        startLocationUpdates();


    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }


            // Drawing polyline in the Google Map for the i-th route




        }
    }

    public void createGeofences(double latitude, double longitude) {

        String id = UUID.randomUUID().toString();
        Geofence fence = new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latitude, longitude, 1000) // Try changing your radius
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
        mGeofenceList.add(fence);

    }

    private GeofencingRequest getGeofencingRequest() {

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {

        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

    }


    GoogleApiClient.ConnectionCallbacks connectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (location == null) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, customeractivity.this);


                    } else {
                        //If everything went fine lets get latitude and longitude
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();

                        //createGeofences(currentLatitude, currentLongitude);
                        //registerGeofences(mGeofenceList);
                    }


                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    try{
                        LocationServices.GeofencingApi.addGeofences(
                                mGoogleApiClient,
                                getGeofencingRequest(),
                                getGeofencePendingIntent()
                        ).setResultCallback(new ResultCallback<Status>() {

                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {

                                } else {

                                }
                            }
                        });

                    } catch (SecurityException securityException) {
                        // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.

                    }
                }

                @Override
                public void onConnectionSuspended(int i) {



                }
            };

}
