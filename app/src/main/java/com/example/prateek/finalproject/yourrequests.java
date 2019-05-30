package com.example.prateek.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class yourrequests extends AppCompatActivity {


    TextView dr;
    ListView listView;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
   // ArrayList<String> usernames = new ArrayList<>() ;
    List<Driver> driverList;


   // final int[] to = new int[]{R.id.id1, R.id.title1, R.id.desc1};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yourrequests);




        dr = (TextView) findViewById(R.id.drivernum);
        sharedPreferences = getSharedPreferences("customer_details",MODE_PRIVATE);
        String usr = sharedPreferences.getString("username","");

        driverList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list111);




        databaseReference = FirebaseDatabase.getInstance().getReference().child("History").child(usr);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot driverSnapshot : dataSnapshot.getChildren())
                {
                    final String key = driverSnapshot.getKey();
                    Toast.makeText(getApplicationContext(),key,Toast.LENGTH_SHORT).show();

                    databaseReference.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dr : dataSnapshot.getChildren())
                            {
                           //     String key2 = dr.getKey();


                            //        Driver driver = dr.getValue(Driver.class);
                            //    driverList.add(driver);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

                DriverList driverList1 = new DriverList(yourrequests.this,driverList);
                listView.setAdapter(driverList1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),detailhistory.class);
                startActivity(intent);



            }
        });
    }
}
