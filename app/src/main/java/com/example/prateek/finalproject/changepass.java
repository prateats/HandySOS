package com.example.prateek.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class changepass extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText currentpass,newpass,confirmnewpass;
    Button change;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);

        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
        currentpass = (EditText) findViewById(R.id.current);
        newpass = (EditText) findViewById(R.id.newp);
        confirmnewpass = (EditText) findViewById(R.id.confirmnew);
        final String mobi = sharedPreferences.getString("username","");
        final String checkcurrentpass = sharedPreferences.getString("password","");
        change=(Button) findViewById(R.id.changebutton);

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        db = FirebaseFirestore.getInstance();


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference users1 = db.collection("users").document(mobi);
                users1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if (task.isSuccessful())
                       {
                           DocumentSnapshot documentSnapshot = task.getResult();
                           if (documentSnapshot.exists())
                           {
                               String pass = documentSnapshot.getString("Password");
                               String passc = currentpass.getText().toString();

                               String newpass1 = newpass.getText().toString();
                               String newpass2 = confirmnewpass.getText().toString();
                               if (passc.equals(pass)&&newpass1.equals(newpass2)&&newpass1.matches(PASSWORD_PATTERN))
                               {
                                   users1.update("Password",newpass1);
                                   SharedPreferences.Editor editor = sharedPreferences.edit();
                                   editor.clear();
                                   editor.commit();

                                   Intent i = new Intent(getApplicationContext(), login.class);        // Specify any activity here e.g. home or splash or login etc
                                   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                   i.putExtra("EXIT", true);
                                   startActivity(i);
                                   finish();
                               }
                               else {
                                   Toast.makeText(getApplicationContext(),"The password must contain minimum 8 characters and atleast 1 upper case alphabet, 1 lower case alphabet, 1 number and 1 special character",Toast.LENGTH_SHORT).show();
                               }

                           }
                           else {
                               Toast.makeText(getApplicationContext(),"User does not exist!",Toast.LENGTH_SHORT).show();

                           }
                       }
                       else {
                           Toast.makeText(getApplicationContext(),"Internal Error!",Toast.LENGTH_SHORT).show();

                       }
                    }
                });
            }
        });

    }
}

