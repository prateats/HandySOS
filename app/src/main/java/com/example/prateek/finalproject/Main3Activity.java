package com.example.prateek.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Main3Activity extends AppCompatActivity {

    TextView name,email,number,password;
    SharedPreferences preferences;
    EditText editText;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        name = (TextView) findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        number = (TextView) findViewById(R.id.phone);
        button = (Button) findViewById(R.id.editpass);
      //  password = (TextView) findViewById(R.id.password);
        preferences = getSharedPreferences("user_details",MODE_PRIVATE);

        String name1 = preferences.getString("name","");
        String email1 = preferences.getString("email","");
        String mob = preferences.getString("username","");
      //  final String pass = preferences.getString("password","");




        name.setText(name1);
        email.setText(email1);
        number.setText(mob);
       // password.setText(pass);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(),changepass.class);
              startActivity(intent);

            }
        });



    }
}
