package com.example.prateek.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    Button btn1,btn2;
    EditText mob,pass;
    SharedPreferences preferences;
    FirebaseFirestore db;
    TextView textView;
    String x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        textView=(TextView) findViewById(R.id.textView8);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),forgotpass.class);
                startActivity(intent);
            }
        });

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        btn1 = (Button) findViewById(R.id.button5);
        x = "users";
        btn2 = (Button) findViewById(R.id.button6);
        mob = (EditText) findViewById(R.id.editText);
        pass = (EditText) findViewById(R.id.editText3);
        preferences = getSharedPreferences("user_details",MODE_PRIVATE);
        String type = preferences.getString("type","");
        if (type.equals("driver"))
        {
            btn2.setVisibility(View.GONE);
            x = "drivers";
        }


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nam = mob.getText().toString();
                final String paa = pass.getText().toString();

                if (nam.equals("")) {
                    mob.setError("Please enter a valid mobile number");
                } else if (pass.equals("")) {
                    pass.setError("Please enter a valid password");
                } else {


                    final DocumentReference contact = db.collection(x).document(nam);
                    contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String pass = document.getString("Password");
                                    String email = document.getString("Email");
                                    if (pass.equals(paa)) {
                                        String name = document.getString("Name");

                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("username", nam);
                                        editor.putString("password", paa);
                                        editor.putString("name",name);
                                        editor.putString("email",email);

                                        editor.commit();

                                        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }
            }
        });





        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),register.class);
                startActivity(intent);
            }
        });

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
