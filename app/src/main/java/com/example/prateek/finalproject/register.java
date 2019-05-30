package com.example.prateek.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

public class  register extends AppCompatActivity {

    EditText name1,email,password,mobile,age;
    Button button;
    RadioButton r1,r2;
    FirebaseFirestore db;

    //our database reference object

    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        name1 = (EditText) findViewById(R.id.editText2);
        email = (EditText) findViewById(R.id.editText5);
        password = (EditText) findViewById(R.id.editText6);
        mobile = (EditText) findViewById(R.id.editText4);
        age = (EditText) findViewById(R.id.editText9);
        r1 = (RadioButton) findViewById(R.id.radioButton);
        r2 = (RadioButton) findViewById(R.id.radioButton2);
        button = (Button) findViewById(R.id.button7);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String n, e, p, m, aa, r;
                final Integer a;
                String regx = "^[\\p{L} .'-]+$";
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
                String mobil = "[6-9][0-9]{9}";

                n = name1.getText().toString();
                e = email.getText().toString();
                p = password.getText().toString();
                m = mobile.getText().toString();
                aa = age.getText().toString();

                password.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage("Do you want to delete this contact ?")
                                .setCancelable(false)
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(getApplicationContext(),Main4Activity.class);
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
                        alert.setTitle("Confirm Delete");
                        alert.show();

                    }
                });

                if (n.isEmpty() || e.isEmpty() || p.isEmpty() || m.isEmpty() || aa.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "One or more of the fields are left blank", Toast.LENGTH_SHORT).show();
                } else {
                    a = Integer.parseInt(aa);
                 if (!n.matches(regx)) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                        name1.setError("Please enter a valid name");
                    } else if (!e.matches(emailPattern)) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                        email.setError("Please enter a valid email");
                    } else if (!p.matches(PASSWORD_PATTERN)) {
                        Toast.makeText(getApplicationContext(), "The password must contain minimum 8 characters and atleast 1 upper case alphabet, 1 lower case alphabet, 1 number and 1 special character", Toast.LENGTH_LONG).show();
                        password.setError("Please enter a valid password");
                 } else if (!m.matches(mobil)) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                        mobile.setError("Please enter a valid mobile number");
                     } else if (!r1.isChecked() && !r2.isChecked()) {
                        Toast.makeText(getApplicationContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
                    } else if (a < 12 || a > 100) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid age", Toast.LENGTH_SHORT).show();
                        age.setError("Please enter a valid age");
                 } else {
                        if (r1.isChecked()) {
                            r = "Male";
                        } else {
                            r = "Female";
                        }


                     final DocumentReference contact = db.collection("users").document(m);
                     contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                             if (task.isSuccessful()) {
                                 DocumentSnapshot document = task.getResult();
                                 if (document.exists()) {
                                     Toast.makeText(getApplicationContext(),"User already exists",Toast.LENGTH_LONG).show();
                                 } else {
                                     Map< String, Object > newUser = new HashMap< >();
                                     newUser.put("Name", n);
                                     newUser.put("Email", e);
                                     newUser.put("Password", p);
                                     newUser.put("Age",a);
                                     newUser.put("Gender",r);

                                     db.collection("users").document(m).set(newUser)
                                             .addOnSuccessListener(new OnSuccessListener< Void >() {
                                                 @Override
                                                 public void onSuccess(Void aVoid) {
                                                     Toast.makeText(getApplicationContext(), "User Registered",
                                                             Toast.LENGTH_SHORT).show();
                                                     Intent intent = new Intent(getApplicationContext(),login.class);
                                                     startActivity(intent);
                                                     finish();
                                                 }
                                             })
                                             .addOnFailureListener(new OnFailureListener() {
                                                 @Override
                                                 public void onFailure(@NonNull Exception e) {
                                                     Toast.makeText(getApplicationContext(), "ERROR" + e.toString(),
                                                             Toast.LENGTH_SHORT).show();

                                                 }
                                             });
                                 }
                             } else {
                                 Toast.makeText(getApplicationContext(),"Internal error",Toast.LENGTH_LONG).show();
                             }
                         }
                     });



                    }
                }
            }
        });



    }


}
