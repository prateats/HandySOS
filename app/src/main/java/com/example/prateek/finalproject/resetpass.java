package com.example.prateek.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class resetpass extends AppCompatActivity {

    EditText pass,confirmpass;
    Button button;
    FirebaseFirestore db;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpass);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        final String mobile = intent.getStringExtra("mobile");
        confirmpass = (EditText) findViewById(R.id.editText3);
        pass = (EditText) findViewById(R.id.editText8);
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";

        button = (Button) findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final DocumentReference contact = db.collection("users").document(mobile);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String dbpass = document.getString("Password");
                                if (pass.getText().toString().equals(confirmpass.getText().toString())) {
                                    if (pass.getText().toString().matches(PASSWORD_PATTERN))
                                    {
                                        if (!dbpass.equals(pass))
                                        {
                                    contact.update("Password", pass.getText().toString());
                                    Intent intent1 = new Intent(getApplicationContext(), login.class);
                                    startActivity(intent1);
                                    finish();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(),"Password cannot be same as previous password!",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"The password must contain minimum 8 characters and atleast 1 upper case alphabet, 1 lower case alphabet, 1 number and 1 special character",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                else {
                                    Toast.makeText(getApplicationContext(),"Passwords do not match",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),"User not found!",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Internal Error!",Toast.LENGTH_SHORT).show();

                        }
                    }
                });



            }
        });

    }
    @Override
    public  void  onBackPressed(){
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to quit?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Quit");
        alert.show();

    }
}
