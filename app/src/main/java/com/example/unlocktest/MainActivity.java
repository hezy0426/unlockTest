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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

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

        enable = findViewById(R.id.enable);
        lock = findViewById(R.id.lock);

        deviceManger = (DevicePolicyManager) getSystemService(Context. DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, DeviceAdmin.class);
        PowerManager powerManager =(PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");


        if(deviceManger.isAdminActive(compName))
            enable.setText("Disable");
        else
            enable.setText("Enable");

        enable.setOnClickListener(v -> {
        enablePhone(enable);
        });

        lock.setOnClickListener(v -> {
            wakeLock.acquire();

            lockPhone(lock);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   Intent intent = new Intent(MainActivity.this,MainActivity2.class);

                   MainActivity.this.startActivity(intent);

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
    public void lockPhone (View view) {
        deviceManger .lockNow() ;
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