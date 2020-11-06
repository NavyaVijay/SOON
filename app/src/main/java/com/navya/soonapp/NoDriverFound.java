package com.navya.soonapp;


import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;


public class NoDriverFound extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_no_driver_found);
        toolbar=(Toolbar)findViewById(R.id.tool_bar2);
        setSupportActionBar(toolbar);
    }
}