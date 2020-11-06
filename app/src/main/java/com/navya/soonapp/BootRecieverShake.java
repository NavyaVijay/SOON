package com.navya.soonapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootRecieverShake extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent = new Intent(context,ShakeService.class);
        context.startService(intent);
    }
}
