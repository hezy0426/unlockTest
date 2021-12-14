package com.example.unlocktest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pax.poslink.CommSetting;
import com.pax.poslink.POSLinkAndroid;


public class MainActivity extends AppCompatActivity {
    static final int RESULT_ENABLE = 1 ;
    DevicePolicyManager deviceManger ;
    ComponentName compName ;
    Button enable, lock ;
    PowerManager.WakeLock wakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Set up for poslink
        CommSetting comm = new CommSetting();
        comm.setType("AIDL");
        POSLinkAndroid.init(this, comm);

        enable = findViewById(R.id.enable);
        lock = findViewById(R.id.lock);

        //Needed to lock screen
        deviceManger = (DevicePolicyManager) getSystemService(Context. DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, DeviceAdmin.class);
        PowerManager powerManager =(PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");

        //Set up the button
        if(deviceManger.isAdminActive(compName))
            enable.setText("Disable");
        else
            enable.setText("Enable");

        enable.setOnClickListener(v -> {
        enablePhone(enable);
        });

        lock.setOnClickListener(v -> {
            //Keep the CPU running while screen is off
            wakeLock.acquire();

            //Lock the screen
            deviceManger.lockNow() ;

            //Wait 5 secs before starting the next activity
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   Intent intent = new Intent(MainActivity.this,MainActivity2.class);

                   MainActivity.this.startActivity(intent);

                   //Release cpu
                    wakeLock.release();
                }
            },5000);




        });
    }
    public void enablePhone (View view) {
        boolean active = deviceManger .isAdminActive( compName ) ;
        if (active) {
            deviceManger .removeActiveAdmin( compName ) ;
            enable .setText( "Enable" ) ;
            lock .setVisibility(View. GONE ) ;
        } else {
            Intent intent = new Intent(DevicePolicyManager. ACTION_ADD_DEVICE_ADMIN ) ;
            intent.putExtra(DevicePolicyManager. EXTRA_DEVICE_ADMIN , compName ) ;
            intent.putExtra(DevicePolicyManager. EXTRA_ADD_EXPLANATION , "You should enable the app!" ) ;
            startActivityForResult(intent , RESULT_ENABLE ) ;
        }
    }

    @Override
    protected void onActivityResult ( int requestCode , int resultCode , @Nullable Intent
            data) {
        super .onActivityResult(requestCode , resultCode , data) ;
        switch (requestCode) {
            case RESULT_ENABLE :
                if (resultCode == Activity. RESULT_OK ) {
                    enable .setText( "Disable" ) ;
                    lock .setVisibility(View. VISIBLE ) ;
                } else {
                    Toast. makeText (getApplicationContext() , "Failed!" ,
                            Toast. LENGTH_SHORT ).show() ;
                }
                return;
        }
    }


}