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
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    ArrayList markerPoints = new ArrayList();
    Double locationLat, locationLng;
    LatLng pickupLatLng;
    FirebaseFirestore db1;
    LinearLayout mCustomerInfo; // customer info layout
    private TextView t1, t2; // customer name and phone
    String tim;

    SharedPreferences sharedPreferences;
    private GoogleMap mMap;
    Button mLogout, mSettings, mEndRide;
    private String customerId="", customerId1="";
    private boolean isLogginOut = false;

    // Play services location

    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300193;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference ref;
    GeoFire geoFire;

    Marker mCurrent;
    Switch aSwitch;
    Button callPhone;
    Boolean active;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drivermap);

        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo); // info about the customer
        t1 = (TextView) findViewById(R.id.cname);


        mEndRide = (Button) findViewById(R.id.endride);
        db1 = FirebaseFirestore.getInstance();
        aSwitch = (Switch) findViewById(R.id.switch2);
        callPhone = (Button) findViewById(R.id.callphone);

        callPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + customerId));//change the number.
                startActivity(callIntent);
            }
        });






        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);

        final String driver = sharedPreferences.getString("username","");

        final DatabaseReference time = FirebaseDatabase.getInstance().getReference().child("Time").child(customerId1);
        time.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    tim = dataSnapshot.child("dateandtime").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (aSwitch.isChecked()) {
                    active = true;
                    ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
                    geoFire = new GeoFire(ref);
                    setUpLocation();

                    getAssignedCustomer();
                }
                else {
                    active=false;
                    ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
                    geoFire = new GeoFire(ref);
                    geoFire.removeLocation(driver);
                }
            }
        });


        mEndRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
                final String driver = sharedPreferences.getString("username","");


                final DatabaseReference time = FirebaseDatabase.getInstance().getReference().child("Time").child(customerId1);
                time.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            tim = dataSnapshot.child("dateandtime").getValue(String.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });








                final DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driver).child("customerRideId");
                assignedCustomerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            customerId1 = dataSnapshot.getValue(String.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });









                final DatabaseReference addtohistoryref = FirebaseDatabase.getInstance().getReference().child("History");
                addtohistoryref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if (dataSnapshot.exists()||!dataSnapshot.exists())
                      {

                          Driver driver1 = new Driver(driver,locationLat.toString(),locationLng.toString(),tim);
                          addtohistoryref.child(customerId1).child(driver).push().setValue(driver1);
                         /*addtohistoryref.child(customerId1).child("Driver").setValue(driver);
                         addtohistoryref.child(customerId1).child("Driver").child("PickupLat").setValue(locationLat);
                        addtohistoryref.child(customerId1).child("Driver").child("PickupLong").setValue(locationLng);*/
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("customerRequest");
                GeoFire geoFire = new GeoFire(reference1);
                geoFire.removeLocation(customerId1);




                final DatabaseReference deleteConnref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driver).child(customerId1);
                deleteConnref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            deleteConnref.removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }


    private void getAssignedCustomer(){
        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
        String driverId = sharedPreferences.getString("username","");
        final DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                        customerId = dataSnapshot.getValue().toString();
                  //     Toast.makeText(getApplicationContext(),customerId,Toast.LENGTH_LONG).show();
                    getAssignedCustomerInfo(customerId);  // info about the customer
                    getAssignedCustomerPickupLocation();

                }else {

                    customerId="";
                    if (pickUpMarker!=null)
                    {
                        pickUpMarker.remove();
                    }
                    if (assignedCustomerPickupLocationRefListener!=null) {
                        assignedCustomerRef.removeEventListener(assignedCustomerPickupLocationRefListener);
                        mCustomerInfo.setVisibility(View.GONE);
                        t1.setText("");

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

        private void getAssignedCustomerInfo(String cid)
    {
        mCustomerInfo.setVisibility(View.VISIBLE);
        final DocumentReference contact = db1.collection("users").document(cid);
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("Name");
                        t1.setText(name);


                      /*  Location loc1 = new Location("");
                        loc1.setLatitude(pickupLatLng.latitude);
                        loc1.setLongitude(pickupLatLng.longitude);

                        Location loc2 = new Location("");
                        loc2.setLatitude(mLastLocation.getLatitude());
                        loc2.setLongitude(mLastLocation.getLongitude());

                        float lol = loc2.distanceTo(loc1);

                        if (lol<100)
                        {
                            mEndRide.setVisibility(View.VISIBLE);
                        }*/

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
                                    .setContentText("User has been assigned!")// message for notification
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
                                    .setContentText("User has been assigned!")
                                    .setSmallIcon(R.drawable.ic_account_box_black_24dp).getNotification();

                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(0,notification);
                        }
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                    }
                }
        });
    }



    Marker pickUpMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;
    private void getAssignedCustomerPickupLocation(){
         assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustomerPickupLocationRefListener=assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                     locationLat = 0.0;
                     locationLng = 0.0;
                    if (map.get(0)!=null)
                    {
                        locationLat = Double.parseDouble(map.get(0).toString());

                    }
                    if (map.get(1)!=null)
                    {
                        locationLng = Double.parseDouble(map.get(1).toString());

                    }
                    LatLng pickupLatLng = new LatLng(locationLat,locationLng);


                    pickUpMarker=mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ambu)));

                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getRouteToMarker(LatLng pickup)
    {

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
            sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
            String mobile = sharedPreferences.getString("username","");
            geoFire.setLocation(mobile, new GeoLocation(latitude, longitude),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (mCurrent != null)
                                mCurrent.remove();

//                             mCurrent = mMap.addMarker(new MarkerOptions()
//                                            .position(new LatLng(latitude,longitude))
//                                            .title("You"));

                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                            mMap.setMyLocationEnabled(true);






                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),5.0f));

                        }
                    });


            Log.d("EDMTDEV",String.format("Your location was changed : %f / %f",latitude,longitude));

        }
        else
        {
            Log.d("EDMTDEV","Cannot get your location");

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
/*
        LatLng dangerous_area = new LatLng(12.9330503,77.6063177);
        mMap.addCircle(new CircleOptions()
            .center(dangerous_area)
            .radius(500)
            .strokeColor(Color.BLUE)
            .fillColor(0x220000FF)
            .strokeWidth(5.0f)
        );


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
        notification.defaults |= Notification.DEFAULT_SOUND;

        manager.notify(new Random().nextInt(),notification);
    }*/

    @Override
    public void onLocationChanged(Location location) {
      if (getApplicationContext()!=null)
      {
          mLastLocation = location;

          LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
          mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
          mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
          String userId = sharedPreferences.getString("username","");
          DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("DriverAvailable");
          DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("DriverWorking");
         GeoFire geoFireAvailable = new GeoFire(refAvailable);
          GeoFire geoFireWorking = new GeoFire(refWorking);




         switch (customerId)
          {
              case "":
                  if (active) {
                      geoFireWorking.removeLocation(userId);
                      geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                  }
                  break;

              default:

                  geoFireAvailable.removeLocation(userId);
                  geoFireWorking.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));

                  break;
          }



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



    }








