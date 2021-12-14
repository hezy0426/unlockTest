package com.example.unlocktest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.pax.poslink.CommSetting;
import com.pax.poslink.PaymentRequest;
import com.pax.poslink.PosLink;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Wake up screen
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //Start a transaction
        PosLink pos = new PosLink(MainActivity2.this);
        CommSetting comm = new CommSetting();
        comm.setType("AIDL");

        pos.SetCommSetting(comm);
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = 1;
        pay.TransType = 1;
        pay.ECRRefNum = "122";
        pay.Amount = "100";

        new Thread(new Runnable() {
            @Override
            public void run() {
                pos.PaymentRequest = pay;
                pos.ProcessTrans();
            }
        }).start();
    }
}