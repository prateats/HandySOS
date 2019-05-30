package com.example.prateek.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try
                {
                    sleep(3000);
                    sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
                   String uname = sharedPreferences.getString("username","");
                    if (uname.equals(""))
                    {
                        Intent intent = new Intent(getApplicationContext(),loginas.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                        startActivity(intent);
                        finish();
                    }


                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
