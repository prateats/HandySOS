package com.example.prateek.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class loginas extends AppCompatActivity {

    ImageView user,driver;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginas);

        driver = (ImageView) findViewById(R.id.imageView3);
        user = (ImageView) findViewById(R.id.imageView4);
        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("type","driver");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),login.class);
                startActivity(intent);

            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("type","user");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),login.class);
                startActivity(intent);


            }
        });
    }
}
