package com.navya.soonapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import java.util.Timer;
import java.util.TimerTask;

public class CancelRequestActivity extends AppCompatActivity {
    TextView count;
    Timer timer;
    CountDownTimer countDownTimer;
    Location location;
    long timeleft=6000;
    Button cancelbutton;
    Boolean cancel;
    Toolbar toolbar;
    RippleBackground ripple;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_request);
        ripple=(RippleBackground)findViewById(R.id.ripple);
        ripple.startRippleAnimation();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        ripple=(RippleBackground)findViewById(R.id.ripple);
        ripple.startRippleAnimation();
        toolbar=(Toolbar)findViewById(R.id.tool_bar1);
        count=(TextView)findViewById(R.id.count);
        cancelbutton=(Button) findViewById(R.id.greenb);
        cancel=false;
        setSupportActionBar(toolbar);

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel=true;
                Intent intent=new Intent(CancelRequestActivity.this,RiderHomeActivity.class);
                startActivity(intent);
                ripple.stopRippleAnimation();

            }
        });

        starttimer();
        timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(cancel==false){
                    Intent intent=new Intent(CancelRequestActivity.this,RiderMapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;}

            }
        },6000);
    }
    public void starttimer(){
        countDownTimer=new CountDownTimer(timeleft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeleft=millisUntilFinished;
                updateTime();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    public void updateTime(){
        int seconds=(int)timeleft/1000;
        String timelefttext;
        timelefttext= String.format("00:00:0%d", seconds);
        count.setText(timelefttext);
    }
}