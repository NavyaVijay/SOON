package com.navya.soonapp;


import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ShakeService extends Service implements SensorEventListener {
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    float mAccel;
    float mAccelCurrent;
    float mAccelLast;
    public ShakeService() {}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_UI,new Handler());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;
        if (mAccel > 12) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child("Users");
            db.child("Riders");
            db.child(user.getUid());
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Intent intent=new Intent(getApplicationContext(),RiderHomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onDestroy(){
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

}
