package com.example.prateek.finalproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class Notify extends AppCompatActivity {

    Switch aSwitch;
    TextView textView;
    Long _id;
    private DBManager dbManager;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbManager = new DBManager(this);
        dbManager.open();

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        final String name = intent.getStringExtra("name");
        final String mobile = intent.getStringExtra("mobile");
        final String notif = intent.getStringExtra("notif");
        _id = Long.parseLong(id);

        aSwitch = (Switch) findViewById(R.id.switch1);
        textView =(TextView) findViewById(R.id.textView2);
        if (notif.equals("YES"))
        {
            aSwitch.setChecked(true);
            textView.setText("Contact will be notified");

        }
        else {
            aSwitch.setChecked(false);
            textView.setText("Contact will not be notified");
        }


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (aSwitch.isChecked())
                {
                    dbManager.update(_id,name,mobile,"YES");
                    textView.setText("Contact will be notified");
                }
                else
                {
                    dbManager.update(_id,name,mobile,"NO");
                    textView.setText("Contact will not be notified");
                }
            }
        });
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


            builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to delete this contact ?")
                    .setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbManager.delete(_id);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
