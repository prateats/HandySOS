package com.example.prateek.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class forgotpass extends AppCompatActivity {

    Button btn;
    EditText editText;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpass);

        editText=(EditText) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.button);
        db = FirebaseFirestore.getInstance();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mob = editText.getText().toString();
                final DocumentReference contact = db.collection("users").document(mob);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String mobile = editText.getText().toString();

                                Intent intent = new Intent(forgotpass.this, otp.class);
                                intent.putExtra("mobile", mobile);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),"User not found!",Toast.LENGTH_SHORT).show();
                            }
                        } else {

                        }
                    }
                });

            }
        });


    }
}
