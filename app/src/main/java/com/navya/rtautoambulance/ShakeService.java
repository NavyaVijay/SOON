package com.navya.rtautoambulance;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

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
        float x=event.values[0];
        float y=event.values[1];
        float z=event.values[2];
        mAccelLast=mAccelCurrent;
        mAccelCurrent=(float)Math.sqrt((double)(x*x+y*y+z*z));
        float delta=mAccelCurrent-mAccelLast;
        mAccel=mAccel*0.9f+delta;
        if(mAccel>12){
              showNotification();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void showNotification(){
        final NotificationManager mgr=(NotificationManager)this
                                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder note= new NotificationCompat.Builder(this);
        note.setContentTitle("Device Accelerometer Notification");
        note.setTicker("New Message Alert!");
        note.setAutoCancel(true);
        note.setDefaults(Notification.DEFAULT_ALL);
        note.setSmallIcon(R.drawable.ic_launcher_foreground);
        PendingIntent pi= PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);
        note.setContentIntent(pi);
        mgr.notify(101,note.build());
    }
}
