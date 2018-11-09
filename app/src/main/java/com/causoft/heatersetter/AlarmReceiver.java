package com.causoft.heatersetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myServiceIntent = new Intent(context, MyAlarmService.class);
        context.startService(myServiceIntent);
    }
}

