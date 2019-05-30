package com.example.prateek.finalproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.prateek.finalproject.SimpleGestureFilter.SimpleGestureListener;

public class Main4Activity extends AppCompatActivity implements SimpleGestureListener,LocationListener {


    LocationManager locationManager;
    private SimpleGestureFilter detector;

    FloatingActionButton fb;
    ListView listView;
    static final int RESULT_PICK_CONTACT = 1;

    private DBManager dbManager;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private SimpleCursorAdapter adapter;

    AlertDialog.Builder builder;


    final String[] from = new String[]{DatabaseHelper._ID,
            DatabaseHelper.NAME, DatabaseHelper.MOB};

    final int[] to = new int[]{R.id.id, R.id.title, R.id.desc};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_blank);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    99);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        detector = new SimpleGestureFilter(Main4Activity.this, Main4Activity.this);

        listView = (ListView) findViewById(R.id.list);

        dbManager = new DBManager(this);
        dbManager.open();
        final Cursor cursor = dbManager.fetch();


        adapter = new SimpleCursorAdapter(this, R.layout.activity_main4, cursor, from, to, 0);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView idTextView = (TextView) view.findViewById(R.id.id);
                TextView nameTextView = (TextView) view.findViewById(R.id.title);
                TextView descTextView = (TextView) view.findViewById(R.id.desc);
                String id1 = idTextView.getText().toString();
                String id2 = nameTextView.getText().toString();
                String id3 = descTextView.getText().toString();
                String n = "";


                Cursor cursor2 = dbManager.fetch();
                if (cursor2.moveToFirst()) {
                    while (!cursor2.isAfterLast()) {
                        String i = cursor2.getString(0);
                        if (i.equals(id1)) {
                            n = cursor2.getString(3);
                            break;
                        }
                        cursor2.moveToNext();
                    }
                }

                cursor2.close();


                Intent intent = new Intent(getApplicationContext(), Notify.class);
                intent.putExtra("id", id1);
                intent.putExtra("name", id2);
                intent.putExtra("mobile", id3);
                intent.putExtra("notif", n);
                startActivity(intent);
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    Cursor cursor = null;
                    try {
                        String phoneNo = null;
                        String name = null;

                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        phoneNo = cursor.getString(phoneIndex);
                        name = cursor.getString(nameIndex);
                        dbManager.insert(name, phoneNo, "NO");

                        Intent intent = new Intent(getApplicationContext(), Main4Activity.class);
                        startActivity(intent);
                        finish();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Cannot fetch", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {

            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(i, RESULT_PICK_CONTACT);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onLocationChanged(Location location) {
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

    @Override
    public void onSwipe(int direction) {

    }

    //Toast shown when double tapped on screen
    @Override
    public void onDoubleTap() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm!");
        alertDialog.setMessage("Are you sure that you want to send the SMS? ");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main4Activity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);
                    return;
                }

                Cursor cursor1 = dbManager.fetch();
                if (cursor1.moveToFirst()) {
                    while (!cursor1.isAfterLast()) {
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Double lat = location.getLatitude();
                        Double longi = location.getLongitude();
                        String loc = "http://maps.google.com/maps?q="+lat+","+longi+"&z=17";
                        String number = cursor1.getString(2);
                        String notif = cursor1.getString(3);
                        if (notif.equals("YES")) {

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(number, null,"I am involved in an accident and I need help. Please find me at this location. "+ loc , null, null);
                        }
                        cursor1.moveToNext();
                    }
                }

                cursor1.close();
                Toast.makeText(getApplicationContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();




    }


    protected void sendSMSMessage() {



    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }






}

